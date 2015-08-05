; Copyright © 2013 - 2015 Thomas Schank <DrTom@schank.ch>

(ns drtom.logbug.thrown
  (:require
    [clojure.stacktrace :as stacktrace]
    [clojure.tools.logging :as logging]
    [clojure.string :as string]
    ))

(defn get-cause [tr]
  (try
    (when-let [c (.getCause tr)]
      [c (get-cause c)])
    (catch Throwable _)))

(defn expand-to-seq [^Throwable tr]
  (->> (cond
         (instance? java.sql.SQLException tr) (iterator-seq (.iterator tr))
         :else (flatten [tr (get-cause tr)]))
       (filter identity)))

(defn trace-string-seq [ex]
  (map (fn [e] (with-out-str (stacktrace/print-trace-element e)))
       (.getStackTrace ex)))

;(trace-string-seq (IllegalStateException. "asdfa"))

(def ^:dynamic *ns-filter-regex* #".*")

(defn reset-ns-filter-regex [regex]
  (def ^:dynamic *ns-filter-regex* regex))

;(reset-ns-filter-regex #".*drtom.*logbug.*")

(defn filter-trace-string-seq [ex-seq filter-regex]
  (filter
    #(re-matches filter-regex %)
    ex-seq))

(defn to-string [tr filter-regex]
  (str [(with-out-str (stacktrace/print-throwable tr))
        (filter-trace-string-seq (trace-string-seq tr) filter-regex)]))

;(to-string (IllegalStateException. "Just a demo") #".*")

(defn stringify
  ([^Throwable tr]
   (stringify tr *ns-filter-regex* ", "))
  ([^Throwable tr filter-regex join-str]
   (let [fmap #(to-string % filter-regex)]
     (string/join join-str (map fmap (expand-to-seq tr))))))


;(stringify  (IllegalStateException. "Just a demo", (IllegalStateException. "The Cause")))

;(println (stringify (ex-info "Some error "{:x 42}  (IllegalStateException. "The cause", (IllegalStateException. "The root")))))
