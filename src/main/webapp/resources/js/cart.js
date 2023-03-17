let background = document.querySelector(".overlay");
 let checkoutBtn = document.querySelector(".checkout-button");
     let modalPayPal = document.querySelector(".cart-paypal");
    let closeModalButton = document.querySelector(".modal-close-button");

        (function handleFormBackground() {
checkoutBtn.addEventListener("click", () => {
                background.style.display = "block";
                modalPayPal.classList.add("open");
                if (modalPayPal.classList.contains("open")) {
                    modalPayPal.style.cssText = `
    display: block;
    opacicty: 1;
    visibility: visible;
    top: 150px;
    left: 50%;
    margin: 0 auto;
    max-height: 90%;
    max-width: 95%;
    outline: 0;
    overflow: hidden;
    padding: 0;
    right: 0;
    top: 50% !important;
    transform: translate(-50%, -50%);
    position: fixed;
    overflow-y:scroll`
                }
            })

            closeModalButton.addEventListener("click", () => {
                modalPayPal.classList.remove("open");
                background.style.display = "none";
                if (!modalPayPal.classList.contains("open")) {
                    modalPayPal.style.cssText = ``;

                }
            });
        })();


        let cart = JSON.parse(window.localStorage.getItem('cart'));
        let fieldShopID = document.querySelector(".shopID")

        let cartList = document.querySelector('.cart')
        let total = 0;
//        let cart = JSON.parse(window.localStorage.getItem('cart'));

        if (cart == null) {
            document.querySelector(".cart-container").innerHTML = `<div class='empty-cart-img'>
            <img class='empty-img' src="assets/images/emptyCart.png" alt="alt"/>
<a href='<c:url value="MainController" />' class='href-homePage'>Đi mua sắm</a>
 </div>

`
        }

        const unique = [...new Set(cart.map(item => item.shopID))];
        unique.forEach(u => {

            fetch("MainController?btnAction=cart&cartAction=render&shopID=" + u, {
                method: 'GET'
            })
                    .then(res => res.json())
                    .then(data => {

                        cartList.innerHTML += ` <tbody class="cart-name-shop">

                        <tr class="cart-item shop-name"><td class="name"><a href="#"><i class="fa-solid fa-store"></i>\${data.shopName}</a></td></tr>`
                        let htmls = cart.map(item => {
                            console.log(parseInt(item.price.trim().replace(".0", "")))
                            if (data.shopID == item.shopID) {
                                return `

                    <tr class="cart-item">
                        <td class="cart-item cart-item-image">
                            <img src="\${item.srcImg}" alt="">
                        </td>
                        <td class="cart-item cart-item-title">

                            <h4 class="cart-item-name">

                                <a href="<c:url value="MainController?btnAction=product&productAction=showDetail&productID=\${item.productID}" />">
                            \${item.productName}
                                </a>
                            </h4>
                        </td>
                        <td class="cart-item cart-item-price">
                            <span class="cart-item-price-value">\${item.price}</span>
                        </td>
                       <input type="hidden" class="productID" value="\${item.productID}"/>
                        <td class="cart-item cart-item-quantity">
                            <div class="increment">

                                <div class="box-id" style="display: flex; justify-content: center; align-items: center;">
                                    <div class="increase"  onclick ="increaseValue(this,\${item.productID})"><i class="fa-solid fa-angle-up"></i>
                                    </div>
                                    <input class="form-input quantity-input" type="number" id="quantity" value="\${item.quantity}" onchange="handleOnChangeQuantity(this,\${item.productID})"  min="1">

                                    <div class="decrease" onclick ="decreaseValue(this,\${item.productID},\${data.shopID})"><i class="fa-solid fa-angle-down"></i>
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td class="cart-item cart-item-total">
                            <span class="cart-item-total">\${formatter.format(parseInt(item.price.replaceAll(".","")) * item.quantity)}</span>
                            <div class="close" onclick="deleteAnItem(this,\${item.productID},\${data.shopID})">
                                <span class="modal-close-button fa-solid fa-x">
                                </span>
                            </div>
                        </td>
                    </tr>

                    `
                            }
                        })
                        cartList.innerHTML += `</tbody>`
                        cartList.innerHTML += htmls.join('')
                    }
                    )
        })


        function handleOnChangeQuantity(btn, productID) {

            fetch("MainController?btnAction=product&productAction=showQuantity&productID=" + productID, {
                method: 'GET'
            })
                    .then(res => res.json())
                    .then(data => {
                        if (data < btn.value) {
                            swal("", "Sản phẩm này chỉ còn " + data, "error");
                            btn.value = data
                        }
                        if (btn.value < 0) {
                            btn.value = Math.abs(btn.value)
                        } else if (btn.value == 0) {
                            btn.value = 1
                        }
                        handleTotalItem(btn, btn.value)
                        setQuantityWithDeIncrease(btn.value, productID)
                        total = calculateTotal()
                        document.querySelector(".total").innerHTML = `<span>` + formatter.format(total) + `</span>`
                    })

//                    calculateTotal()



        }


        function increaseValue(btn, productID) {
            let value = btn.nextElementSibling.value
            value++

            value = isNaN(value) ? 1 : value;
            fetch("MainController?btnAction=product&productAction=showQuantity&productID=" + productID, {
                method: 'GET'
            })
                    .then(res => res.json())
                    .then(data => {
                        if (data < value) {
                            swal("", "Sản phẩm này chỉ còn " + data, "error");
                            value = value - 1

                        }
                        handleTotalItem(btn, value)
                        setQuantityWithDeIncrease(value, productID)
                        btn.nextElementSibling.value = value
//                        calculateTotal()
                    })

//            setQuantityWithDeIncrease(value, productID)
        }
        function decreaseValue(btn, productID, shopID) {
            let value = btn.previousElementSibling.value
            value--;
            if (value == 0) {
                console.log("delete thôi")
                value = 1;
                deleteAnItem(btn.parentNode.parentNode.parentNode.nextElementSibling.childNodes[3], productID, shopID)

            }

            handleTotalItem(btn, value)
            setQuantityWithDeIncrease(value, productID)

            value = isNaN(value) ? 1 : value;

            value < 1 ? value = 1 : value;
            btn.previousElementSibling.value = value

        }
        function setQuantityWithDeIncrease(value, productID) {
            cart.forEach(item => {
//                console.log(item.productID == productID)
//                console.log(value)
                if (item.productID == productID) {
                    item.quantity = value;
                }


            })
            window.localStorage.setItem('cart', JSON.stringify(cart))
            total = calculateTotal()
            document.querySelector(".total").innerHTML = `<span>` + formatter.format(total) + `</span>`
        }
