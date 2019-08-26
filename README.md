## Spring-mongo-soft-delete

* Configurable fields for deletion marks
* Update documents instead a full deletion

### Settings

* enabled - enable, disable soft deletion of records
* deleteField - the name of field for boolean value
* deletedAt - the name of field with the date of deletion

```yaml
ru:
    it:
      zoo:
        soft-delete:
         enabled: true
         deleteField: deleted
         dateField: deletedAt
```

