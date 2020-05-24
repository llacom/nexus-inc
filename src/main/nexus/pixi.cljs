(ns nexus.pixi
  (:require ["pixi.js" :as PIXI]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

(def app-config #js {:width 500 :height 200 :backgroundColor 0x2c3e50})
(defonce app (new (.-Application PIXI) app-config))
(defonce bunnies (atom []))

;; (set! js/window.app app)
;; (set! js/window.PIXI PIXI)

(def assets [["bunny" "https://pixijs.io/examples/examples/assets/bunny.png"]])

(defn not-zero? [x] (not (zero? x)))

(defn update-bunny [{:keys [x y vx vy rotation vrotation] :as bunny} delta]
  (-> bunny
      (assoc :x (+ x (* delta vx)))
      (assoc :y (+ y (* delta vy)))
      (assoc :rotation (+ rotation (* delta vrotation)))))

(defn new-bunny [{:keys [x y vx vy rotation vrotation]
                  :or {x 0 y 0 vx 0 vy 0 rotation 0 vrotation 0}}]
  (let [bunny (new (.-Sprite PIXI)
                   (.. app -loader -resources -bunny -texture))]
    (.. bunny -anchor (set 0.5))
    (set! (.-x bunny) x)
    (set! (.-y bunny) y)
    (set! (.-rotation bunny) rotation)
    {:sprite bunny
     :loaded false
     :x x :vx vx
     :y y :vy vy
     :rotation rotation :vrotation vrotation}))

(defn load-sprite [sprite]
  (.. app -stage (addChild (:sprite sprite)))
  (assoc sprite :loaded true))

(defn update-sprite! [{:keys [sprite x y rotation]}]
  (set! (.-x sprite) x)
  (set! (.-y sprite) y)
  (set! (.-rotation sprite) rotation))

(defn main-loop [delta]
  (swap!
   bunnies
   (fn [bunnies]
     (doall
      (map
       (fn [bunny]
         (let [updated-bunny (cond-> bunny
                               (not (:loaded bunny)) load-sprite
                               :true (update-bunny delta)
                               )]
           (update-sprite! updated-bunny)
           updated-bunny))
       bunnies))))
  #_(js/console.log (count @bunnies))
  43
  )

(defn startup []
  (js/console.log "Starting!")
  (.. app -ticker (add #(main-loop %)))
  ;; (.. app -ticker start)
  ;; (.. app -ticker -count)
  )

(defn load-assets! [app assets cb]
  (doseq [[name uri] assets]
    (.. app -loader (add name uri)))
  (.. app -loader
      (load (fn [_ resources]
              (js/console.log "Loaded resources" resources)
              (cb)))))

(defn init []
  (js/console.log "------------------------------ PIXI INIT")
  (load-assets!
   app assets
   (fn []
     (doseq [x (range 0 50)
             y (range 0 40)]
       (swap! bunnies conj (new-bunny {:vrotation 0.1
                                       :x (+ 40 (* 8 x))
                                       :y (+ 40 (* 3 y))})))
     (startup))))

;; (load-assets! app assets)
;; (startup)

(comment
  (let [bunny (nth @bunnies 3)
        sprite (:sprite bunny)]
    (set! (.-rotation sprite) (:rotation bunny))
    )

  (do
    (doall (map
            #(.. app -stage (removeChild (:sprite %)))
            @bunnies))
    (reset! bunnies []))

  (doseq [x (range 0 50)
          y (range 0 40)]
    (swap! bunnies conj (new-bunny {:vrotation 0.1
                                    :x (+ 40 (* 8 x))
                                    :y (+ 40 (* 3 y))})))

  (js/console.log (count @bunnies))

  (load-assets! app assets)
  
  (-> app
      .-loader
      (.load startup))

  ;; (startup)
  )

(defn Pixi []
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (.appendChild (rdom/dom-node comp) (.-view app)))
    :reagent-render
    (fn [] [:div#pixi])}))
