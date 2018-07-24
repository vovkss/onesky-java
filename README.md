# OneSky Java API Library

[![Java 11+](https://img.shields.io/badge/java-11%2B-blue.svg)](http://java.oracle.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/vovkss/onesky-java/master/LICENSE.md)
[![Build Status](https://travis-ci.org/vovkss/onesky-java.png?branch=master)](https://travis-ci.org/vovkss/onesky-java)

Java wrapper for [OneSky](http://oneskyapp.com/) API.

For the list of API wrappers in other programming languages, see the [OneSky Platform API documentation](https://github.com/onesky/api-documentation-platform/blob/master/README.md).


## Dependency Information

The library is available in Maven Central Repository, the coordinates are: 

    info.datamuse:onesky-java:1.0.0

For example, Apache Maven dependency would be:
    
    <dependency>
      <groupId>info.datamuse</groupId>
      <artifactId>onesky-java</artifactId>
      <version>1.0.0</version>
    </dependency>


## How to use

**Create a client instance:**

    // Create the HttpClient and configure it as needed
    var httpClient = HttpClient.newHttpClient();
    
    var oneSkyClient = new OneSkyClient(
        "<api-key>", "<api-secret>", httpClient
    );


**Asynchronous vs synchronous usage:**

Every API call returns a `CompletableFuture` which allows asynchronous API usage, e.g.:

    oneSkyClient.locales().list().whenComplete(
        (locales, throwable) -> {
            if (locales != null) {
                final String localesString = locales.stream().map(Locale::toString).collect(Collectors.joining(", "));
                System.out.println("OneSky locales: " + localesString);
            } else if (throwable != null) {
                System.out.println("OneSky API call failed: " + throwable.getMessage());
            }
        }
    );

To use the API synchronously, just invoke `CompletableFuture.join()`, e.g.:

    List<Locale> locales = oneSkyClient.locales().list().join();


**Project Types API example:**

    List<OneSkyProjectTypesApi.ProjectType> projectTypes = oneSkyClient.projectTypes().list().join();
    projectTypes.forEach(projectType -> 
        System.out.println(projectType.getCode() + ": " + projectType.getName())
    );


**Locales API example:**

    List<Locale> oneSkyLocales = oneSkyClient.locales().list().join();


## Authors and contributors

* [Alex Shesterov](https://www.linkedin.com/in/alexshesterov/)

* [Vlad Dvoretskyi](https://www.linkedin.com/in/vladislav-dvoretskiy-17528419/)

