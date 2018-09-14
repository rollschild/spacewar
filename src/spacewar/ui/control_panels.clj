(ns spacewar.ui.control-panels
  (:require [quil.core :as q]
            [spacewar.ui.protocols :as p]
            [spacewar.ui.widgets :as w]))

(def banner-width 40)
(def stringer-width 15)
(def button-gap 10)
(def button-h 40)

(defn lcars-points [state]
  (let [{:keys [x y w h]} state
        inverted (:inverted state false)]
    (if inverted
      {:a [(+ x w) y]
       :b [(+ x banner-width) y]
       :c [x (+ y banner-width)]
       :d [x (+ y h)]
       :e [(+ x stringer-width) (+ y h)]
       :f [(+ x stringer-width) (+ y banner-width stringer-width)]
       :g [(+ x stringer-width stringer-width) (+ y banner-width)]
       :h [(+ x w) (+ y banner-width)]
       :c1 [x y]
       :c2 [(+ x stringer-width) (+ y banner-width)]
       :label-position [(+ x w -10) (+ y 10)]}
      {:a [x y]
       :b [(+ x w (- banner-width)) y]
       :c [(+ x w) (+ y banner-width)]
       :d [(+ x w) (+ y h)]
       :e [(+ x w (- stringer-width)) (+ y h)]
       :f [(+ x w (- stringer-width)) (+ y banner-width stringer-width)]
       :g [(+ x w (- stringer-width) (- stringer-width)) (+ y banner-width)]
       :h [x (+ y banner-width)]
       :c1 [(+ x w) y]
       :c2 [(+ x w (- stringer-width)) (+ y banner-width)]
       :label-position [(+ x 10) (+ y 10)]})))

(defn draw-lcars [state]
  (let [inverted (:inverted state false)
        color (:color state)
        {:keys [a b c d e f g h c1 c2 label-position]} (lcars-points state)]
    (q/no-stroke)
    (apply q/fill color)
    (q/begin-shape)
    (apply q/vertex a)
    (apply q/vertex b)
    (apply q/quadratic-vertex (concat c1 c))
    (apply q/vertex d)
    (apply q/vertex e)
    (apply q/vertex f)
    (apply q/quadratic-vertex (concat c2 g))
    (apply q/vertex h)
    (apply q/vertex a)
    (q/end-shape)
    (q/fill 0 0 0)
    (q/text-size 24)
    (q/text-font (:lcars (q/state :fonts)))
    (q/text-align (if inverted :right :left) :top)
    (apply q/text (:name state) label-position)))

(defn draw-bottom-lcars [state]
  (let [{:keys [x y w h name color]} state]
    (q/no-stroke)
    (apply q/fill color)
    (q/rect x (+ y h (- banner-width)) w banner-width)
    (q/fill 0 0 0)
    (q/text-size 24)
    (q/text-font (:lcars (q/state :fonts)))
    (q/text-align :center :center)
    (q/text name (+ x (/ w 2)) (+ y h (/ banner-width -2)))
    )
  )

(deftype scan-panel [state]
  p/Drawable
  (draw [_]
    (draw-lcars state)
    (p/draw-elements state))

  (setup [_]
    (let [{:keys [x y w button-color]} state
          button-w (- w stringer-width 10)
          strategic-y (+ y banner-width button-gap)
          tactical-y (+ strategic-y button-h button-gap)
          front-view-y (+ tactical-y button-h button-gap)]
      (scan-panel.
        (assoc state
          :strategic (p/setup
                       (w/->button
                         {:x x
                          :y strategic-y
                          :w button-w
                          :h button-h
                          :name "STRAT"
                          :color button-color
                          :left-up-event {:event :strategic-scan}}))
          :tactical (p/setup
                      (w/->button
                        {:x x
                         :y tactical-y
                         :w button-w
                         :h button-h
                         :name "TACT"
                         :color button-color
                         :left-up-event {:event :tactical-scan}}))

          :front-view (p/setup
                        (w/->button
                          {:x x
                           :y front-view-y
                           :w button-w
                           :h button-h
                           :name "FRONT"
                           :color button-color
                           :left-up-event {:event :front-view}}))

          :elements [:strategic :tactical :front-view]))))

  (update-state [_ commands]
    (let [[new-state events] (p/update-elements state commands)]
      (p/pack-update
        (scan-panel. new-state)
        events))))

