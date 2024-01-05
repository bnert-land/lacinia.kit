(ns user)

(require
  '[aleph.http :as http]
  '[clojure.tools.namespace.repl :as repl]
  '[clojure.java.io :as io]
  '[beh.core :as beh]
  '[sieppari.async.manifold])

(repl/disable-unload!)
(repl/disable-reload!)
(repl/set-refresh-dirs "src/")

(comment
  (repl/refresh)

  (beh/use-jsonista)

  (defn q [query op variables]
    (try
      (deref
        (http/post "http://localhost:9109/graphql"
          {:as           :json
           :content-type :json
           :form-params  {:operationName op
                          :variables     (or variables {})
                          :query         (-> query
                                             (io/resource)
                                             (slurp))}}))
      (catch Exception e
        (ex-data e))))

  ;; heros from examples
  (let [luke "e071ca3c-817a-4cb3-a1be-138b2d3d085c"
        leia "acf2bc4f-8b1b-4c93-8e8f-1491c6d25755"
        r4   "a3915127-1676-4a1e-93e9-6c66dbf1a76a"
        query (slurp (io/resource "graphql/queries/heroes.graphql"))]
    (q "graphql/queries/hero.graphql"
       "HeroQuery"
       {:id luke}))

  (q "graphql/queries/heroes.graphql"
     "HeroesQuery"
     {:episode :JEDI})

  (q "graphql/queries/droids.graphql"
     "DroidsQuery"
     {:episode nil #_:NEWHOPE})

  (q "graphql/queries/humans.graphql"
     "HumansQuery"
     {})

  (q "graphql/queries/cause.graphql"
     "CauseQuery"
     {:id 1})

  (q "graphql/mutations/add-human.graphql"
     "AddHuman"
     {:name         "Jabba The Hutt"
      :originPlanet "Nal Hutta"
      :episodes     [:EMPIRE :JEDI]})
)
