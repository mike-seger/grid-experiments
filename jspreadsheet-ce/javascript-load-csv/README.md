# howto


## Spring Boot application (Server)

```
./gradlew bootRun
```

### Access Swagger-UI
http://localhost:8080/swagger-ui/index.html

### Access H2 console
http://localhost:8080/h2-console

## JS application (Client)

http://localhost:8080/ui/index.html

### Start alternative local http server

The root is in:
src/main/resources/static/

```
python3 -m http.server 8000
```
Access application:

http://localhost:8000/ui/

### Links
- https://bossanova.uk/jspreadsheet/v4/docs/quick-reference
- https://bossanova.uk/jspreadsheet/v4/cases/themes
- https://bossanova.uk/jspreadsheet/v3/docs/programmatically-changes
- https://bossanova.uk/jspreadsheet/v4/examples/tabs
- https://jspreadsheet.com/v8/docs/data
- https://jsuites.net/v4/toolbar
- https://github.com/d3/d3-dsv

autoWidth PRO
- https://www.skypack.dev/view/@jspreadsheet/autowidth
- https://github.com/GBonnaire/jspreadsheet-plugins-and-editors/tree/master/plugins/src/autoWidth
