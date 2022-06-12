import React, { useRef, useEffect } from "react";
import jspreadsheet from "jspreadsheet-ce";

import "../node_modules/jspreadsheet-ce/dist/jspreadsheet.css";
//import "../node_modules/jspreadsheet-ce/dist/jexcel.css";
import "./styles.css";

export default function App() {
  const jRef = useRef(null);
  const options = {
    csv:'http://localhost:8080/resource/data.csv',
    csvHeaders:true,
    tableOverflow:true,
    columns: [
        { type:'text', width:300 },
        { type:'text', width:80 },
        { type:'dropdown', width:120, source:['England','Wales','Northern Ireland','Scotland'] },
        { type:'text', width:120 },
        { type:'text', width:120 },
     ],
//    data: [[]],
//    minDimensions: [10, 10],
    allowInsertRow:true,
    allowManualInsertRow:false,
    allowInsertColumn:false,
    allowManualInsertColumn:false,
    allowDeleteRow:false,
    allowDeletingAllRows:false,
    allowDeleteColumn:false,
    allowRenameColumn:false
  };

  useEffect(() => {
    if (!jRef.current.jspreadsheet) {
      jspreadsheet(jRef.current, options);
    }
  }, [options]);

  const addRow = () => {
    jRef.current.jexcel.insertRow();
  };

  const download = () => {
    jRef.current.jexcel.download();
  };

  return (
    <div>
      <div ref={jRef} />
      <br />
      <input type="button" onClick={download} value="Download" />
    </div>
  );
}
