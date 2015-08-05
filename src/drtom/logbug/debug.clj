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
    (hooke/add-hook target-var wrapper-fn)))


;### Remember arguments of last call ##########################################

(defonce ^:private last-arguments (atom {}))

(defn- var-key [target-var]
  (str (-> target-var meta :ns) "/" (-> target-var meta :name)))

(defn wrap-with-remember-last-argument [target-var]
  (let [swap-in (fn [current args]
                  (conj current
                        {(var-key target-var) args}))
        wrapper-fn (fn [ f & args]
                     (swap! last-arguments swap-in args)
                     (apply f args))]
    (hooke/add-hook target-var wrapper-fn)))

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
    (logging/debug "wrapping for debugging: " wrappable)
    (wrap-with-log-debug wrappable)
    (wrap-with-remember-last-argument wrappable)))


;### identity-with-logging ns #################################################

(defn identity-with-logging [ns x]
  (logging/log ns :debug nil x)
  x)

