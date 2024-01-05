(ns server.graphql.scalars)

(def Uuid
  {:parse    #(when (string? %)
                (try
                  (parse-uuid %)
                  (catch Throwable _
                    nil)))
   :serialize #(try
                (str %)
                (catch Throwable _
                  nil))})

