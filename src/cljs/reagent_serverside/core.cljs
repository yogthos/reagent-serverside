(ns reagent-serverside.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent-serverside.home :refer [items home-page]]))

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (reset! items (range 10))
  (mount-root))
