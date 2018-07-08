(ns hphelperv2.websockets
  #?@(:clj [(:require
              [taoensso.timbre :as log]
              [compojure.core :refer [GET POST defroutes]]
              [org.httpkit.server :refer [send! with-channel on-close on-receive]]
              [cognitect.transit :as t]

              [taoensso.sente :as sente]
              [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]
              )]
      :cljs [(:require-macros
               [cljs.core.async.macros :as asyncm :refer (go go-loop)])
             (:require
               [taoensso.timbre :as log]
               [taoensso.sente  :as sente :refer (cb-success?)]
               )]))
               
;; Declare basic websocket handlers
#?(:clj
 (do
  (let [{:keys [ch-recv send-fn connected-uids
                ajax-post-fn ajax-get-or-ws-handshake-fn]}
        (sente/make-channel-socket! (get-sch-adapter) {})]

    (def ring-ajax-post                ajax-post-fn)
    (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
    (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
    (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
    (def connected-uids                connected-uids) ; Watchable, read-only atom
    )
  (defroutes websocket-routes
    (GET  "/api/transit/ws" request (ring-ajax-get-or-ws-handshake request))
    (POST "/api/transit/ws" request (ring-ajax-post request))
    )
  (def websocket-routes-wrapped
    (-> websocket-routes
        ring.middleware.keyword-params/wrap-keyword-params
        ring.middleware.params/wrap-params))

  (add-watch connected-uids :connected-uids
             (fn [_ _ old new]
               (when (not= old new)
                 (log/infof "Connected uids change: %s" new))))
  )

 :cljs
 (do
   (let [{:keys [chsk ch-recv send-fn state]}
         (sente/make-channel-socket! "/api/transit/ws" ; Note the same path as before
                                     {:type :auto ; e/o #{:auto :ajax :ws}
                                      })]
     (def chsk       chsk)
     (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
     (def chsk-send! send-fn) ; ChannelSocket's send API fn
     (def chsk-state state)   ; Watchable, read-only atom
     )
  )
 (log/info "Finished setting up websockets")
 )

;; Setup basic message handlers
(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id ; Dispatch on event-id
  )
(defmethod -event-msg-handler
  :chsk/ws-ping
  [{:as ev-msg :keys [id]}]
  (log/trace "Received ping"))


#?(:clj
 (do
   (defmethod -event-msg-handler
     :default ; Default/fallback case (no other matching handler)
     [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
     (let [session (:session ring-req)
           uid     (:uid     session)]
       (log/debugf "Unhandled event: %s" event)
       (when ?reply-fn
         (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))
   (defn event-msg-handler
     "Wraps `-event-msg-handler` with logging, error catching, etc."
     [{:as ev-msg :keys [id ?data event]}]
     (-event-msg-handler ev-msg) ; Handle event-msgs on a single thread
     ;; (future (-event-msg-handler ev-msg)) ; Handle event-msgs on a thread pool
     )
   )
 :cljs
 (do
   (defmethod -event-msg-handler
     :default ; Default/fallback case (no other matching handler)
     [{:as ev-msg :keys [event]}]
     (log/infof "Unhandled event: %s" event))
   (defn event-msg-handler
     "Wraps `-event-msg-handler` with logging, error catching, etc."
     [{:as ev-msg :keys [id ?data event]}]
     (-event-msg-handler ev-msg))
   )) ; End #?

;; Define and start router
(defonce router_ (atom nil))
(defn  stop-router! [] (when-let [stop-fn @router_] (stop-fn)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          ;; Server on the clj, client on the cljs
          (#?(:clj sente/start-server-chsk-router! :cljs sente/start-client-chsk-router!)
            ch-chsk event-msg-handler)))
#?(:clj
 (do
   )
 :cljs
 (do
   (defn start! [] (start-router!))

   (defonce _start-once (start!))
   ))

(comment
  (defn test-fast-server>user-pushes
    "Quickly pushes 100 events to all connected users. Note that this'll be
    fast+reliable even over Ajax!"
    []
    (doseq [uid (:any @connected-uids)]
      (doseq [i (range 100)]
        (log/infof "Sending test message number: %d" i)
        (chsk-send! uid [:fast-push/is-fast (str "hello " i "!!")]))))
  (test-fast-server>user-pushes))

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
    (chsk-send! channel msg)))
(defmulti process-message :ws/type)
(defmethod process-message :default
  [params]
  (log/info "Don't know how to handle incoming message:" (pr-str params)))

