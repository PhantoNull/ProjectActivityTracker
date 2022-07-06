function extractXLSX(month, year, username, global){
    let table = document.getElementById("sortableTable");
    let wb = XLSX.utils.book_new();

    let ws = XLSX.utils.aoa_to_sheet([ ]);

    wb.Sheets["TimeSheet"] = ws;
    let rowValues = [];
    let rows = table.rows;
    let startingRow = 0;
    if(global)
        startingRow = 2;
    for (let i = startingRow; i < rows.length; i++) {
        if(table.rows[i].classList.contains("hidden"))
            continue;
        let rowCells = table.rows[i].cells;

        if (i === rows.length - 1) {
            if(global) {
                rowValues.push("", "", "Tot");
                for(let j=6; j<=rowCells.length; j++)
                    rowValues.push("")
            }
            else
                rowValues.push("", "Tot");
        }

        for (let cell of rowCells) {
            if (cell.classList.contains("table-header") && !cell.classList.contains("checkboxCol")) {
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
    XLSX.utils.book_append_sheet(wb, ws, "TimeSheet");

    ws["!cols"] = [
        {width: "Nome utente lungo   ".length},
        {width: "BPER-ORCH-22:DEV-22     ".length},
        {width: "TRASFERTA  ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length},
        {width: "    ".length}
    ];
    console.log(typeof ws["!cols"]);
    if(!global)
        ws["!cols"] = ws["!cols"].slice(1);
    console.log(ws["!cols"]);
    XLSX.writeFileXLSX(wb, "TimeSheet_" + year + "_" + month + "_" + username + ".xlsx");
}