# howto


## Spring Boot application (Server)

```
./gradlew bootRun
```

### Spring Boot application (Server) connecting to postgres (from docker-compose)
```
./gradlew bootRun  --args='--spring.profiles.active=pglocal'
```

### Access Swagger-UI
http://localhost:8080/swagger-ui/index.html

### Access H2 console
http://localhost:8080/h2-console

## JS application (Client)

http://localhost:8080/ui/index.html

### Start alternative local http server

The root is in:
src/main/resources/

```
python3 -m http.server 8000
```
Access application:

http://localhost:8000/static/ui/

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

#### Other gris/tables
- https://jsgrids.statico.io/
- http://tabulator.info/
- https://editor.datatables.net/manual/getting-started
- https://revolist.github.io/revogrid/demo/
- https://canvas-datagrid.js.org/#readme
- https://canvas-datagrid.js.org/demo.html
- https://canvas-datagrid.js.org/sparklineDemo.html
- https://www.ag-grid.com/example/?theme=ag-theme-balham
- https://handsontable.com/demo
- https://glideapps.github.io/glide-data-grid/?path=/story/glide-data-grid-dataeditor-demos--add-data
- https://glideapps.github.io/glide-data-grid/?path=/story/glide-data-grid-dataeditor-demos--freeze-columns
- https://mengshukeji.github.io/LuckysheetDemo/
- https://w2ui.com/web/demos/#/combo/3
- https://adazzle.github.io/react-data-grid/#/common-features
- https://paramquery.com/demos
- https://github.com/paramquery/grid
- https://fancygrid.com/dashboards/big-data-paging/
- https://fancygrid.com/dashboards/employee/
. https://www.htmlelements.com/demos/grid/overview/
- https://www.datagridxl.com/demos/one-million-cells