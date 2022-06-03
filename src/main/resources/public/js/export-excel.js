function extractXLSX(month, year, name){
    let table_elt = document.getElementById("sortableTable");
    let workbook = XLSX.utils.table_to_book(table_elt);

// Process Data (add a new row)
    let ws = workbook.Sheets["Time Sheet"];
    XLSX.utils.sheet_add_aoa(ws, [["Created on "+new Date().toISOString()]], {origin:-1});

// Package and Release Data (`writeFile` tries to write and save an XLSB file)
    XLSX.writeFile(workbook, "Time_Sheet_" + name + "_" + month + "_" + year + ".xlsb");
}