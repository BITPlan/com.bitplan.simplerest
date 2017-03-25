# com.bitplan.simplerest
Needed Base Utilities and Functions for Jersey 1.x RESTFul applications

# API
see https://github.com/BITPlan/com.bitplan.simplerest-api

# Issues
http://stackoverflow.com/q/37775309/1497139

### Project
[![Build Status](https://travis-ci.org/BITPlan/com.bitplan.simplerest.svg?branch=master)](https://travis-ci.org/BITPlan/com.bitplan.simplerest)

* Open Source hosted at https://github.com/BITPlan/com.bitplan.simplerest
* License based on license of libraries used (see [pom.xml](https://github.com/BITPlan/com.bitplan.simplerest/blob/master/pom.xml))
* Maven based Java project including JUnit 4 tests.

### How to build
```
git clone https://github.com/BITPlan/com.bitplan.simplerest
cd com.bitplan.simplerest
mvn install
```

### Distribution
Available at Maven Central see 

http://search.maven.org/#artifactdetails|com.bitplan.rest|com.bitplan.simplerest|0.0.8|jar

Maven dependency:

```xml
<dependency>
	<groupId>com.bitplan.rest</groupId>
	<artifactId>com.bitplan.simplerest</artifactId>
	<version>0.0.8</version>
</dependency>
```
## Tests
There are 19 JUnit test for version 0.0.8

## Version history
* 0.0.1: 2016-06-18 First release via GitHub / Maven central
* 0.0.2: 2016-06-19 adds basic auth support
* 0.0.3: 2016-11-09 Tests Map wrapper
* 0.0.4: 2016-11-17 upgrades to Jersey 1.19.3
* 0.0.5: 2017-03-10 support for Rythm Template Engine
* 0.0.6: 2017-03-18 adds support for TypeConverter
* 0.0.7: 2017-03-19 adds support for moxy Json Provider
* 0.0.8: 2017-03-25 adds support for Postable Interface