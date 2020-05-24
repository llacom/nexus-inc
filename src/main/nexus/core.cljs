(ns nexus.core
  (:require [reagent.dom :as rdom]
            [nexus.pixi :as pixi]
            [re-frame.core :as rf]))

(defn app []
  [:div
   [:h1 "Nexus Inc"]
   [pixi/Pixi]])

(defn ^:dev/before-load stop []
  nil)

(defn ^:dev/after-load start []
  (rdom/render [app] (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "------------------------------ Init!")
  (start)
  (pixi/init)
  ;; (rf/dispatch [:initialize])
  )
