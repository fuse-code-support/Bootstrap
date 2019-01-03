(ns fusecode.server.graphql
  (:require [fusecode.server.handler :as handler]))

(defmethod handler/event-msg-handler :gql/request
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (handler/chsk-send! (:client-id ev-msg) [(:resp-id ?data) (rand-int 100)]))
