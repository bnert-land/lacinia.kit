(ns lacinia.kit.interceptors
  "These set of interceptors assume a couple of things:
  1. Query params/form params/body params are being parsed vai muuntaja or something upstream.
  2. ...
  "
  (:require
    [lacinia.kit.constants :as c]
    [lacinia.kit.internal :as i]))

; -- helpers

(defn clear-graphql-data [ctx]
  (dissoc ctx :request dissoc :graphql-op :graphql-query :graphql-vars))

(defn error-graphql-data
  ([ctx] ; sieppari
   (-> ctx
          (clear-graphql-data)))
  ([ctx ^Exception _e] ; pedestal
   (-> ctx
          (clear-graphql-data))))


; -- interceptors 

; Not gonna worry about tracing at the outset, just want to get a working
; idea
#_(defn init-tracing []
  {:name :lacinia/init-tracing
   :enter
   (fn [ctx]
     (update-in ctx
                [:request c/*tracing-key*]
                (fnil identity (tracing/create-timing-start))))})

#_(defn enable-tracing []
  {:name :lacinia/enable-tracing
   :enter
   (fn [ctx]
     (cond-> ctx
       ; i.e. enabled?
       (get-in ctx [:request :headers "lacinia-tracing"])
         (update-in [:request c/*ctx-key*] tracing/enable-tracing)))})

(defn source-graphql-data
  ([]
   (source-graphql-data [:body-params :form-params]))
  ([search-keys]
   {:name :lacinia/source-graphql-data
    :enter
    (fn [ctx]
      (if-let [data (i/source-graphql-data (get ctx :request) search-keys)]
        (update ctx :request into data)
        (throw
          (ex-info "Invalid request"
                   {:status 400
                    :errors [{:title "Unable to find graphql data"
                              :meta  {:search-keys search-keys}}]}))))
    :error error-graphql-data
    :leave clear-graphql-data}))

(defn app-context [app-ctx]
  {:name :lacinia/app-context
   :enter
   (fn [ctx]
     (->> (assoc app-ctx :request (get ctx :request))
          (assoc-in ctx [:request c/*ctx-key*])))
   :leave
   (fn [ctx]
     (update ctx :request dissoc c/*ctx-key*))})

(defn parse-query [compiled-schema cache?]
  {:name :lacinia/parse-query
   :enter
   (fn [{:keys [request] :as ctx}]
     (assoc-in ctx
               [:request c/*parsed-query-key*]
               (i/parse-query request compiled-schema cache?)))
   :error
   (fn [{:keys [error] :as ctx}]
     (let [em (ex-message error)
           ed (ex-data error)]
       (-> ctx
           (dissoc :error)
           (assoc :response
                  {:status 400
                   :body
                   {:errors [{:message em
                              :locations (:locations ed)
                              :extensions
                              {:missingArguments (:missing-arguments ed)
                               :fieldName (:field-name ed)}}]}}))))})


; --

(defn interceptors [{:keys [context
                            body-search-keys
                            graphiql
                            resolvers
                            scalars
                            schema
                            schema-opts
                            query-cache]
                     :or   {context          {}
                            body-search-keys [:body :body-params :form-params]
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

