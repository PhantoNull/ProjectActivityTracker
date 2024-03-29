function extractXLSX(month, year, username, global, festdaysArray) {
    let table = document.getElementById("sortableTable");
    let wb = XLSX.utils.book_new();

    let rowValues = [];
    let totRowValues = [];
    let rows = table.rows;
    let startingRow = 0;
    //Global inizia dalla row 2 saltando le righe 0 e 1 (header filtro)
    if(global)
        startingRow = 2;
    console.log(festdaysArray);

    //Per ogni riga della tabella HTML
    for (let i = startingRow; i < rows.length; i++) {
        //Se la riga è hidden O è l'ultima riga del global (rimuove riga subtotale) salta
        if(table.rows[i].classList.contains("hidden") || (i === rows.length-1 && global))
            continue;
        //Ottiene tutte le celle della riga
        let rowCells = table.rows[i].cells;
        //Per l'ultima riga non global pusha una cella vuote e Tot
        if (i === rows.length - 1) {
            if(global) {
                rowValues.push("", "", "Tot");
                for(let j=6; j<=rowCells.length; j++)
                    rowValues.push("")
            }
            else
                rowValues.push("", "Tot");
        }
        //Indice colonna
        let index = 0;
        //Per ogni cella della riga
        for (let cell of rowCells) {
            //Se è table-header e non è la colonna checkbox aggiunge instestazione formattata con colore
            if (cell.classList.contains("table-header") && !cell.classList.contains("checkboxCol")) {
                let value = { v: cell.textContent,
                              t: "s",
                              s: { font: { bold: true, color: { rgb: "f9f9f9" } }, alignment: { horizontal: "center"}, fill: { fgColor: { rgb: "267183" } } } };
                rowValues.push(value);
            //Altrimenti contiene dati
            } else {
                let childNodes = cell.childNodes;
                for (let child of childNodes) {
                    //Se è un form
                    if (child.nodeName === "FORM") {
                        console.log("A");
                        let grandchildren = child.childNodes;
                        for (let grandchild of grandchildren) {
                            if (grandchild.nodeName === "INPUT" && grandchild.classList.contains("input")) {
                                let value = { v:grandchild.value, t: "s", s: {alignment: { horizontal: "center"}}};
                                if(index > 2 && global && festdaysArray.indexOf(index-2) >= 0)
                                    value = { v:grandchild.value, t: "s", s: { alignment: { horizontal: "center"}, fill: { fgColor: { rgb: "ececec" } } }};
                                rowValues.push(value);
                                index = index + 1;
                            }
                        }
                    }
                    if (child.nodeName === "INPUT" && child.classList.contains("input")) {
                        console.log("B");
                        let value = { v:child.value, t: "s", s: {alignment: { horizontal: "center"}}};
                        if(index > 2 && global && festdaysArray.indexOf(index-2) >= 0)
                            value = { v:child.value, t: "s", s: { alignment: { horizontal: "center"}, fill: { fgColor: { rgb: "ececec" } } }};
                        rowValues.push(value);
                        index = index + 1;
                    }
                }
            }
        }
        console.log(rowValues);
        totRowValues.push(rowValues);
        rowValues = [];
    }
    let ws = XLSX.utils.aoa_to_sheet(totRowValues);
/*
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
    */
    XLSX.utils.book_append_sheet(wb, ws, "TimeSheet");
    XLSX.writeFile(wb, "TimeSheet_" + year + "_" + month + "_" + username + ".xlsx");
}