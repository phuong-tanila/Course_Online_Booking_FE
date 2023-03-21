var url = "http://localhost:8080"
var search = document.querySelector("#search_input");
search.addEventListener("input", function(event){
//console.log(event)
//console.log(this.value)
        fetch(url + "/courses/search?name=" + search.value).then(res => res.json()).then(data => {

            let htmls = data.map(c => {
                return `<li>${c.courseName}</li>`

            })
            console.log(data)
            document.querySelector("#search_result").innerHTML = htmls.join('')
        })
})