### com.bitplan.simplerest
[Needed Base Utilities and Functions for Jersey 1.x RESTFul applications](http://www.bitplan.com/SimpleRest)

[![Travis (.org)](https://img.shields.io/travis/BITPlan/com.bitplan.simplerest.svg)](https://travis-ci.org/BITPlan/com.bitplan.simplerest)
[![Maven Central](https://img.shields.io/maven-central/v/com.bitplan.rest/com.bitplan.simplerest.svg)](https://search.maven.org/artifact/com.bitplan.rest/com.bitplan.simplerest/0.0.25/jar)

[![GitHub issues](https://img.shields.io/github/issues/BITPlan/com.bitplan.simplerest.svg)](https://github.com/BITPlan/com.bitplan.simplerest/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/BITPlan/com.bitplan.simplerest.svg)](https://github.com/BITPlan/com.bitplan.simplerest/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/BITPlan/com.bitplan.simplerest.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

### Documentation
* [Wiki](http://www.bitplan.com/SimpleRest)
* [com.bitplan.simplerest Project pages](https://BITPlan.github.io/com.bitplan.simplerest)
* [Javadoc](https://BITPlan.github.io/com.bitplan.simplerest/apidocs/index.html)
* [Test-Report](https://BITPlan.github.io/com.bitplan.simplerest/surefire-report.html)
### Maven dependency

Maven dependency
```xml
<!-- Needed Base Utilities and Functions for Jersey 1.x RESTFul applications http://www.bitplan.com/SimpleRest -->
<dependency>
  <groupId>com.bitplan.rest</groupId>
  <artifactId>com.bitplan.simplerest</artifactId>
  <version>0.0.25</version>
</dependency>
```

[Current release at repo1.maven.org](https://repo1.maven.org/maven2/com/bitplan/rest/com.bitplan.simplerest/0.0.25/)

### How to build
```
git clone https://github.com/BITPlan/com.bitplan.simplerest
cd com.bitplan.simplerest
mvn install
```
## Version history
*  0.0.1: 2016-06-18 First release via GitHub / Maven central
*  0.0.2: 2016-06-19 adds basic auth support
*  0.0.3: 2016-11-09 Tests Map wrapper
*  0.0.4: 2016-11-17 upgrades to Jersey 1.19.3
*  0.0.5: 2017-03-10 support for Rythm Template Engine
*  0.0.6: 2017-03-18 adds support for TypeConverter
*  0.0.7: 2017-03-19 adds support for moxy Json Provider
*  0.0.8: 2017-03-25 adds support for Postable Interface
*  0.0.9: 2017-03-28 adds implode functionality for multi select handling
* 0.0.10: 2017-12-19 adds Clickstream tracking
* 0.0.11: 2017-12-22 updates most dependencies
* 0.0.12: 2018-08-21 upgrades Eclipse link to fix #9
* 0.0.13: 2018-08-21 applies work-around to fix #9
* 0.0.14: 2018-09-20 adds CORSFilter support to fix #10 (failed attempt)
* 0.0.15: 2018-09-20 adds CORSFilter support to fix #10 (with unit test)
* 0.0.16: 2018-12-14 works around https://github.com/rythmengine/rythmengine/issues/383
* 0.0.17: 2019-01-28 works around https://github.com/alibaba/fastjson/issues/2256
* 0.0.18: 2019-02-03 adds getters and setters for urInfo and request
* 0.0.19: 2019-02-03 adds redirect function to TemplateResource and fixes uri context
* 0.0.20: 2019-02-08 adds reinit function to Manager interface
* 0.0.21: 2019-02-08 fixes #12 redirect support with basic auth
* 0.0.22: 2019-02-09 fixes #13 allow getting principal even when UnsupportedOperationException hits
* 0.0.23: 2019-02-10 fixes #14 use temporaryRedirect instead of seeOther for redirect
* 0.0.24: 2019-02-13 upgrades Apache POI to 4.0.1 to be compatible with other BITPlan projects
