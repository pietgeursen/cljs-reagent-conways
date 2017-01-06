(ns conways.core
  (:require 
    [clojure.string :refer [join]]
    [reagent.core :as r]
    ))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn ressuretable?
  "is cell ressuretable?"
  [neighbourCount]
  (= neighbourCount 3))

(defn createBoard 
  "creates a new board with dimensions of size passed as first arg" 
  [size] 
  (defn makeRow "makes a row of size n" [n] (map (fn [item] false) (range n))) 
  (map (fn [item] (makeRow size)) (range size)))

(defn nextCellAlive? 
  "calculates if next cell is alive given its neighbour count and whether it is currently alive"
  [neighbourCount alive]
  (or 
    (ressuretable? neighbourCount) 
    (and alive (= neighbourCount 4))))

(defn getCell 
  "gets a cell in a board at a location [[row col] board]"
  [[row col] board]
  (nth (nth board row nil) col nil))

(def locations [
                  [-1 -1]                  
                  [-1 0]                  
                  [-1 1]                  
                  [ 0 -1]                  
                  [ 0 1]                  
                  [1 -1]                  
                  [1 0]                  
                  [1 1]                  
                  ])

(defn getNeighbourCount 
  "calculates the number of neighbours of a given cell location in a board [[row col] board]"
  [[row col] board]
  
  (reduce (fn 
            [sum location] 
            (def absLoc [(+ row (first location)) 
                        (+ col (second location))])
            (def cell (getCell absLoc board))
            (+ sum 
               (if cell 1 0))) 
          0 locations))

(defn nextBoard 
  "calculates the next board from the current board"
  [board]
    (map-indexed 
      (fn [rowIndex row ] 
          (map-indexed 
            (fn [colIndex cell ] 
               (nextCellAlive? (getNeighbourCount [rowIndex colIndex] board) cell))
            row))
      board))

(defn randomBoard 
  "takes a board and fills it with random alive and dead cells" 
  [board]
  (map 
    (fn [row] 
      (map (fn [cell] (> 0.7 (rand))) row))
    board))
    
(defn cellComponent [idx cell] [:div {:class "cell" :key idx } (if cell "X" "_")])
(defn rowComponent [idx row] 
  [:div {:class "row" :key idx} 
    (map-indexed cellComponent row) 
   ])

(defn boardComponent [board] 
  [:div {:class "board"} 
   (map-indexed rowComponent @board)
   ])

(def board (r/atom (randomBoard (createBoard 50))))

(r/render [boardComponent board]
            (js/document.getElementById "app"))

(js/setInterval (fn [] (swap! board (fn [] (nextBoard @board)))) 1000)
