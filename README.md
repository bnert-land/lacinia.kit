# lacinia.kit

Utilities to use Lacinia with other http implementations beside Pedestal.


## Features/Compatability

| Feature       | Ring Middleware | Sieppari Middleware | Sieppari Interceptors |
|:--------------|:----------------|:--------------------|:----------------------|
| Query         | yes, not tested | yes, not tested     | yes, partially tested |
| Mutations     | yes, not tested | yes, not tested     | yes, partially tested |
| Subscriptions | no, planned     | no, planned         | no, planned           |
| Tracing       | no, planned     | no, planned         | no, planned           |
| GraphiQL      | no, planned     | no, planned         | no, planned           |

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

To run each example:
```
$ clj -M -m server.core
```
