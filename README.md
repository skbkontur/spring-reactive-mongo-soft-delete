[![Build Status](https://travis-ci.org/skbkontur/spring-reactive-mongo-soft-delete.svg?branch=master)](https://travis-ci.org/arrow-kt/arrow/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
![License](https://img.shields.io/github/license/skbkontur/spring-reactive-mongo-soft-delete.svg)
## Spring-mongo-soft-delete

* Configurable fields for deletion marks
* Update documents instead a full deletion

### Set up

#### With Gradle

Add to your gradle:

```
repositories {
    maven {
        url  "https://dl.bintray.com/kostya05983/kontur" 
    }
}

dependencies {
    implementation "ru.kontur:spring-reactive-mongo-soft-delete:1.0.0"
}
```

#### With Maven

Add next settings:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
          xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
    
    <profiles>
        <profile>
            <repositories>
                <repository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-kostya05983-kontur</id>
                    <name>bintray</name>
                    <url>https://dl.bintray.com/kostya05983/kontur</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-kostya05983-kontur</id>
                    <name>bintray-plugins</name>
                    <url>https://dl.bintray.com/kostya05983/kontur</url>
                </pluginRepository>
            </pluginRepositories>
            <id>bintray</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>bintray</activeProfile>
    </activeProfiles>
</settings>
```

And add to your dependency section:
```xml
<dependency>
      <groupId>ru.kontur</groupId>
      <artifactId>spring-reactive-mongo-soft-delete</artifactId>
      <version>1.0.0</version>
      <scope>compile/scope>
    </dependency>
```

### Settings

* enabled - enable, disable soft deletion of records
* deleteField - the name of field for boolean value
* deletedAt - the name of field with the date of deletion

```yaml
ru:
    kontur:
      soft-delete:
        enabled: true
        deleteField: deleted
        dateField: deletedAt
```

