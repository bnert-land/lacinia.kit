(ns server.resolvers
  (:require
    [com.walmartlabs.lacinia.executor :as l.executor]
    [com.walmartlabs.lacinia.schema :refer [tag-with-type]]
    [com.walmartlabs.lacinia.resolve :as resolve*]
    [com.walmartlabs.lacinia.selection :as l.selection]
    [com.walmartlabs.lacinia.select-utils :refer [wrap-value]]
    [server.db :as db]))

(defn- tag-hero [x]
  (case (:kind x)
    :HUMAN    (tag-with-type x :Human)
    :DROID    (tag-with-type x :Droid)
    #_default nil))

(defn with-parent [ctx parent-name value]
  (let [pxs (or (get ctx ::parents []) [])]
    (resolve*/with-context value {::parents (conj pxs parent-name)})))

(defn parent? [ctx parent-name]
  (= parent-name (last (get ctx ::parents []))))


; Mocking how they would be returned from a db
(defn batched-causes [ids]
  (-> (select-keys (db/causes) ids)
      (vals)))

; --

(defn add-human [_ctx args _value]
  (db/add-human (update args :appearsIn #(into #{} %))))


(defn cause [ctx {:keys [id] :as _args} value]
  (with-parent ctx :Cause
    (cond 
      (parent? ctx :Humans)
        (->> (get value :causes #{})
             (mapv #(get (db/causes) %))
             (sort-by :name))
      :else
        (when-let [c (get (db/causes) id)]
          (with-parent ctx :Cause c)))))

(defn droid [_ctx {:keys [id] :as _args} _value]
  (->> (db/droids)
       (filter #(= id (:id %)))
       (first)))

(defn droids [_ctx {:keys [episode] :as _args} _value]
  (cond->> (db/droids)
    episode
      (filter #(contains? (:appearsIn %) episode))
    true
    (sort-by :name)))

(defn hero [_ctx {:keys [id] :as _args} _value]
  (when-let [result (->> (db/heros)
                         (map tag-hero)
                         (filter #(= id (:id %)))
                         (first))]
    (tag-hero result)))

(defn heros [_ctx {:keys [episode] :as _args} _value]
  (cond->> (map tag-hero (db/heros))
    episode
      (filter #(contains? (:appearsIn %) episode))
    true
      (sort-by :name)))

(defn human [_ctx {:keys [id] :as _args} _value]
  (->> (db/humans)
       (filter #(= id (:id %)))
       (first)))

(defn humans [ctx {:keys [episode] :as _args} value]
  (with-parent
    ctx
    :Humans
    (cond->> (db/humans)
      episode
        (filter #(contains? (:appearsIn %) episode))
      (parent? ctx :Cause)
        (filter #(contains? (:causes %) (get value :id)))
      true
        (sort-by :name))))

