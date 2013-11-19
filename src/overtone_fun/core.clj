(ns overtone-fun.core)

(use 'overtone.live)

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

(def metro (metronome 120))

(def rhythm [[0 o-hat] [1 c-hat] [1.65 c-hat])

(defn play-rhythm [rhythm beat]
  (play-once rhythm beat)
  (apply-at (metro (+ 2 beat)) #'play-rhythm rhythm (+ 2 beat) []))

(defn play-once [rhythm beat]
  (let [play-beat (fn [offset inst] (at (metro (+ beat offset)) (inst)))]
    (doseq [sound rhythm] (apply play-beat sound))))

'(play-rhythm rhythm (metro))

'(play-rhythm rhythm2 (metro))


