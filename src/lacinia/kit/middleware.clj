(ns lacinia.kit.middleware
  (:require
    [lacinia.kit.constants :as c]
    [lacinia.kit.internal :as i]))

(defn source-graphql-data 
  ([]
   (source-graphql-data [:body-params :form-params]))
  ([search-keys]
   (source-graphql-data
     search-keys
     {:status 400
      :body   {:errors [{:title "Unable to find graphql data"
                         :meta  {:search-keys search-keys}}]}}))
  ([search-keys missing-query-response]
   (fn [handler]
     (fn [req]
       (if-let [data (i/source-graphql-data req search-keys)]
         (handler (into req data))
         missing-query-response)))))

(defn app-context [app-ctx]
  (fn [handler]
    (fn [req]
      (handler (assoc req c/*ctx-key* (assoc app-ctx :request req))))))

(defn parse-query [compiled-schema cache?]
  (fn [handler]
    (fn [req]
      (handler (assoc req
                      c/*parsed-query-key*
                      (i/parse-query req compiled-schema cache?))))))

; --

(defn middleware [{:keys [context
                          body-search-keys
                          resolvers
                          scalars
                          schema
                          schema-opts
                          query-cache]
                   :or   {context          {}
                          body-search-keys [:body-params :form-params]
                          query-cache      nil
                          resolvers        {}
                          scalars          {}
                          schema           {}
                          schema-opts      {}}}]
  [(source-graphql-data body-search-keys)
   (app-context context)
   (parse-query (i/compile-schema
                  {:resolvers   resolvers
                   :scalars     scalars
                   :schema      schema
                   :schema-opts schema-opts})
                query-cache)])

