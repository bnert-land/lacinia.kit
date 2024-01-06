(ns lacinia.kit.impls.apollo.contracts
  (:require
    [lacinia.kit.impls.apollo.messages :as a.msg]))

(def MapKey
  [:or :string :keyword])

(def ConnectionInitMessage
  [:map
   [:type [:= a.msg/kind-connection-init]]
   [:payload {:optional true}
    [:map-of :any :any]]])

(def ConnectionAckMessage
  [:map
   [:type [:= a.msg/kind-connection-ack]]
   [:payload {:optional true}
    [:map-of MapKey :any]]])

(def PingMessage
  [:map
   [:type [:= a.msg/kind-ping]]
   [:payload {:optional true}
    [:map-of MapKey :any]]])

(def PongMessage
  [:map
   [:type [:= a.msg/kind-pong]]
   [:payload {:optional true}
    [:map-of MapKey :any]]])

(def SubscribeMessage
  [:map
   [:id :string]
   [:type [:= a.msg/kind-subscribe]]
   [:payload
    [:map
     ; -- required (alphabetical)
     [:query :string]

     ; -- optional (alphabetical)
     [:extensions {:optional true}
      [:map-of MapKey :any]]
     [:operationName {:optional true} :string]
     [:variables {:optional true}
      [:map-of MapKey :any]]]]])

(declare ExecutionResult ExecutionPatchResult)

(def NextMessage
  [:map
   [:id :string]
   [:type [:= a.msg/kind-next]]
   [:payload
     [:or ExecutionResult ExecutionPatchResult]]])

(def SourceLocation
  [:map [:line :int] [:column :int]])

(def GraphQLError
  [:map
   ; -- require (alphabetical)
   ; -- optional (alphabetical)
   [:extensions {:optional true}
    [:map-of MapKey :any]]
   [:locations {:optional true}
    SourceLocation]
   [:nodes {:optional true}
    [:+ :any]]
   [:originalError {:optional true}
    :any]
   [:path {:optional true}
    [:+ [:or :string :int]]]
   [:positions {:optional true}
    [:+ :int]]
   [:source {:optional true}
    [:map
     [:body :string]
     [:name :string]
     [:locationOffset SourceLocation]]]])

(def ExecutionResult
  [:map
   ; -- required (alphabetical)
   [:hasNext :boolean]
   ; -- optional (alphabetical)
   [:data {:optional true}
    [:map-of MapKey :any]]
   [:errors {:optional true}
    [:+ GraphQLError]]
   [:extensions {:optional true}
    [:map-of MapKey :any]]])

(def ExecutionPatchResult
  [:map
   ; -- required (alphabetical)
   [:hasNext :boolean]
   ; -- optional (alphabetical)
   [:data {:optional true}
    [:map-of MapKey :any]]
   [:errors {:optional true}
    [:+ GraphQLError]]
   [:extensions {:optional true}
    [:map-of MapKey :any]]
   [:label {:optional true}
    :string]
   [:path {:optional true}
    [:+ [:or :string :int]]]])

