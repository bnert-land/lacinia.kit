(ns lacinia.kit.graphiql
  (:require
    [hiccup2.core :as h]))

(defn document* [opts]
  ; Adapted from: https://github.com/graphql/graphiql/blob/main/examples/graphiql-cdn/index.html
  ; with some minor modifications.
  (str
    (h/html
      [:html {:lang "en"}
       [:head
        [:style
         "body { height: 100%; margin: 0; width: 100%; overflow: hidden; }
         #graphiql { height: 100vh; }"]
        [:script
         {:crossorigin true,
          :src         "https://unpkg.com/react@18/umd/react.development.js"}]
        [:script
         {:crossorigin true,
          :src         "https://unpkg.com/react-dom@18/umd/react-dom.development.js"}]
        [:script
         {:src  "https://unpkg.com/graphiql/graphiql.min.js"
          :type "application/javascript"}]
        [:link
         {:href "https://unpkg.com/graphiql/graphiql.min.css"
          :rel "stylesheet"}]
        [:script
         {:crossorigin true
          :src         "https://unpkg.com/@graphiql/plugin-explorer/dist/index.umd.js"}]
        [:link
         {:href "https://unpkg.com/@graphiql/plugin-explorer/dist/style.css"
          :rel  "stylesheet"}]]
       [:body
        [:div {:id "graphiql"} "Loading..."]
        [:script
         (h/raw
           (format
             "const root = ReactDOM.createRoot(document.getElementById('graphiql'));
             const fetcher = GraphiQL.createFetcher({
               url: \"%s\",
               /* subscriptionUrl: \"%s\", */
             });
             const explorerPlugin = GraphiQLPluginExplorer.explorerPlugin();
             root.render(
               React.createElement(GraphiQL, {
                 fetcher,
                 defaultEditorToolsVisibility: true,
                 plugins: [explorerPlugin],
               }),
             );"
             (get opts :queryUrl "http://localhost:9109/graphql")
             (get opts :subscriptionUrl "ws://localhost:9109/graphql/subscribe")))]]])))

(def document
  (memoize document*))

(defn graphiql [opts]
  (fn graphiql-handler [_req]
    (when (true? (get opts :enable? false))
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body    (document opts)})))