(deftype engine-panel [state]
  p/Drawable
  (draw [_]
    (draw-lcars state)
    (p/draw-elements state))

  (setup [_]
    (let [{:keys [x y button-color]} state
          button-w 150
          warp-y (+ y banner-width button-gap)
          impulse-y (+ warp-y button-h button-gap)
          dock-y (+ impulse-y button-h button-gap)]
      (engine-panel.
        (assoc state
          :warp (p/setup
                  (w/->button
                    {:x x
                     :y warp-y
                     :w button-w
                     :h button-h
                     :name "WARP"
                     :color button-color
                     :left-up-event {:event :select-warp}}))
          :impulse (p/setup
                     (w/->button
                       {:x x
                        :y impulse-y
                        :w button-w
                        :h button-h
                        :name "IMPULSE"
                        :color button-color
                        :left-up-event {:event :select-impulse}}))
          :dock (p/setup
                  (w/->button
                    {:x x
                     :y dock-y
                     :w button-w
                     :h button-h
                     :name "DOCK"
                     :color button-color
                     :left-up-event {:event :select-dock}}))
          :elements [:warp :impulse :dock]))))

  (update-state [_ commands]
    (let [[new-state events] (p/update-elements state commands)]
      (p/pack-update
        (engine-panel. new-state)
        events))))

(deftype weapons-panel [state]
  p/Drawable
  (draw [_]
    (draw-lcars state)
    (p/draw-elements state))

  (setup [_]
    (let [{:keys [x y button-color]} state
          button-w 150
          button-x (+ x stringer-width button-gap)
          torpedo-y (+ y banner-width button-gap)
          kinetic-y (+ torpedo-y button-h button-gap)
          phaser-y (+ kinetic-y button-h button-gap)]
      (weapons-panel.
        (assoc state
          :torpedo (p/setup
                     (w/->button
                       {:x button-x
                        :y torpedo-y
                        :w button-w
                        :h button-h
                        :name "TORPEDO"
                        :color button-color
                        :left-up-event {:event :select-torpedo}}))
          :kinetic (p/setup
                     (w/->button
                       {:x button-x
                        :y kinetic-y
                        :w button-w
                        :h button-h
                        :name "KINETIC"
                        :color button-color
                        :left-up-event {:event :select-kinetic}}))
          :phaser (p/setup
                  (w/->button
                    {:x button-x
                     :y phaser-y
                     :w button-w
                     :h button-h
                     :name "PHASER"
                     :color button-color
                     :left-up-event {:event :select-phaser}}))
          :elements [:torpedo :kinetic :phaser]))))

  (update-state [_ commands]
    (let [[new-state events] (p/update-elements state commands)]
      (p/pack-update
        (weapons-panel. new-state)
        events))))

(deftype damage-panel [state]
  p/Drawable
  (draw [_]
    (draw-lcars state)
    (p/draw-elements state))

  (setup [_]
    (let [{:keys [x y w]} state]
      (damage-panel. state)))

  (update-state [_ commands]
    (let [[new-state events] (p/update-elements state commands)]
      (p/pack-update
        (damage-panel. new-state)
        events))))

(deftype status-panel [state]
  p/Drawable
  (draw [_]
    (draw-bottom-lcars state)
    (p/draw-elements state))

  (setup [_]
    (let [{:keys [x y w]} state]
      (status-panel. state)))

  (update-state [_ commands]
    (let [[new-state events] (p/update-elements state commands)]
      (p/pack-update
        (status-panel. new-state)
        events))))

