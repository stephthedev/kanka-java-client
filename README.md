# kanka-java-client
A simple java client wrapper for the [Kanka.io API](https://kanka.io/en-US/docs/1.0/overview).

## Overview
A synchronous java client for making CRUD operations on Kanka.io entities. 

## Using the API
The API is currently incomplete but found in [KankaClient.java](./kanka-client-api/src/main/java/com/stephthedev/kankaclient/api/KankaClient.java). 

### Examples
#### Initalize the Client
You will need an authentication token to use this API. The instructions can be found on the [Kanka.io documentation](https://kanka.io/en-US/docs/1.0/setup).

```java
KankaClient client = new KankaClientImpl.Builder()
    .withAuthToken(authToken)
    .withCampaignId(CAMPAIGN_ID)
    .build();
```

#### Character CRUD

##### Generate a Character
```java
 KankaCharacter character = (KankaCharacter) new KankaCharacter.KankaCharacterBuilder<>()
    .withName("Boken Brewfall")
    .withAge("254")
    .withSex("F")
    .withEntry("Dwarf miner found with a black eye on the outskirts of Phandalin")
    .build();
    
 System.out.println(character.getId());   //Null, because it hasn't been created
```
Note: Due to a [limitation with jsonschema2pojo](https://github.com/joelittlejohn/jsonschema2pojo/issues/1104), a concrete builder is not returned from the Builder and instead you need to cast the built object.

##### Create a character
```java
  character = client.createCharacter(character);
  System.out.println(character.getId());   //Non-null
```

#### Get a single character by id
```java
  KankaCharacter halflingCharacter = client.getCharacter(123456L);
```

##### Get multiple characters
```java
  EntitiesRequest request = new EntitiesRequest.Builder().build()
  EntitiesResponse<KankaCharacter> response = client.getCharacters(request);
  List<KankaCharacter> characters = response.getData();
```

##### Update a character
```java
  character.setEntry(character.getEntry() + "Retired from mining to go back to brewing beer")
  character.updateCharacter(character);
```

##### Delete a character
```java
  client.deleteCharacter(123456L);
```

#### Modify the get entities request
##### [Last Sync](https://kanka.io/en-US/docs/1.0/last-sync)
```java
EntitiesRequest request = new EntitiesRequest.Builder()
  .withLastSync("2019-03-21T19:17:42.207577")
  .build();
```

##### [Pagination](https://kanka.io/en-US/docs/1.0/pagination)
```java
EntitiesRequest request = new EntitiesRequest.Builder()
    .withPage(3)
    .build();
```

##### [HATEOAS Link Support](https://restfulapi.net/hateoas/)
```java
EntitiesRequest request = new EntitiesRequest.Builder()
    .withLink("http://kanka.io/api/1.0/campaigns/98765/characters?page=6")
    .build();
```

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
