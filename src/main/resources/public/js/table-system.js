
var tableRowList = [];
tableRowList['add'] = false;

function selectTableRow(id){
    tableRowList[id] = !tableRowList[id];
    if(tableRowList[id]){
        document.getElementById("row"+id).classList.add('is-selected');
        console.log("attivata "+ id);
    }
    else{
        document.getElementById("row"+id).classList.remove('is-selected');
        console.log("disattivata " +id);
    }

}

function sortTable(column, order, columnList) {
    tableRowList = [];
    let table, rows, switching, i, x, y, shouldSwitch;
    table = document.getElementById("sortableTable");
    switching = true;
    i = 3;
    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 3; i < (rows.length - 2); i++) {
            document.getElementById("checkbox" + (i - 3)).checked = false;
            shouldSwitch = false;
            x = document.getElementById(column + (i - 3));
            y = document.getElementById(column + (i - 2));
            if (order === 'asc') {
                if (x.value.toLowerCase() > y.value.toLowerCase())
                    shouldSwitch = true;
            } else if (order === 'desc') {
                if (x.value.toLowerCase() < y.value.toLowerCase())
                    shouldSwitch = true;
            }
            if (shouldSwitch) {
                let row1 = document.getElementById("row" + (i - 3));
                let row2 = document.getElementById("row" + (i - 2));
                let form1 = document.getElementById("form" + (i - 3));
                let form2 = document.getElementById("form" + (i - 2));
                row1.parentNode.insertBefore(row2, row1);
                switching = true;
                row1.id = "row" + (i - 2);
                row2.id = "row" + (i - 3);
                form1.id = "form" + (i - 2);
                form2.id = "form" + (i - 3);
                for (let j = 0; j < columnList.length; j++) {
                    let elemInput1 = document.getElementById(columnList[j] + (i - 3));
                    let elemInput2 = document.getElementById(columnList[j] + (i - 2));
                    elemInput2.id = columnList[j] + (i - 3);
                    elemInput1.id = columnList[j] + (i - 2);
                    if (columnList[j] === "checkbox") {
                        elemInput2.setAttribute("onClick", "selectTableRow(" + (i - 3) + ");");
                        elemInput1.setAttribute("onClick", "selectTableRow(" + (i - 2) + ");");
                    }

                }
                i = 3;
            }
        }
    }
}
function showConfirm(bool) {
    let elem = document.getElementById("confirm-delete");
    if (bool && !tableRowList.every(v => v === false)) {
        tableRowList['99999'] = false;
        document.getElementById("checkboxadd").checked = false;
        if (!tableRowList.every(v => v === false))
            elem.style.display = "block";
    } else
        elem.style.display = "none";
}

function showErrorLog(bool) {
    let elem = document.getElementById("error-log");
    if (bool)
        elem.style.display = "block";
    else
        elem.style.display = "none";
}