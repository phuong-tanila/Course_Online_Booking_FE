                                                                let totalMoney = 0;
                                                                const formatter = new Intl.NumberFormat('vi-VN', {
                                                                    style: 'currency',
                                                                    currency: 'VND',
                                                                });
                                                                const handlePayment = (status) => {
                                                                    console.log(status)
                                                                    if (status == 'not logged in') {
                                                                        document.querySelector("#hiddenEmail").value = document.querySelector("#email").value
                                                                        document.querySelector("#hiddenFullName").value = document.querySelector("#fullName").value
                                                                        document.querySelector("#hiddenPhone").value = document.querySelector("#phone").value
                                                                    }
                                                                    document.querySelector("#paymentMethod").value = document.querySelector(".opa-1").classList.contains("fa-brands fa-cc-paypal") ? "paypal" : "cash";
                                                                    document.querySelector("#total").value = document.querySelector('.total-money').innerHTML.replace(".", "").replace("&nbsp;", "").replace("VNĐ", "");
                                                                    document.querySelector("#hiddenForm").submit();
                                                                }
                                                                const handleDelete = (el, id) => {
                                                                    swal("Do you want to delete this course out of cart?", {
                                                                        dangerMode: true,
                                                                        buttons: true,
                                                                    }).then(result => {
                                                                        if (result) {
                                                                            el.parentElement.parentElement.parentElement.remove()
                                                                            calculateMoney();
                                                                            fetch('<c:url value="MainController?btnAction=cart&func=delete&id="/>' + id)
                                                                        }
                                                                    })
                                                                }
                                                                const calculateMoney = () => {
                                                                    totalMoney = 0
                                                                    cartItems = document.querySelectorAll(".itemMoney");
                                                                    if (cartItems.length == 0) {
                                                                        document.querySelector(".btn.btn-primary.btn-lg.btn-block").disabled = true;
                                                                        return;
                                                                    }
                                                                    cartItems.forEach(i => {
                                                                        console.log(i)
                                                                        totalMoney += parseInt(i.innerHTML.replace("đ", "").replace(".", "").trim())
                                                                    })
                                                                    document.querySelector(".total-money").innerHTML = formatter.format(totalMoney)
                                                                    document.querySelector(".cart-size").innerHTML = cartItems.length;
                                                                    if (cartItems.length <= 1) {
                                                                        document.querySelector('.letter-s').innerHTML = "";
                                                                    } else {
                                                                        document.querySelector('.letter-s').innerHTML = "s";
                                                                    }
                                                                }
                                                                calculateMoney()

                                                                let prevMethodIsPaypal = false;
                                                                function renderDefaultBuyButton(isLoggedIn, isDisabled) {
                                                                    if (isLoggedIn == 0) {
                                                                        document.querySelector('#paypal-button-container').innerHTML =
                                                                                '<button type="button" ' +
                                                                                'class="btn btn-primary btn-lg btn-block" ' +
                                                                                (isDisabled ? "disabled " : " ") +
                                                                                ' data-bs-toggle="modal" ' +
                                                                                ' data-bs-target="#exampleModal" onclick="handlePayment(&quot;not logged in&quot;)"> ' +
                                                                                ' Go to checkout' +
                                                                                ' </button>'
                                                                    } else {
                                                                        document.querySelector('#paypal-button-container').innerHTML =
                                                                                '<button type="button" ' +
                                                                                'class="btn btn-primary btn-lg btn-block" ' +
                                                                                (isDisabled ? "disabled " : " ") +
                                                                                ' data-bs-toggle="modal" ' +
                                                                                ' data-bs-target="#exampleModal"  onclick="handlePayment(&quot;logged in&quot;)"> ' +
                                                                                ' Go to checkout' +
                                                                                ' </button>'

                                                                    }
                                                                }
                                                                function handleChangePaymentMethod(el) {
                                                                    document.querySelectorAll(".payment-method>i").forEach(i => {
                                                                        i.classList.remove("opa-1");
                                                                    })
                                                                    const classList = el.querySelector("i").classList;
                                                                    if (classList.contains("fa-brands fa-cc-paypal")) {
                                                                        if (prevMethodIsPaypal == false) {
                                                                            document.querySelector('#paypal-button-container').innerHTML = "";
                                                                            const vndPrice = document.querySelector('#total').value;
                                                                            let price = 0
                                                                            fetch("https://api.apilayer.com/exchangerates_data/convert?to=usd&from=vnd&amount=700000", {
                                                                                headers: {
                                                                                    apikey: 'Egj2knbKqmkBva9q7FsIrUWpEqQ32Dpa'
                                                                                },
                                                                                method: "GET",
                                                                            }).then(res => res.json()).then(res => price = res.result)
                                                                            console.log(price)
                                                                            paypal_sdk.Buttons({
                                                                                onInit: function (data, actions) {
                                                                                    if (document.querySelector('.cart-size') == 0) {
                                                                                        actions.disable();
                                                                                    } else {
                                                                                        actions.enable();
                                                                                    }
                                                                                },
                                                                                createOrder: function (data, actions) {
                                                                                    return actions.order.create({
                                                                                        purchase_units: [{
                                                                                                amount: {
                                                                                                    value: (Math.round(price * 100) / 100).toString(),
                                                                                                }
                                                                                            }]
                                                                                    });
                                                                                },
                                                                                onApprove: function (data, actions) {
                                                                                    return actions.order.capture().then(function (details) {

                                                                                        // This function shows a transaction success message to your buyer.
                                                                                        console.log(details)
                                                                                        handlePayment(${empty user ? 0 : 1})
                                                                                    });
                                                                                }
                                                                                ,
                                                                                style: {
                                                                                    layout: 'vertical',
                                                                                    color: 'blue',
                                                                                    shape: 'rect',
                                                                                    label: 'paypal'
                                                                                }
                                                                            }).render('#paypal-button-container');
                                                                            prevMethodIsPaypal = true;
                                                                        }
                                                                    } else {
                                                                        prevMethodIsPaypal = false;
                                                                        renderDefaultBuyButton(${empty user ? 0 : 1}, false)
                                                                    }
                                                                    classList.add("opa-1");
                                                                }
                                                                function checkInput() {
                                                                    const emailEl = document.querySelector('#email');
                                                                    const email = emailEl.value;
                                                                    const phoneEl = document.querySelector('#phone');
                                                                    const phone = phoneEl.value;
                                                                    const fullNameEl = document.querySelector('#fullName');
                                                                    const fullName = fullNameEl.value;
                                                                    const errorList = []
                                                                    const fullNameRegex = '(^[A-Za-z]{3,16})([ ]{0,1})([A-Za-z]{3,16})?([ ]{0,1})?([A-Za-z]{3,16})?([ ]{0,1})?([A-Za-z]{3,16})';
                                                                    if (!email.match(
                                                                            /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                                                                            )) {
                                                                        errorList.push(1)
                                                                        emailEl.style.border = '1.5px solid red';
                                                                        document.querySelector(".email-noti").innerHTML = "Invalid email";
                                                                    } else {
                                                                        let user;
                                                                        fetch('MainController?btnAction=ajax&func=checkEmailExisted&email=' + email)
                                                                                .then(res => res.json())
                                                                                .then(res => {
                                                                                    user = res;
                                                                                    console.log(user)
                                                                                    if (user == null) {
                                                                                        document.querySelector(".email-noti").innerHTML = "";
                                                                                        emailEl.style.border = '1px solid #ced4da';
                                                                                    } else {
                                                                                        emailEl.style.border = '1.5px solid red';
                                                                                        errorList.push(1)
                                                                                        document.querySelector(".email-noti").innerHTML = "This email is existed in our system";
                                                                                    }
                                                                                })

                                                                    }
                                                                    const phoneRegex = /\(?([0-9]{3})\)?([ .-]?)([0-9]{3})\2([0-9]{4})/;
                                                                    if (!phone.match(phoneRegex)) {
//                                                                        console.log(phone.match(phoneRegex))
                                                                        errorList.push(2)
                                                                        phoneEl.style.border = '1.5px solid red';
                                                                        document.querySelector(".phone-noti").innerHTML = "Invalid phone";
                                                                    } else {
                                                                        document.querySelector(".phone-noti").innerHTML = "";
                                                                        phoneEl.style.border = '1px solid #ced4da';
                                                                    }
                                                                    if (!fullName.match(fullNameRegex)) {
                                                                        document.querySelector(".fullName-noti").innerHTML = "Invalid full name";
                                                                        fullNameEl.style.border = '1.5px solid red';
                                                                        errorList.push(3)
                                                                    } else {
                                                                        document.querySelector(".fullName-noti").innerHTML = "";
                                                                        fullNameEl.style.border = '1px solid #ced4da';
                                                                    }
//                                                                    if (errorList.length == 0) {
                                                                    const selectedPaymentClassList = document.querySelector(".opa-1").classList;
                                                                    if (selectedPaymentClassList.contains('fa-brands fa-cc-paypal')) {
                                                                        document.querySelector('#paypal-button-container').innerHTML = "";
                                                                        const vndPrice = document.querySelector('#total').value;
                                                                        let price = 0
                                                                        fetch("https://api.apilayer.com/exchangerates_data/convert?to=usd&from=vnd&amount=700000", {
                                                                            headers: {
                                                                                apikey: 'Egj2knbKqmkBva9q7FsIrUWpEqQ32Dpa'
                                                                            },
                                                                            method: "GET",
                                                                        }).then(res => res.json()).then(res => price = res.result)
                                                                        paypal_sdk.Buttons({
                                                                            onInit: function (data, actions) {
                                                                                if (document.querySelector('.cart-size') == 0 || errorList.length != 0) {
                                                                                    actions.disable();
                                                                                } else {
                                                                                    actions.enable();
                                                                                }
                                                                            },
                                                                            createOrder: function (data, actions) {
                                                                                return actions.order.create({
                                                                                    purchase_units: [{
                                                                                            amount: {
                                                                                                value: (Math.round(price * 100) / 100).toString(),
                                                                                            }
                                                                                        }]
                                                                                });
                                                                            },
                                                                            onApprove: function (data, actions) {
                                                                                return actions.order.capture().then(function (details) {

                                                                                    // This function shows a transaction success message to your buyer.
                                                                                    console.log(details)
                                                                                    handlePayment(${empty user ? 0 : 1})
                                                                                });
                                                                            }
                                                                            ,
                                                                            style: {
                                                                                layout: 'vertical',
                                                                                color: 'blue',
                                                                                shape: 'rect',
                                                                                label: 'paypal'
                                                                            }
                                                                        }).render('#paypal-button-container');
                                                                    } else {
                                                                        renderDefaultBuyButton(${empty user ? 0 : 1}, errorList.length != 0);
                                                                    }
//                                                                    }
                                                                    return errorList;
                                                                }