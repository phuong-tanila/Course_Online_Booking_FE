let bannerTop = document.querySelector('.banner_area').offsetHeight
let course_details_left = document.querySelector('.course_details_left').offsetWidth
let right_contents = document.querySelector('.right-contents')
let right_contents_height = document.querySelector('.right-contents').offsetHeight
let container_width = document.querySelector('.container').offsetWidth
let right_contents_width = container_width - course_details_left
let stop_point_sticky = document.querySelector('.stop-sticky').offsetTop

window.onscroll = () => {
    handleSticky()

}
// console.log(window.pageYOffset)

const handleSticky = () => {
    if (bannerTop <= window.pageYOffset) {
        right_contents.style.width = right_contents_width + "px"
        right_contents.style.marginLeft = course_details_left + "px"
        right_contents.classList.add("sticky")
        let pointSticky = window.pageYOffset + right_contents_height
        if (pointSticky > stop_point_sticky) {
            right_contents.classList.remove("sticky")
            right_contents.classList.add("stop")

        } else {
            right_contents.classList.add("sticky")
            right_contents.classList.remove("stop")
        }

    } else {
        right_contents.removeAttribute('style')
        right_contents.classList.remove("sticky")
    }
}
handleSticky()