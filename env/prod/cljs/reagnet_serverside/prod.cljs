(ns reagnet-serverside.prod
  (:require [reagnet-serverside.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
