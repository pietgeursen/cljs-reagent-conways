(ns conways.core
  (:require 
    [cljsjs.virtual-dom]
    [conways.lib :refer [randomBoard createBoard nextBoard]]
    ))

(def h js/virtualDom.h)
(def create js/virtualDom.create)
(def diff js/virtualDom.diff)
(def patch js/virtualDom.patch)

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
    
(defn cellComponent [idx cell] 
  (h "div" (js-obj "className" "cell") (if cell "X" "_")))

(defn rowComponent [idx row] 
  (h "div" (js-obj "className" "row") 
     (clj->js (map-indexed (fn [idx cell] ^{:key idx} (cellComponent idx cell)) row))
   ))

(defn boardComponent [board] 
  (h "div" (js-obj "className" "board") 
     (clj->js (map-indexed (fn [idx board] ^{:key idx} (rowComponent idx board)) board))
   ))

(defonce board (atom (randomBoard (createBoard 50))))
(defonce tree (atom (boardComponent @board)))
(defonce rootNode (atom (create @tree)))


(js/document.body.appendChild @rootNode)

(defn tick [] 
  (swap! board #(nextBoard @board))
  (def newTree (boardComponent @board))
  (def patches (diff @tree newTree))
  (swap! rootNode #(patch @rootNode patches))
  (swap! tree (fn [] newTree))
  )

(js/setInterval tick 1000)
