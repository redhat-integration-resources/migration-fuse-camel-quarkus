# Migration example from a Karaf based Fuse project to Camel Quarkus

The folders in this repository contain the following:

- base -> karaf: Source code for the Fuse project.
- base -> camelq: REST and SOAP stubs (endpoints Fuse calls).
- docs -> Guided instructions on how to migrate from Fuse to Camel.
- migration -> includes template to use when following the guided instructions.
- solution -> Example of migrated source code.


## Service description

The service implemente in Fuse (to migrate to Camel Quarkus) implements a composition workflow. The service exposes a REST operation. When a client posts a request, Camel integrates with a REST (Json) and SOAP endpoint to obtain retrieve data. The data is merged with a data transformation and returned to the caller in JSON format.



