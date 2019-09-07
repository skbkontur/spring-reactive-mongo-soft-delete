[![Build Status](https://travis-ci.org/skbkontur/spring-reactive-mongo-soft-delete.svg?branch=master)](https://travis-ci.org/arrow-kt/arrow/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
![License](https://img.shields.io/github/license/skbkontur/spring-reactive-mongo-soft-delete.svg)
## Spring-mongo-soft-delete

* Configurable fields for deletion marks
* Update documents instead a full deletion

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

