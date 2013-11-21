(ns overtone-fun.drummachine
  (:import jline.Terminal)
  (:require
   [overtone.live :refer :all]
   [overtone.inst.sampled-piano :refer :all]
   [overtone.inst.drum :as drum]
   [overtone.inst.synth :as synth]
   [org.httpkit.server :refer [run-server]]
   )  
  (:gen-class))

(def freesound-drum-ids
  [
   {:sound 104214 :name "Crash Cymbal"}
   {:sound 104257 :name "Bass"}
   {:sound 120403 :name "Drum"}
   {:sound 43370  :name "Bass"}
   {:sound 63239  :name "Tambourine"}
   {:sound 121099 :name "Sleigh Bells"}
   {:sound 91191  :name "Cowbell"}])

(def freesound-drums (map
   (fn [x] (update-in x [:sound] freesound-sample))
   freesound-drum-ids))

(defn random-drum []
  (rand-nth freesound-drums))

(defn play-sound [sound]
  ((:sound sound)))


(play-random-drum)

(def stdout *out*)

(def player-instruments (atom {}))

(defn app [req]
  (let [keycode (Integer/parseInt (:query-string req))
        ip-addr (:remote-addr req)]
    (when (nil? (get @player-instruments ip-addr))
      (swap! player-instruments assoc ip-addr (random-drum))
      )
    (play-sound (get @player-instruments ip-addr))
    {:status 200}))

(def stop-server (run-server app {:port 8090}))
(stop-server)

(drum/hat-demo 10)

(stop)
(overtone.inst.synth/grunge-bass )

(defn read-character []
  (let [term (Terminal/getTerminal)]
    (.readCharacter term System/in)))

(sampled-piano 40)

(defn play-chord [a-chord]
  (doseq [note a-chord] (sampled-piano note)))

(play-chord (chord :C4 :major))
(play-chord (chord :F4 :major))
(play-chord (chord :G4 :major))

(defonce metro (metronome 120))

(defn chord-progression-beat [m beat-num]
  (at (m (+ 0 beat-num)) (play-chord (chord :C4 :major)))
  (at (m (+ 4 beat-num)) (play-chord (chord :G3 :major)))
  (at (m (+ 8 beat-num)) (play-chord (chord :A3 :minor)))
  (at (m (+ 12 beat-num)) (play-chord (chord :F3 :major)))
  (apply-at (m (+ 16 beat-num)) chord-progression-beat m (+ 16 beat-num) [])
)

(defn play-random-note [m beat-num a-chord]
  (at (m (+ 0 beat-num)) (sampled-piano (rand-nth a-chord)))
  (apply-at (m (+ 1 beat-num)) play-random-note m (+ 1 beat-num) a-chord []))
(drum/tone-snare)

(defn play-snare [m beat-num]
  (at (m (+ 0 beat-num)) (drum/tone-snare 1000 1))
  (apply-at (m (+ 2 beat-num)) play-snare m (+ 2 beat-num) []))


(play-snare metro (metro))
(chord-progression-beat metro (metro))
(play-random-note metro (metro) (chord :C4 :major)) 

(stop)

(def random-notes (repeatedly #(rand-nth (chord :C4 :major))))
(doseq [note random-notes]
  (sampled-piano note))
(o-hat)
(c-hat)

(defn -main [& args]
  (while true
    (let [c (read-character)]
      (cond
       (== c 68) (o-hat)
       (== c 67) (c-hat)
       :else nil))))
