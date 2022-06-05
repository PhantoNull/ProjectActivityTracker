function extractXLSX(month, year, name){
    let table = document.getElementById("sortableTable");
    let wb = XLSX.utils.book_new();

    wb.SheetNames.push("TimeSheet");
    let ws = XLSX.utils.aoa_to_sheet([[ "TimeSheet" ]]);
    wb.Sheets["TimeSheet"] = ws;

    let rowValues = [];
    let rows = table.rows;
    for (let i = 0; i < rows.length; i++) {
        let rowCells = table.rows[i].cells;

        if (i === rows.length - 1) {
            rowValues.push("", "Tot");
        }

        for (let cell of rowCells) {
            if (i === 0) {
                if (cell.classList.contains("table-header") && !cell.classList.contains("checkboxCol")) {
                    let value = cell.textContent;
                    rowValues.push(value);
                }
            } else {
                let childNodes = cell.childNodes;
                for (let child of childNodes) {
                    if (child.nodeName === "FORM") {
                        let grandchildren = child.childNodes;
                        for (let grandchild of grandchildren) {
                            if (grandchild.nodeName === "INPUT" && grandchild.classList.contains("input")) {
                                let value = grandchild.value;
                                rowValues.push(value);
                            }
                        }
                    }
                    if (child.nodeName === "INPUT" && child.classList.contains("input")) {
                        let value = child.value;
                        rowValues.push(value);
                    }
                }
            }

        }
        XLSX.utils.sheet_add_aoa(ws, [rowValues], {origin:-1});
        rowValues = [];
    }

    let strCreated = "Created on "+new Date().toISOString();
    XLSX.utils.sheet_add_aoa(ws, [[strCreated]], {origin:-1});

    ws["!cols"] = [
        {width: strCreated.length},
        {width: "TRASFERTA  ".length}
    ];

    XLSX.writeFile(wb, "Time_Sheet_" + name + "_" + month + "_" + year + ".xlsb");
}