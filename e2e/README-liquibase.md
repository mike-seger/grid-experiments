### Generate liquibase schema snapshot from DB
A file from the current DB data can be generated in order to pre-populate a new DB with other than the provided default data in [changelog/](src/main/resources/db/changelog/). 
```
# build
./gradlew clean build

# update (sync) db to current changelog state
./gradlew update -PrunList=update

# create a changelog against application entities 
./gradlew diffChangeLog
