(ns fusecode.client.websocket
  (:require [taoensso.sente  :as sente]))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state))  ; Watchable, read-only atom


;;;; Routing handlers

;; Dispatch on event :id
(defmulti event-msg-handler :id)

(defmethod event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]))

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (comment if (= ?data {:first-open? true})
           (.log js/console "Channel socket successfully established!")
           (.log js/console (str "Channel socket state change: " ?data))))

(defmethod event-msg-handler :default
  [{:as ev-msg :keys [id event]}]
  (.log js/console (str "Unhandled event: " id)))


;; Receive a message
(defmulti chsk-recv (fn [id ?data] id))

(defmethod event-msg-handler :chsk/recv
           [{:as ev-msg :keys [?data]}]
           (chsk-recv (?data 0) (?data 1)))


;; Wrap for logging, catching, etc.:
(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (event-msg-handler ev-msg))


;; Initialization
(def router_ (atom nil))

(defn stop-router! []
  (when-let [stop-f @router_]
    (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router_ (sente/start-chsk-router! ch-chsk event-msg-handler*)))


(defn start! []
  (start-router!))
