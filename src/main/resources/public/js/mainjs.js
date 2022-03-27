function clickImg(imgString) {
    let img = document.getElementById(imgString);
    img.style.transform = "scale(0.1)";
    img.style.transition = "transform 0.5s ease";
    setTimeout(deClickImg(img), 0.5);
    console.log("RIMPICCIOLITO");
}

function deClickImg(img){
    img.style.transform = "scale(1)";
    img.style.transition = "transform 0.5s ease";
    console.log("INGRANDITO");
}

var listaRighe = [];
function selezionaRigaTabella(id){
    listaRighe[id] = !listaRighe[id];
    console.log(id);
    if(listaRighe[id]){
        document.getElementById(id).classList.add('is-selected');
        console.log("attivata "+ id);
    }
    else{
        document.getElementById(id).classList.remove('is-selected');
        console.log("disattivata " +id);
    }

}