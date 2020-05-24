(ns nexus.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]))

(defn app []
  [:h1 "Nexus Inc 2"])

(defn ^:dev/before-load stop []
  nil)

(defn ^:dev/after-load start []
  (rdom/render [app] (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "------------------------------ Init!")
  (start)
  ;; (rf/dispatch [:initialize])
  )
