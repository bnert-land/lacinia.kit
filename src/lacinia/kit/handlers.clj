(ns lacinia.kit.handlers
  (:require
    [lacinia.kit.constants :as c]
    [com.walmartlabs.lacinia :as l]))

(defn query
  ([]
   (query nil))
  ([opts]
   (fn [req]
     (try
       (let [result (l/execute-parsed-query
                      (get req c/*parsed-query-key*)
                      (get req :graphql/variables {})
                      (get req c/*ctx-key* {})
                      opts)
             {:keys [data]} result]
         {:status  (or (and data 200) 400)
          :headers {}
          :body    result})
       (catch Exception e
         {:status  500
          :headers {}
          :body    {:errors [{:title "Execution error"
                              :meta  {:message (.getMessage e)}}]}})))))

