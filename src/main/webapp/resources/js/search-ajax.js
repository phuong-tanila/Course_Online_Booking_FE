var url = "http://localhost:8080"
var search = document.querySelector("#search_input");
search.addEventListener("input", function(event){
    if(search.value == ''){
                document.querySelector("#search_result").innerHTML = ''
            }else{
                fetch(url + "/courses/search?name=" + search.value).then(res => res.json()).then(data => {
                        let htmls = data.map(c => {
                            return `<li><a href="/course-detail/${c.id}">${c.courseName}</a></li>`

                        })
                        console.log(data)
                        document.querySelector("#search_result").innerHTML = htmls.join('')
                    })

            }
    })
