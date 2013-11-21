(ns magical-orchestra.server
  (:require [overtone.live :refer :all]
            [overtone.inst.sampled-piano :refer :all]
            [overtone.inst.drum :as drum]
            [overtone.inst.synth :as synth]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET]]
            [org.httpkit.server :refer [run-server]]))


;; Some drums we found on http://freesound.org
(def freesound-drums
  (map #(assoc % :sound (freesound-sample (:id %)))
       [{:id 104214 :name "Crash Cymbal"}
        {:id 63239  :name "Tambourine"}
        {:id 7786   :name "Kazoo"}
        {:id 26710  :name "Snare"}
        {:id 62793  :name "Laser"}
        {:id 96109  :name "Xylophone"}
        {:id 9876   :name "Car Door"}
        {:id 26903  :name "Snare"}
        {:id 15348  :name "Pop"}
        {:id 9874   :name "Crow"}
        {:id 60009  :name "Bamboo Stick"}
        {:id 121099 :name "Sleigh Bells"}
        {:id 21840  :name "Drum"} ;; Wood noise
        {:id 65480  :name "Wine Bottle"}
        {:id 65231  :name "Metal Bowl"}
        {:id 91191  :name "Cowbell"}]))



(def metro-fast (metronome 960))
(def metro-slow (metronome 60))

;; Our background sound
(def bass-sound (freesound-sample 104257))


(defn apply-interval
  ([m freq f]
     (apply-interval m (m) freq f))
  ([m beat freq f]
     (f beat)
     (apply-at (m (+ beat freq)) apply-interval m (+ beat freq) beat freq f)))

(defn play-sound-loudly [m freq sound]
  (apply-interval m freq
    (fn [current-beat]
      (dotimes [_ 8]
        (at (m current-beat) (sound))))))

(defn play-snare [m beat-num freq sound]
  ;; hack to increase volume
  (dotimes [_ 8]
    (at (m (+ 0 beat-num)) (sound)))
  (apply-at (m (+ freq beat-num))
            play-snare m (+ freq beat-num) freq sound []))


;; Get a random drum sound
(defn random-drum []
  (rand-nth freesound-drums))

;; Get n random drums
(defn random-drums [n]
  (take n (shuffle freesound-drums)))

;; Play a sound stored in a map
(defn play-sound [sound]
  (at (metro-fast (metro-fast)) ((:sound sound))))

;; Store all the player instruments so that they
;; don't change every request
(def player-instruments (atom {}))

(def SOUNDS-PER-PLAYER 4)

(defn get-sound [keycode ip-addr]
  (if (<= 37 keycode 40) ; arrow-keys
    (let [player-sounds (get @player-instruments ip-addr)]
      (nth player-sounds (mod keycode SOUNDS-PER-PLAYER)))
    {:name "Silence" :sound (fn [& _])}))

(defn play-sound-request [req]
  (let [keycode (-> req :params :keycode Integer/parseInt)
        ip-addr (-> req :remote-addr)]
    (when-not (get @player-instruments ip-addr)
      (swap! player-instruments assoc ip-addr (random-drums SOUNDS-PER-PLAYER)))
    (let [sound (get-sound keycode ip-addr)]
      (play-sound sound)
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body (:name sound)})))

(defroutes all-routes
  (GET "/" [] play-sound-request)
  (route/files "/static/")
  (route/not-found "<p>Page not found.</p>"))

(comment
  ;; We need to save 'stop-server so that we can
  ;; stop the server when we're done
  (def stop-server
    (run-server (site #'all-routes) {:port 8080}))


  (stop-server)
  )

(defn -main [& args]
  (stop)
  (do
    (play-snare metro-fast (metro-fast) 16 bass-sound))
  (run-server (site #'all-routes) {:port 8080}))
