(ns fusecode.ui
  (:require [javelin.core :as j :refer [defc cell cell=]]
            [fusecode.client.api :as api]
            [hoplon.core :refer [link header h1 ul li div img textarea text p br button]]))


(defc css "")

(defc tab-title
  (text "Fusecode ~{api/random}"))

(defc body
  (div :id "inner-remaining" :class "center-div"
         (img :src "/loading.gif")))

(defc page-init (fn []))
