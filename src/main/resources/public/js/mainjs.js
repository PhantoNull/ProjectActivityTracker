function clickImg(imgString) {
    let img = document.getElementById(imgString);
    img.style.transform = "scale(0.1)";
    img.style.transition = "transform 0.5s ease";
    setTimeout(deClickImg(img), 500);
    console.log("RIMPICCIOLITO");
}

function deClickImg(img){
    img.style.transform = "scale(1)";
    img.style.transition = "transform 0.5s ease";
    console.log("INGRANDITO");
}

function showModal(id){
    document.getElementById(id).classList.add('is-active');
}
function hideModal(id){
    document.getElementById(id).classList.remove('is-active');
}

var tableRowList = [];
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