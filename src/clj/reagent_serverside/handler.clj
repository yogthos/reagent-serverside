(ns reagent-serverside.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [reagent-serverside.home :refer [items home-page]]))

(defn react-id-str [react-id]
  (assert (vector? react-id))
  (str "." (clojure.string/join "." react-id)))

(defn set-react-id [react-id element]
  (update element 1 merge {:data-reactid (react-id-str react-id)}))

(defn normalize [component]
  (if (map? (second component))
    component
    (into [(first component) {}] (rest component))))

(defn render
  ([component] (render [0] component))
  ([id component]
   (cond
    (fn? component)
    (render (component))

    (not (coll? component))
    component

    (coll? (first component))
    (map-indexed #(render (conj id %1) %2) component)

    (keyword? (first component))
    (let [[tag opts & body] (normalize component)]
      (->> body
           (map-indexed #(render (conj id %1) %2))
           (into [tag opts])
           (set-react-id id)))

    (fn? (first component))
    (render id (apply (first component) (rest component))))))

(reset! items (range 10))

(def mount-target
  [:div#app (render home-page)])

(def loading-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css (if (env :dev) "css/site.css" "css/site.min.css"))]
    [:body
     mount-target
     (include-js "js/app.js")]]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)

  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults #'routes site-defaults)]
    (if (env :dev) (-> handler wrap-exceptions wrap-reload) handler)))
