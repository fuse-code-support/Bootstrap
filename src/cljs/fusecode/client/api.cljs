(ns fusecode.client.api
  (:require [javelin.core :as j :refer [defc cell cell=]]
            [fusecode.client.websocket :as rpc]))


(defc random nil)

(defmethod rpc/chsk-recv :simple/update
  [id ran]
  (reset! random ran))

(defn get-state []
  (rpc/chsk-send! [:simple/get-random {:resp-id :simple/update}]))


(defn init []
  (rpc/start!)
  (js/setInterval get-state 1000))
