 function arrayToCsv(data){
    return data.map(row =>
      row
      .map(String)
      .map(v => v.trim())
      .map(v => v.replaceAll('"', '""'))
      .map(v => `"${v}"`)
      .join(',')
    ).join('\r\n');
  }

 function downloadCsvFile(fileName, data) {
     var csv = arrayToCsv(data);
     var hiddenA = document.createElement('a');
     hiddenA.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
     hiddenA.target = '_blank';
     hiddenA.download = fileName;
     hiddenA.click();
 }