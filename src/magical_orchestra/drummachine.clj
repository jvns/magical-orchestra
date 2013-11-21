(ns magical-orchestra.drummachine
  (:import jline.Terminal)
  (:require
   [overtone.live :refer :all]
   [overtone.inst.sampled-piano :refer :all]
   [overtone.inst.drum :as drum]
   [overtone.inst.synth :as synth]
   [org.httpkit.server :refer [run-server]]
   ))

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
(defonce metro3 (metronome 240))


(defn apply-interval
  ([m freq f]
     (apply-interval m (m) freq f))
  ([m beat freq f]
     (f beat)
     (apply-at (m (+ beat freq)) apply-interval m (+ beat freq) beat freq f)))

(defn chord-progression-beat [m]
  (apply-interval m 32
    (fn [beat]
      (at (m (+ 0 beat)) (play-chord (chord :C4 :major)))
      (at (m (+ 8 beat)) (play-chord (chord :G3 :major)))
      (at (m (+ 16 beat)) (play-chord (chord :A3 :minor)))
      (at (m (+ 24 beat)) (play-chord (chord :F3 :major))))))

#_(defn chord-progression-beat [m beat-num]
  (at (m (+ 0 beat-num)) (play-chord (chord :C4 :major)))
  (at (m (+ 8 beat-num)) (play-chord (chord :G3 :major)))
  (at (m (+ 16 beat-num)) (play-chord (chord :A3 :minor)))
  (at (m (+ 24 beat-num)) (play-chord (chord :F3 :major)))
  (apply-at (m (+ 32 beat-num)) chord-progression-beat m (+ 32 beat-num) [])
)

(defn play-random-note [m a-chord]
  (apply-interval m 1
    (fn [beat] (at (m beat) (sampled-piano (rand-nth a-chord))))))

#_(defn play-random-note [m beat-num a-chord]
  (at (m (+ 0 beat-num)) (sampled-piano (rand-nth a-chord)))
  (apply-at (m (+ 1 beat-num)) play-random-note m (+ 1 beat-num) a-chord []))
(drum/tone-snare)

(defn play-snare [m beat-num]
  (at (m (+ 0 beat-num)) (drum/tone-snare 1000 1))
  (apply-at (m (+ 2 beat-num)) play-snare m (+ 2 beat-num) []))


(play-snare metro (metro))
(chord-progression-beat metro3 (metro3))
(play-random-note metro3 (metro3) (chord :C4 :major))

(stop)

(defn -main [& args]
  (while true
    (let [c (read-character)]
      (cond
       (== c 68) (o-hat)
       (== c 67) (c-hat)
       :else nil))))
