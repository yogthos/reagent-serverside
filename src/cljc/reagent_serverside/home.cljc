(ns reagent-serverside.home
  #?(:cljs
     (:require [reagent.core :as reagent :refer [atom]])))

(def items (atom nil))

(defn item-list [items]
  [:ul
   (for [item items]
     ^{:key item}
     [:li item])])

(defn add-item-button [items]
  [:button
   {:on-click #(swap! items conj (count @items))}
   "add item"])

(defn home-page []
  [:div [:h2 "Welcome to reagent-serverside"]
   [add-item-button items]
   [item-list @items]])
