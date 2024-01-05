(ns lacinia.kit.constants)

(def ^:dynamic *ctx-key* :lacinia-app-context)

; not 1:1 w/ pedestal impl, but similar enough
(def ^:dynamic *tracing-key* :lacinia-timing-start)
(def ^:dynamic *parsed-query-key* :lacinia-parsed-query)
