(ns hphelperv2.websockets
  (:require
    #?@(:clj [[taoensso.timbre :as log]
              [compojure.core :refer [GET defroutes]]
              [org.httpkit.server :refer [send! with-channel on-close on-receive]]
              [cognitect.transit :as t]
              ]
        :cljs [[taoensso.timbre :as timbre]])
    )
  )

(defonce channels (atom #{}))
(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel))
(defn disconnect! [channel status]
  (log/info "channel closed:" status)
  (swap! channels #(remove #{channel} %)))
; TODO change this
(defn notify-clients [msg]
  (doseq [channel @channels]
    (send! channel msg)))
(defmulti process-message :ws/type)
(defmethod process-message :default
  [params]
  (log/info "Don't know how to handle incoming message:" (pr-str params)))

(defroutes websocket-routes
  (GET "/api/transit/ws" request (process-message request)))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(notify-clients %))))

(def websocket-app
  (-> (routes
        websocket-routes
        (wrap-routes home-routes middleware/wrap-csrf)
        base-routes)
      middleware/wrap-base))
