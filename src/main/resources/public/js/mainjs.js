function setInputFilter(textbox, inputFilter, errMsg) {
    ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop", "focusout"].forEach(function(event) {
        textbox.addEventListener(event, function(e) {
            if (inputFilter(this.value)) {
                // Accepted value
                if (["keydown","mousedown","focusout"].indexOf(e.type) >= 0){
                    this.classList.remove("input-error");
                    this.setCustomValidity("");
                }
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                // Rejected value - restore the previous one
                this.classList.add("input-error");
                this.setCustomValidity(errMsg);
                this.reportValidity();
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                // Rejected value - nothing to restore
                this.value = "";
            }
        });
    });
}

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