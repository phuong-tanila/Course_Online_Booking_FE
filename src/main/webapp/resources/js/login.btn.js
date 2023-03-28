// Get the modal
var modal = document.getElementById("login-form");

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
};


var url = "http://localhost:8080"
var search2 = document.querySelector("#search2_input");
search2.addEventListener("input", function(event){

if(search2.value == ''){

document.querySelector("#search2_result").innerHTML = ''
}else{
fetch(url + "/courses/search?name=" + search2.value).then(res => res.json()).then(data => {
let htmls = data.map(c => {
return `<li><a href="/course-detail/${c.id}">${c.courseName}</a></li>`

})
console.log(data)
document.querySelector("#search2_result").innerHTML = htmls.join('')
})

}
})