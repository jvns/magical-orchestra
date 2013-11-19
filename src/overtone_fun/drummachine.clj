(ns overtone-fun.drummachine
  (:import jline.Terminal)
 (:require [overtone.live :refer :all])  
  (:gen-class))

(definst c-hat [amp 0.8 t 0.04]
  (let [env (env-gen (perc 0.001 t) 1 1 0 1 FREE)
        noise (white-noise)
        sqr (* (env-gen (perc 0.01 0.04)) (pulse 880 0.2))
        filt (bpf (+ sqr noise) 9000 0.5)]
    (* amp env filt)))


(definst o-hat [amp 0.8 t 0.5]
  (let [env (env-gen (perc 0.001 t) 1 1 0 1 FREE)
        noise (white-noise)
        sqr (* (env-gen (perc 0.01 0.04)) (pulse 880 0.2))
        filt (bpf (+ sqr noise) 9000 0.5)]
    (* amp env filt)))

(defn read-character []
  (let [term (Terminal/getTerminal)]
    (.readCharacter term System/in)))

(defn -main [& args]
  (while true
    (let [c (read-character)]
      (cond
       (== c 68) (o-hat)
       (== c 67) (c-hat)
       :else nil))))
