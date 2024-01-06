# lacinia.kit

**IMPORTANT**: Even though much hasn't been done here, I think there is enough
to go a different direction.

The direction should be to have a "server frontend" facade in front of
GraphQL execution engines and other such protocols, rather than attempting to
build a framework/utilities on top of a specific solution.

Work for this is done in [graphql.kit](https://github.com/bnert-land/graphql.kit),
and the work done here will be left as a tombstone, but also an example to anyone
who want to use lacinia directly.


---

Utilities/framework to use Lacinia with other http implementations beside Pedestal.

The design intent of this library is to expose some GraphQL primitives which
are able to hook into current web stacks in the Clojure community.


## Features/Compatability

| Feature       | Ring Middleware        | Sieppari Interceptors  |
|:--------------|:-----------------------|:-----------------------|
| Query         | yes, not tested        | yes, partially tested  |
| Mutations     | yes, not tested        | yes, partially tested  |
| Subscriptions | planned via websockets | planned via websockets |
| Tracing       | no, planned            | no, planned            |
| GraphiQL      | yes, partially tested  | yes, partially tested  |

Handling of subscriptions is still being worked out, given that underlying
http abstractions don't have a unified approach for websockets.

Want to figure out by giving it a floor run to see how it plays...


## Current State

Pre-pre-pre-pre-alpha.

The below examples are working as one would expect, given the above feature table.


## Examples

- [Aleph + Reitit (Interceptors)](./examples/aleph-reitit-interceptors)
- **TODO** [Aleph + Reitit (Middleware)](./)
- **TODO** [Jetty + Compojure](./)
- Others...?

To run each example (with GraphiQL):
```
$ clj -M -m server.core
```

By default, the server(s) will listen `http://localhost:9109`,
with the GraphQL endpoint set to `/graphql`. A `GET` request to `/graphiql`
will fetch the GraphiQL app (if enabled).

To launch a server w/o GraphiQL:
```
$ DISABLE_GRAPHIQL=true clj -M -m server.core
```
