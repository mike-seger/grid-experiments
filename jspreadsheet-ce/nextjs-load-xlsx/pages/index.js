import jspreadsheet from "jspreadsheet";
import formula from "@jspreadsheet/formula-pro";
import parser from "@jspreadsheet/parser";
import "../node_modules/jspreadsheet/dist/jspreadsheet.css";
import "../node_modules/jsuites/dist/jsuites.css";

// Create the spreadsheet from a local file
let load = function (e) {
  // Precision
  formula.adjustPrecision = true;
  // Formula
  // jspreadsheet.setLicense(
  //   "ZjMyODc0OTRhZmFhNDE0MGRhMGNkZjg0ZWZlODljOWNjNjcxYzdkOWFiNDFmZDQ3ZTM1MmRmZmNiMGFkOThhOTg4NGQ0MzM0ZDdjNGRlYjM3OGZkZWJjOWJhNDMzZTcwNGFlODFhMTA5YzYwZjQwOGQyZjQ1OGZjOGYwZjlmYjYsZXlKdVlXMWxJam9pY0dGMWJDNW9iMlJsYkNJc0ltUmhkR1VpT2pFMk56UXhOekk0TURBc0ltUnZiV0ZwYmlJNld5SnFjMmhsYkd3dWJtVjBJaXdpYW5Od2NtVmhaSE5vWldWMExtTnZiU0lzSW1OellpNWhjSEFpTENKMVpTNWpiMjB1WW5JaUxDSjFibWwwWldRdVpXUjFZMkYwYVc5dUlpd2ljMkZ2Y205amF5NWpiMjBpTENKMVpTNWpiMjB1WW5JaUxDSjFibWwwWldRdVpXUjFZMkYwYVc5dUlpd2lZMjlrWlhOaGJtUmliM2d1YVc4aUxDSnpjMlV1WTI5a1pYTmhibVJpYjNndWFXOGlMQ0pzYjJOaGJHaHZjM1FpWFN3aWNHeGhiaUk2SWpNaUxDSnpZMjl3WlNJNld5SjJOeUlzSW5ZNElpd2labTl5YlhWc1lTSXNJbVp2Y20xeklpd2ljbVZ1WkdWeUlpd2ljR0Z5YzJWeUlpd2lhVzF3YjNKMFpYSWlYWDA9"
  // );
  // Extensions
  jspreadsheet.setExtensions({ formula, parser });
  // Destroy any existing JSS
  jspreadsheet.destroy(document.getElementById("spreadsheet"));

  // Parse XLSX file and create a new spreadsheet
  jspreadsheet.parser({
    file: e.target.files[0],
    onload: function (config) {
      jspreadsheet(document.getElementById("spreadsheet"), config);
    }
  });
};

export default function App() {
  return (
    <div>
      <div id="spreadsheet"></div>
      <input type="file" name="file" onChange={(e) => load(e)} />
    </div>
  );
}
