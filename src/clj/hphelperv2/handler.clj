(ns hphelperv2.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [hphelperv2.layout :refer [error-page]]
            [hphelperv2.routes.home :refer [home-routes]]
            [compojure.route :as route]
            [hphelperv2.env :refer [defaults]]
            [mount.core :as mount]
            [hphelperv2.middleware :as middleware]
            [clojure.tools.logging :as log]
            [hphelperv2.config :refer [env]]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (doseq [component (:started (mount/start))]
    (log/info component "started")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents)
  (log/info "hphelperv2 has shut down!"))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))
      (route/not-found
        (:body
          (error-page {:status 404
                       :title "page not found"}))))))
