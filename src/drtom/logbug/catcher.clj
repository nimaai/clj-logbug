; Copyright © 2013 - 2015 Thomas Schank <DrTom@schank.ch>

(ns drtom.logbug.catcher
  (:require 
    [clojure.stacktrace :as stacktrace]
    [clojure.tools.logging :as clj-logging]
    [drtom.logbug.thrown] 
    ))

;##############################################################################

(defmacro wrap-with-log [level & expressions]
  `(try
     ~@expressions
     (catch Throwable e#
       (clj-logging/log ~level (drtom.logbug.thrown/stringify e#))
       (throw e#))))

(defmacro wrap-with-log-debug [& expressions]
  `(wrap-with-log :debug ~@expressions))

(defmacro wrap-with-log-info [& expressions]
  `(wrap-with-log :info ~@expressions))

(defmacro wrap-with-log-warn [& expressions]
  `(wrap-with-log :warn ~@expressions))

(defmacro wrap-with-log-error [& expressions]
  `(wrap-with-log :error ~@expressions))


;##############################################################################

(defmacro wrap-with-suppress-and-log [level & expressions]
  `(try
     ~@expressions
     (catch Throwable e#
       (clj-logging/log ~level (drtom.logbug.thrown/stringify e#))
       nil)))

(defmacro wrap-with-suppress-and-log-debug [& expressions]
  `(wrap-with-suppress-and-log :debug ~@expressions))

(defmacro wrap-with-suppress-and-log-info [& expressions]
  `(wrap-with-suppress-and-log :info ~@expressions))

(defmacro wrap-with-suppress-and-log-warn [& expressions]
  `(wrap-with-suppress-and-log :warn ~@expressions))

(defmacro wrap-with-suppress-and-log-error [& expressions]
  `(wrap-with-suppress-and-log :error ~@expressions))




