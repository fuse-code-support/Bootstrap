(ns fusecode.server.random
  (:require [fusecode.server.handler :as handler]))

(defn app [] 
  (handler/start-router!)
  (handler/routes))

(defmethod handler/event-msg-handler :simple/get-random
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (handler/chsk-send! (:client-id ev-msg) [(:resp-id ?data) (rand-int 100)]))
