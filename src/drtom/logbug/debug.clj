; Copyright © 2013 - 2015 Thomas Schank <DrTom@schank.ch>

(ns drtom.logbug.debug
  (:require
    [clj-logging-config.log4j :as logging-config]
    [clojure.test]
    [clojure.tools.logging :as logging]
    [robert.hooke :as hooke]
    ))


(logging-config/set-logger! :level :debug)
;(logging-config/set-logger! :level :info)

;### Log arguments and result #################################################

(defn wrap-with-log-debug [target-var]
  (logging/debug "wrapping for debugging: " target-var)
  (let [wrapper-fn (fn [f & args]
                     (logging/log (-> target-var meta :ns)
                                  :debug nil
                                  [(symbol (str (-> target-var meta :name)))
                                   "invoked" {:args args}])
                     (let [res (apply f args)]
                       (logging/log (-> target-var meta :ns)
                                    :debug nil
                                    [(symbol (str (-> target-var meta :name)))
                                     "returns" {:res res}])
                       res))]
    (hooke/add-hook target-var :logbug_wrap wrapper-fn)))

(defn unwrap-with-log-debug [target-var]
  (logging/debug "unwrapping from debugging: " target-var)
  (hooke/remove-hook target-var :logbug_wrap))


;### Remember arguments of last call ##########################################

(defonce ^:private last-arguments (atom {}))

(defn- var-key [target-var]
  (str (-> target-var meta :ns) "/" (-> target-var meta :name)))

(defn wrap-with-remember-last-argument [target-var]
  (logging/debug "wrapping for remember" target-var)
  (let [swap-in (fn [current args]
                  (conj current
                        {(var-key target-var) args}))
        wrapper-fn (fn [ f & args]
                     (swap! last-arguments swap-in args)
                     (apply f args))]
    (hooke/add-hook target-var :logbug_remember wrapper-fn)))

(defn unwrap-with-remember-last-argument [target-var]
  (logging/debug "unwrapping from remember" target-var)
  (hooke/remove-hook target-var :logbug_remember))

(defn get-last-argument [target-var]
  (@last-arguments (var-key target-var)))

(defn re-apply-last-argument [target-var]
  (apply target-var (get-last-argument target-var)))


;### Wrap vars of a whole ns ##################################################

(defn- ns-wrappables [ns]
  (filter #(clojure.test/function? (var-get %))
          (vals (ns-interns ns))))

(defn debug-ns [ns]
  (logging-config/set-logger! (str ns) :level :debug)
  (doseq [wrappable (ns-wrappables ns)]
    (wrap-with-log-debug wrappable)
    (wrap-with-remember-last-argument wrappable)))

(defn undebug-ns [ns]
  (doseq [wrappable (ns-wrappables ns)]
    (unwrap-with-log-debug wrappable)
    (unwrap-with-remember-last-argument wrappable)))

;### identity-with-logging ns #################################################

(defn identity-with-logging [ns x]
  (logging/log ns :debug nil x)
  x)

;### log expression and return its value ######################################

(defn log-debug-and-return [expr]
  (do (logging/debug expr) expr))

(defn log-info-and-return [expr]
  (do (logging/info expr) expr))

(defn log-warn-and-return [expr]
  (do (logging/warn expr) expr))

(defn log-error-and-return [expr]
  (do (logging/error expr) expr))
