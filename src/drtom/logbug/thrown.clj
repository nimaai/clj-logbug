; Copyright © 2013 - 2015 Thomas Schank <DrTom@schank.ch>

(ns drtom.logbug.thrown
  (:require
    [clojure.stacktrace :as stacktrace]
    [clojure.tools.logging :as logging]
    [clojure.string :as string]
    ))

(defn expand-to-seq [^Throwable tr]
  (cond
    (instance? java.sql.SQLException tr)
    (doall (iterator-seq (.iterator tr)))
    :else [tr]))


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

(defn stringify
  ([^Throwable tr]
   (stringify tr *ns-filter-regex* ", "))
  ([^Throwable tr filter-regex join-str]
   (let [this-tr-str (string/join join-str
                                  (map
                                    (fn [ex]
                                      (logging/debug {:ex ex})
                                      (str [(with-out-str (stacktrace/print-throwable ex))
                                            (filter-trace-string-seq (trace-string-seq ex) filter-regex)]))
                                    (expand-to-seq tr)))]
     (string/join join-str (flatten (conj [this-tr-str]
                                          (map #(stringify % filter-regex join-str)
                                               (try (iterator-seq (.iterator tr))
                                                    (catch Exception _ [])))
                                          (try [(stringify (.getCause tr))]
                                               (catch Exception _ []))))))))


;(stringify  (IllegalStateException. "Just a demo"))

;(println (stringify (ex-info "Some error "{:x 42}  (IllegalStateException. "The cause"))))
