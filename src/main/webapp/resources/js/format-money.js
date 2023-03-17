const formatter = new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
});
function formatOnLoad(el, money){
    console.log(money)
    el.innerHTML = formatter.format(parseInt(money))
}