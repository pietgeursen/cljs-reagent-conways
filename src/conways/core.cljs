(ns conways.core
  (:require 
    [reagent.core :as r]
    [conways.lib :refer [randomBoard createBoard nextBoard]]
    ))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
    
(defn cellComponent [idx cell] 
  [:div {:class "cell" :key idx } (if cell "X" "_")])

(defn rowComponent [idx row] 
  [:div {:class "row" :key idx} 
     (map-indexed (fn [idx board] ^{:key idx} [cellComponent idx board]) row)
   ])

(defn boardComponent [board] 
  [:div {:class "board"} 
   (map-indexed (fn [idx board] ^{:key idx} [rowComponent idx board]) @board)
 
   ])

(defonce board (r/atom (randomBoard (createBoard 50))))

(r/render [boardComponent board]
            (js/document.getElementById "app"))

(js/setInterval 
  (fn [] (swap! board #(nextBoard @board))) 1000)
