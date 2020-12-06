# kanka-java-client
A simple java client wrapper for the [Kanka API](https://kanka.io/en-US/docs/1.0/overview).

## Overview
Coming Soon!

## Using the API
The API is found in [KankaClient.java](./kanka-client-api/src/main/java/com/stephthedev/kankaclient/api/KankaClient.java). 

## Contributing to the API
Send a PR with your changes for a quick review. 

### Overview
1. Creates several [kanka json schemas](./kanka-client-api/src/main/resources/schema) to represent
   [Kanka entities](https://kanka.io/en-US/docs/1.0/entities).
1. Transforms schemas into Java pojos using [jsonschema2pojo](https://github.com/joelittlejohn/jsonschema2pojo)
1. Kanka pojo entities are then used in the Kanka Client API and 
[implementation](./kanka-client-impl/src/main/java/com/stephthedev/kankaclient/impl/KankaClientImpl.java). 
   
### Setup
#### Prerequisites
* JDK 8+
* Maven

#### Install it
1. Clone the repository
2. `mvn clean install`
