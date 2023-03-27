
// Import the functions you need from the SDKs you need
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.17.1/firebase-app.js";
import { listAll, getStorage, ref, getDownloadURL, deleteObject, uploadBytesResumable, uploadBytes } from "https://www.gstatic.com/firebasejs/9.17.1/firebase-storage.js";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyCV6B7fWl8ehjBnrEbJdU5iDxsRbZE1yeA",
  authDomain: "course-online-booking.firebaseapp.com",
  projectId: "course-online-booking",
  storageBucket: "course-online-booking.appspot.com",
  messagingSenderId: "561075298647",
  appId: "1:561075298647:web:26034bbeb8ff0774631e12"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage();
const listRef = ref(storage, 'userID/');
console.log(listRef);

document.querySelector('#input-avatar').onchange = evt => {
  const [file] = document.querySelector('#input-avatar').files
  if (file) {
    document.querySelector('#img-avatar').src = URL.createObjectURL(file)
  }
}

document.querySelector('#submit').addEventListener('click', function () {
            var http = new XMLHttpRequest();
            const avatar = document.querySelector('#input-avatar').files[0];
            console.log("avatar")
            console.log(avatar)
            if (avatar != undefined) {
                listAll(listRef)
                        .then((res) => {
                            res.items.forEach((itemRef) => {
                                // All the items under listRef.
                                deleteObject(itemRef)
                                        .then(() => {
                                            console.log('deleted')
                                        })
                            });
                        })
                const storageRef = ref(storage, 'userID/avatar/' + avatar.name);
                uploadBytes(storageRef, avatar)
                        .then((snapshot) => {
                            console.log(snapshot)
                            console.log('Uploaded');
                            return snapshot
                        })
                        .then(snapshot => {
                            return getDownloadURL(snapshot.ref)
                        }).then(url => {
                                    console.log(url)
                                    let name = document.querySelector('#profile-name').value;
                                    let email = document.querySelector('#profile-email').value;
                                    let phone = document.querySelector('#profile-phone').value;
                                    let description = document.querySelector('#profile-description').value;
                                    fetch("/update-profile", {
                                                method: 'PUT',
                                                body: JSON.stringify({
                                                     fullname: name,
                                                     phone: phone,
                                                     email: email,
                                                     avatar: url,
                                                     description: description
                                                 }),
                                                headers: {
                                                    'Content-type': 'application/json; charset=UTF-8',
                                                },

                                            }).then(res => {
                                                const responsePayload = decodeJwtResponse(localStorage.getItem("refreshToken"));
                                                const body = {
                                                    email : responsePayload.sub,
                                                    password: ""
                                                }
                                                console.log(body)
                                                console.log(responsePayload)
                                                    fetch("/logout").then(res => {
                                                        console.log(res)
                                                        if(res.ok) return res
                                                    }).then((res) => {
                                                        localStorage.removeItem("accessToken");
                                                        localStorage.removeItem("refreshToken");
                                                        localStorage.removeItem("cart")
                                                        window.location.href="/home"
                                                        if (getCookie("accessToken")) deleteCookie("accessToken");
                                                        if (getCookie("refreshToken")) deleteCookie("refreshToken");
                                                        return res;
                                                    })
                                            })
                                })
            }
})
//                        .then(url => {
//                            console.log(url) //link ảnh trên firebase trả về
//                            // Yêu cầu GET vs API, cbi yêu cầu để kết nối
//                            http.open('GET', 'MainController?btnAction=user&userAction=updateProfile&urlAvatar=' + url
//                                    + '&email=' + '${sessionScope.user.email}'
//                                    + '&phone=' + document.querySelector('#phone').value
//                                    + '&username=' + document.querySelector('#username').value
////                                + '&token=' + getParameterByName('token', url)
//                                    , true)//true: bất đồng bộ( call api delay vài giây thì các code dưới vẫn chạy bt)
//                            http.onreadystatechange = function () {
//                                //trường hợp đã gửi req thành công và nhận dc response
//                                //và status < 300: không lỗi thì cho resolve để .then
//                                //trả về resolve or reject tùy vào if else dưới
//                                //
//                                if (this.readyState == 4) {
//                                    if (this.status < 300) {
//                                        return swal("Updated succesfully!", "", "success");
//                                    } else {
//                                        return  swal("Updated unsuccesfully!", "", "danger");
//                                    }
//                                }
//                            }
//                            //yêu cầu gửi đi
//                            http.send();
//                            return url
//                        })
