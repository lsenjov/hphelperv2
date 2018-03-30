(ns hphelperv2.components.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.events :as events]
            )
  (:import [goog.events EventType])
  )

(def window-coords-path [::window-coords])

(rf/reg-event-db
  ::move-window
  (fn [db [_ k coords]]
    (update-in db (conj window-coords-path k) merge coords)))
(rf/reg-event-db
  ::min-or-maximise
  (fn [db [_ k]]
    (update-in db (conj window-coords-path k ::minimised?) not)))
(rf/reg-sub
  ::get-window
  (fn [db [_ k]]
    (get-in db (conj window-coords-path k))))

(defn get-client-rect [evt]
  (let [r (.getBoundingClientRect (.-target evt))]
    {:left (.-left r), :top (.-top r)}))
(defn mouse-move-handler [id offset]
  (fn [evt]
    (let [x (- (.-clientX evt) (::x offset))
          y (- (.-clientY evt) (::y offset))]
      (rf/dispatch [::move-window id {::x x ::y y}]))))
(defn mouse-up-handler [id on-move]
  (fn me [evt]
    (events/unlisten js/window EventType.MOUSEMOVE
                     on-move)))
(defn mouse-down-handler
  [id e]
  (let [{:keys [left top]} (get-client-rect e)
        offset             {::x (- (.-clientX e) left)
                            ::y (- (.-clientY e) top)}
        on-move            ((partial mouse-move-handler id) offset)]
    (events/listen js/window EventType.MOUSEMOVE
                   on-move)
    (events/listen js/window EventType.MOUSEUP
                   ((partial mouse-up-handler id) on-move))))

(defn comp-draggable
  [id body-fn]
  (let [pos (rf/subscribe [::get-window id])]
    [:div
     [:div (pr-str @pos)]
     [:div.card.border-secondary
      {:style {:position "absolute"
               :left (or (::x @pos) 100)
               :top (or (::y @pos) 100)}}
      [:div.card-header.no-select
        ;:on-click #(rf/dispatch [::move-window :test {::x 200 ::y 200}])
        {:on-mouse-down (partial mouse-down-handler id)}
       "Dragbar" (pr-str id)
       ;; Minimise/maximise button
       [:div.btn.btn-secondary-outline.btn-sm
        {:on-click #(rf/dispatch [::min-or-maximise id])}
        (if (::minimised? @pos) "\u21D2" "\u21D3")
        ]
       ]
      (if (not (::minimised? @pos))
        [:div.card-body
         (body-fn)
         ]
        )
      ]]))
