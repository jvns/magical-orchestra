(ns overtone-fun.server
   (:require
   [overtone.live :refer :all]
   [overtone.inst.sampled-piano :refer :all]
   [overtone.inst.drum :as drum]
   [overtone.inst.synth :as synth]
   [org.httpkit.server :refer [run-server]]))


; Some drums we found on http://freesound.org
(def freesound-drum-ids
  [
   {:sound 104214 :name "Crash Cymbal"}
   {:sound 104257 :name "Bass"}
   {:sound 120403 :name "Drum"}
   {:sound 43370  :name "Bass"}
   {:sound 63239  :name "Tambourine"}
   {:sound 121099 :name "Sleigh Bells"}
   {:sound 91191  :name "Cowbell"}])

; Pull the drum sounds from the server
(def freesound-drums (map
   (fn [x] (update-in x [:sound] freesound-sample))
   freesound-drum-ids))

; Get a random drum sound
(defn random-drum []
  (rand-nth freesound-drums))

; Play a sound stored in a map
(defn play-sound [sound]
  ((:sound sound)))

; Store all the player instruments so that they
; don't change every request
(def player-instruments (atom {}))

(defn app [req]
  (let [keycode (Integer/parseInt (:query-string req))
        ip-addr (:remote-addr req)]
    (when (nil? (get @player-instruments ip-addr))
      (swap! player-instruments assoc ip-addr (random-drum))
      )
    (play-sound (get @player-instruments ip-addr))
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (:name (get @player-instruments ip-addr))}))

; We need to save 'stop-server so that we can 
; stop the server when we're done
(def stop-server (run-server app {:port 8080}))
; (stop-server)


