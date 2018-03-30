(ns user
  (:require 
            [mount.core :as mount]
            [hphelperv2.figwheel :refer [start-fw stop-fw cljs]]
            [hphelperv2.core :refer [start-app]]))

(defn start []
  (mount/start-without #'hphelperv2.core/repl-server))

(defn stop []
  (mount/stop-except #'hphelperv2.core/repl-server))

(defn restart []
  (stop)
  (start))


