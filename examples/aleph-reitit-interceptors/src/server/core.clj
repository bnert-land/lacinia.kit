(ns server.core
  (:require
    [aleph.http :as http]
    [beh.core :as beh]
    [clojure.java.io :as io]
    [lacinia.kit.handlers :as l.kit.handlers]
    [lacinia.kit.graphiql :as l.kit.graphiql]
    [lacinia.kit.interceptors :as l.kit.interceptors]
    [manifold.executor :as m.executor]
    [muuntaja.core :as muuntaja]
    [reitit.http :as r.http]
    [reitit.ring :as r.ring]
    [reitit.interceptor.sieppari :as i.sieppari]
    [reitit.http.interceptors.parameters :as i.parameters]
    [reitit.http.interceptors.muuntaja :as i.muuntaja]
    [server.graphql.scalars]
    [server.resolvers :as s.resolvers]
    [sieppari.async.manifold]))

(def resolvers
  ; :Query/humans returns a (list :Human), so does :Cause/humans.
  ; Therefore, :objects/Cause will be called for each :Human in the returned
  ; list, rather than as batched.
  ;
  ; To avoid n+1 in production cases, look at clojure data loadeer libraries
  ; (in alphabetical order):
  ;   - Claro: https://github.com/xsc/claro
  ;   - Muse: https://github.com/kachayev/muse
  ;   - Superlifter: https://github.com/oliyh/superlifter
  ;   - Urania: https://github.com/funcool/urania
  {:Cause/humans s.resolvers/humans
   :Human/causes s.resolvers/cause
   :Mutation/addHuman s.resolvers/add-human
   :Query/cause  s.resolvers/cause
   :Query/droid  s.resolvers/droid
   :Query/droids s.resolvers/droids
   :Query/hero   s.resolvers/hero
   :Query/heros  s.resolvers/heros
   :Query/human  s.resolvers/human
   :Query/humans s.resolvers/humans})

(def disable-graphiql?
  (delay
    (some? (System/getenv "DISABLE_GRAPHIQL"))))

(def router
  (r.http/router
    [["/graphiql"
      {:get {:handler (l.kit.graphiql/graphiql
                        {:enable? (not @disable-graphiql?)
                         :url     "http://localhost:9109/graphql"})}}]
     ["/graphql"
      {:get {:handler (l.kit.graphiql/graphiql
                        {:enable? (not @disable-graphiql?)
                         :url     "http://localhost:9109/graphql"})}
       :post
       {:interceptors (l.kit.interceptors/interceptors
                        {:schema      (io/resource "graphql/schema.edn")
                         :schema-opts {:executor (m.executor/execute-pool)}
                         :resolvers   resolvers})
        :handler      (l.kit.handlers/query)}}]

       ; Not implemented yet...
       #_["/graphql/subscribe"
        {:post
         {:interceptors l.subscriptions/interceptors
          :handler (l.subscriptions/handler schema resolvers)}}]]
    {:data
     {:muuntaja   muuntaja/instance
      :interceptors
      [(i.parameters/parameters-interceptor)
       (i.muuntaja/format-negotiate-interceptor)
       (i.muuntaja/format-response-interceptor)
       (i.muuntaja/format-request-interceptor)
       ; realize all deferreds, including nested deferreds
       ; before moving back up the interceptor callstack
       (beh/realize-deferreds {:kind :sieppari/interceptor})]}}))

(defn server! [opts]
  (http/start-server
    (http/wrap-ring-async-handler
      (r.http/ring-handler
        router
        (r.ring/create-default-handler)
        {:executor i.sieppari/executor}))
    (-> opts
        (update :port (fnil identity 9109)))))

(defn -main [& _args]
  (println "Starting server...")
  (server! {}))

