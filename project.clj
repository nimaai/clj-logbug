(defproject nimaai/logbug "1.4.0"
  :description "Cross-cutting utils for logging and debugging in Clojure."
  :url "https://github.com/nimaai/clj-logbug"
  :license {:name "GNU AFFERO GENERAL PUBLIC LICENSE Version 3"
            :url "http://www.gnu.org/licenses/agpl-3.0.html"}
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-logging-config "1.9.12"]
                 [robert/hooke "1.3.0"]
                 ]
  :repositories [["clojars" {:sign-releases false}]])
