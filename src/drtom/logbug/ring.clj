; Copyright © 2013 - 2015 Thomas Schank <DrTom@schank.ch>

(ns drtom.logbug.ring
  (:require 
    [clojure.tools.logging :as logging]
    ))

(defn wrap-handler-with-logging 
  "Wraps a handler with logging of 
  request and response to the given namespace."
  ([handler ns]
   (wrap-handler-with-logging handler ns :debug))
  ([handler ns loglevel]
   (fn [request]
     (let [logbug-level (or (:logbug-level request) 0 )]
       (logging/log ns loglevel nil {:logbug-level logbug-level
                                     :request request})
       (let [response (handler 
                        (assoc request :logbug-level (+ logbug-level 1)))]
         (logging/log ns loglevel nil {:logbug-level logbug-level 
                                       :response response})
         response)))))

