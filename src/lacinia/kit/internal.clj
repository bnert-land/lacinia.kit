(ns lacinia.kit.internal
  (:require
    [aero.core :as aero]
    [clojure.core.cache.wrapped :as cache]
    [com.walmartlabs.lacinia.parser :as l.parser]
    [com.walmartlabs.lacinia.schema :as l.schema]
    [com.walmartlabs.lacinia.util :as l.util]))

(defn unwrap-compiled-schema? [compiled-schema]
  (cond
    (map? compiled-schema)
      compiled-schema
    (fn? compiled-schema)
      (compiled-schema)
    (instance? clojure.lang.IDeref compiled-schema)
      (deref compiled-schema)
    :else
      (throw (ex-info "Invalid compiled schema" {}))))

(defn find-body [request search-keys]
  (loop [ks search-keys]
    (if-not (seq ks)
      nil
      (let [body? (get request (first ks))]
        (if (and (map? body?) (seq body?))
          body?
          (recur (rest ks)))))))

(defn source-graphql-data [request search-keys]
  (when-let [body? (find-body request search-keys)]
    (let [{:keys [operationName query variables]} body?]
      {:graphql/op        operationName
       :graphql/query     query
       :graphql/variables variables})))

(defn parse-query [request compiled-schema cache?]
  (let [{:graphql/keys [query op]} request
        compiled  (unwrap-compiled-schema? compiled-schema)
        parsed    (delay
                    (l.parser/parse-query compiled query op nil))]
    (if-not cache?
      @parsed
      (let [cache-key (cond-> [query]
                        op (conj op))]
        (cache/lookup-or-miss cache? cache-key (fn [_] @parsed))))))

(defn resolve-scalar-references [m]
  (reduce-kv
    (fn [m k v]
      (if (and (symbol? v) (namespace v))
        (assoc m k @(resolve v))
        (assoc m k v)))
    {}
    m))

(defn load-schema [x {:keys [use-resource-loader?]
                      :or   {use-resource-loader? true}}]
  (let [config (aero/read-config x (cond-> {}
                                     use-resource-loader?
                                       (into {:resolver aero/resource-resolver})))]
    (update config :scalars (fnil resolve-scalar-references {}))))


(defn resource? [x]
  (instance? java.net.URL x))

(defn compile-schema [{:keys [resolvers schema schema-opts]}]
  (let [schema (cond
                 (resource? schema) (load-schema schema nil)
                 :else              schema)]
    (cond-> schema
      resolvers
        (l.util/inject-resolvers resolvers)
      true
        (l.schema/compile schema-opts))))