//delete a item from cart
        function deleteAnItem(deleleButton, productID, shopID) {

            swal({
                title: "Xóa sản phẩm",
                text: "Bạn có muốn xóa?",
                icon: "warning",
                dangerMode: true,
                buttons: {
                    cancel: {
                        text: "Hủy",
                        value: null,
                        visible: true,
                        className: "",
                        closeModal: true,
                    },
                    confirm: {
                        text: "Xác nhận",
                        value: true,
                        visible: true,
                        className: "",
                        closeModal: true
                    }
                }
            })
                    .then((willDelete) => {

                        if (willDelete) {
                            let indexItemInCart = cart.findIndex(item => item.productID == productID)
                            cart.splice(indexItemInCart, 1)
                            let isShopStillInCart = cart.findIndex(cartItem => cartItem.shopID == shopID)
                            console.log(isShopStillInCart < 0)
                            if (isShopStillInCart < 0) {
                                deleleButton.parentNode.parentNode.parentNode.previousElementSibling.remove()

                            }

                            deleleButton.parentNode.parentNode.remove()
                            window.localStorage.setItem('cart', JSON.stringify(cart))
                            let total = calculateTotal()
                            document.querySelector(".total").innerHTML = `<span>` + formatter.format(total) + `</span>`
                            swal("Đã xóa", {
                                icon: "success",
                            });
                            if (cart.length == null) {
                                document.querySelector(".cart-container").innerHTML = `<div class='empty-cart-img'>
            <img class='empty-img' src="assets/images/emptyCart.png" alt="alt"/>
<a href='<c:url value="MainController" />' class='href-homePage'>Đi mua sắm</a>
 </div>

`
                            }

                        } else {
                            swal("Bạn đã hủy xóa sản phẩm");


                        }

                    })
        }
        function handleTotalItem(btn, quantity) {
//            const f = Intl.
            let currentPrice = btn.parentNode.parentNode.parentNode.parentNode.childNodes[5].childNodes[1].innerHTML

            let totalPriceEachItemByQuantity = btn.parentNode.parentNode.parentNode.nextElementSibling.childNodes[1]
            totalPriceEachItemByQuantity.innerHTML = formatter.format(parseInt(currentPrice.trim().replaceAll(".", "")) * quantity)

        }
//        document.querySelector('.checkout-button').addEventListener('click', function (e) {

