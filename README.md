# lacinia.kit

Utilities to use Lacinia with other http implementations beside Pedestal.


## Features/Compatability

| Feature       | Ring Middleware       | Sieppari Middleware   | Sieppari Interceptors |
|:--------------|:----------------------|:----------------------|:----------------------|
| Query         | yes, not tested       | yes, not tested       | yes, partially tested |
| Mutations     | yes, not tested       | yes, not tested       | yes, partially tested |
| Subscriptions | no, planned           | no, planned           | no, planned           |
| Tracing       | no, planned           | no, planned           | no, planned           |
| GraphiQL      | yes, partially tested | yes, partially tested | yes, partially tested |

Handling of subscriptions is still being worked out, given that underlying
http abstractions don't have a unified approach. Want to figure out by giving
it a floor run to see how it plays...


## Current State

Pre-pre-pre-pre-alpha.

The below examples are working as one would expect.


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
