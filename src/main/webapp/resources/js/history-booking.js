let moreBtns = document.querySelectorAll(".btn-more")
        document.querySelectorAll(".order-item").forEach((o, index) => {
            let arrayProducts = o.children[1].children[0].children
            if (arrayProducts.length - 1 <= 2) {
                moreBtns[index].innerHTML = ""
            }
            console.log(arrayProducts)
            for (let i = 0; i < arrayProducts.length; i++) {
//                console.log(arrayProducts.length)

                if (i >= 2 && i !== arrayProducts.length - 1) {
                    arrayProducts[i].style.display = "none"
                }
            }
        })
        moreBtns.forEach(btn => btn.addEventListener("click", function (e) {
                this.classList.toggle("open");
                let listProductOfAOrder = e.target.parentNode.parentNode.children;

                if (this.classList.contains("open")) {
                    this.innerHTML = `<p>Ẩn bớt</p>`
                    for (let i = 0; i < listProductOfAOrder.length; i++) {
                        if (i >= 2 && i !== listProductOfAOrder.length - 1) {
                            listProductOfAOrder[i].style.display = "flex"
                        }
                    }
                } else {
                    document.querySelectorAll(".order-item").forEach((o, index) => {
                        let arrayProducts = o.children[1].children[0].children
                        this.innerHTML = `<p>Xem thêm</p>`
                        for (let i = 0; i < arrayProducts.length; i++) {
                            if (i >= 2 && i !== arrayProducts.length - 1) {
                                arrayProducts[i].style.display = "none"
                            }
                        }
                    })
                }
            }))