import { StrictMode } from "react";
import ReactDOM from "react-dom";

import DataGrid from "./DataGrid";

const dataGrid = document.getElementById("dataGrid1");
ReactDOM.render(
  <StrictMode>
    <DataGrid />
  </StrictMode>,
  dataGrid
);
