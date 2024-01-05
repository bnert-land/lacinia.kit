(ns server.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]))

(def db
  (atom
    (->> (io/resource "db/seed.edn")
         (slurp)
         (edn/read-string)
         (reduce-kv
           (fn [m k v]
             (assoc m
                    (parse-uuid k)
                    (update v :id parse-uuid)))
           {}))))

(defn add-human [{:keys [name originPlanet appearsIn]}]
  (let [id (random-uuid)
        v  {:id           id
            :kind         :HUMAN
            :name         name
            :causes       #{}
            :appearsIn    appearsIn
            :originPlanet originPlanet}]
      (swap! db assoc id v)
      v))

(defn groups []
  (group-by :kind (vals @db)))

(defn humans []
  (-> (groups) :HUMAN))

(defn droids []
  (-> (groups) :DROID))

(defn heros []
  (vals @db))

; TODO put in db/seed.edn
(defn causes []
  {0 {:id   0
      :name "Galactic Empire"}
   1 {:id   1
      :name "Rebel Alliance"}})

