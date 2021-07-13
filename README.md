### CouchDB Java
A CouchDB Java library that is (mostly) uncoupled from any particular JSON library.

There are many outdated CouchDB libraries, and some more up to date CouchDB libraries for Java. Some of these
libraries are tightly coupled to JSON libraries such as GSON or Jackson. This library uses Jackson internally,
but you can interact with this library using strings representing JSON values.

This library encourages the use of immutable objects as much as possible and doesn't force you to put `_id` and `_rev`
on your objects like many other libraries do.

This library works great with Kotlin. Much of the code is annotated with annotations to denote nullability.

### Using library:
```groovy
allprojects {
  repositories {
    maven { url 'https://www.jitpack.io' }
  }
}
dependencies {
  implementation "com.github.retrodaredevil:couchdb-java:$couchdbJavaVersion"
}
```


### Running Tests:
Running test and building:
```shell
./gradlew build
```
Running integration tests:
```shell
./gradlew integration
```

### Other CouchDB libraries for Java
* [LightCouch](https://github.com/lightcouch/LightCouch)
  * Uses GSON and is tightly coupled to GSON
  * Uses inheritance rather than composition to support alternative HTTP libraries on Android
* [Ektorp](https://github.com/helun/Ektorp)
  * Uses Jackson and is tightly coupled to Jackson
    * Allows a single ObjectMapper to be configured for all serialization and deserialization
  * Updating objects without `_id` and `_rev` on them is awkward
* [couchdb4j](https://github.com/mbreese/couchdb4j)
