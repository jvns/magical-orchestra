(ns overtone-fun.drummachine
  (:import jline.Terminal)
;(:require [overtone.live :refer :all])  
  (:gen-class))

(defn read-character []
  (let [term (Terminal/getTerminal)]
    (.readCharacter term System/in)))

(defn -main [& args]
  (while true
    (let [c (read-character)]
      (cond
       (== c 68) (println "Left!")
       (== c 67) (println "Right!")
       :else nil))))
