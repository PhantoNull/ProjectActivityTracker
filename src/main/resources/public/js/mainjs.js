function clickImg(imgString) {
    let img = document.getElementById(imgString);
    img.style.transform = "scale(0.70)";
    img.style.transition = "transform 0.01s ease";
    setTimeout(function(){deClickImg(img)}, 100);
}

function deClickImg(img){
    img.style.transform = "scale(1.0)";
    img.style.transition = "transform 0.01s ease";
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

mybutton = document.getElementById("button-back-top");
window.onscroll = function() {scrollFunction()};

function scrollFunction() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        mybutton.style.display = "block";
    } else {
        mybutton.style.display = "none";
    }
}
function topFunction() {
    document.body.scrollTop = 0; // For Safari
    document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
}