//            e.preventDefault()
//            console.log('click')
        function calculateTotal() {

            let total = cart.reduce(function (total, currentItem) {
                console.log("item " + formatter.format(1000 + parseInt(currentItem.price.replaceAll(".", "")) * currentItem.quantity))

                console.log("quantity " + currentItem.quantity)
                return total + parseInt(currentItem.price.replaceAll(".", "")) * currentItem.quantity
            }, 0)
//            console.log(formatter.format(total))
            return total
//            console.log("length: " + cart.length)


        }

        var myHeaders = new Headers();
        myHeaders.append("apikey", "0glu83Z8MQm554mvJYUFnrcIo8Ih2SBP");

        var requestOptions = {
            method: 'GET',
            redirect: 'follow',
            headers: myHeaders
        };

        const getTotal = () => {

            return  new Promise(function (res) {
                return res(calculateTotal())
            })
        }
        window.onload = function () {
            total = calculateTotal()
            console.log(total)
            document.querySelector(".total").innerHTML = `<span>` + formatter.format(total) + `</span>`
            if (${sessionScope.user.wardID} !== 0) {
                fetch("MainController?btnAction=address&addressAction=getWard&wardID=" + `${sessionScope.user.wardID}`, {
                    method: 'GET'
                })
                        .then(res => res.json())
                        .then(ward => {
                            document.querySelector(".cart-address").innerHTML = "Phường/Xã: " + ward.name + " - "
                            return new Promise(function (res) {
                                return res(fetch("MainController?btnAction=address&addressAction=getDistrict&districtID=" + `\${ward.districtID}`, {
                                    method: 'GET'
                                }))
                            })
                                    .then(res => res.json())
                                    .then(district => {
                                        document.querySelector(".cart-address").innerHTML += "Quận/Huyện: " + district.name + " - "
                                        return new Promise(function (res) {
                                            return res(fetch("MainController?btnAction=address&addressAction=getCity&cityID=" + `\${district.cityID}`, {
                                                method: 'GET'
                                            }))
                                        })
                                    })
                                    .then(res => res.json())
                                    .then(city => {
                                        document.querySelector(".cart-address").innerHTML += "Tỉnh/Thành Phố: " + city.name
                                        document.querySelector(".cart-address").innerHTML += `<a href="<c:url value="/MainController?btnAction=user&userAction=profile#profile" />">Sửa địa chỉ</a>`
                                    })
                        }


                        )
            }
        }

        document.querySelector(".checkout-button").addEventListener('click', function () {
            console.log("click")
            getTotal()
                    .then(total => {

                        document.querySelector("#paypal-button-container").innerHTML = `<div class="lds-spinner"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>`
                        return new Promise(function (res) {
                            return res(fetch("https://api.apilayer.com/exchangerates_data/convert?to=USD&from=VND&amount=" + total, requestOptions))
                        })
                    })
                    .then(response => response.json())
                    .then(data => {
                        console.log(data.result)
                        return new Promise(function (res) {
                            return res(data)
                        })

                    })
                    .then(rs => {
                        document.querySelector("#paypal-button-container").innerHTML = ""
                        console.log(rs.result)
                        paypal.Buttons({
                            // Order is created on the server and the order id is returned
                            createOrder: function (data, actions) {
                                return actions.order.create(
                                        {
                                            purchase_units: [{
                                                    amount: {
                                                        value: Math.ceil(rs.result),
                                                    }
                                                }]
                                        })
                            },
                            // Finalize the transaction on the server after payer approval
                            onApprove: function (data, actions) {
                                return actions.order.capture().then(function (details) {
                                    console.log("details: " + details)
                                    swal("Thanh toán thành công", "Cảm ơn bạn!", "success");
                                    modalPayPal.classList.remove("open");
                                    background.style.display = "none";
                                    if (!modalPayPal.classList.contains("open")) {
                                        modalPayPal.style.cssText = ``;
                                    }

                                })
                                        .then(() => {
//                                            console.log("aaaa")
//                                            let total = calculateTotal();
                                            const map = new Map();
                                            cart.forEach(item => {
                                                map.set(item.productID, item.quantity)
                                            })
                                            const obj = Object.fromEntries(map)
                                            console.log(map)
                                            console.log(JSON.stringify(obj))
                                            fetch("MainController?btnAction=cart&cartAction=orderPaypal&cart=" + encodeURIComponent(JSON.stringify(obj)), {
                                                method: 'POST'
                                            })
                                        })
                                        .then(() => {
                                            localStorage.removeItem("cart")
                                            cartList.innerHTML = ""
                                            document.querySelector(".checkout-button").style.display = "none"
                                            document.querySelector(".total").innerHTML = ""
                                        })
                            }
                        }).render('#paypal-button-container');

                    })
                    .catch(error => console.log('error', error));
            document.querySelector(".total").innerHTML = `<span>` + formatter.format(total) + `</span>`
        })
