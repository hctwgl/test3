/*
* @Author: Yangyang
* @Date:   2017-02-15 09:59:54
* @Last Modified by:   yoe
* @Last Modified time: 2017-06-07 21:43:57
* @title:  公用的
*/


// 公用弹框内容
function requestMsg (msg){
	layer.open({
        content: msg,
        skin: 'msg',
        time: 2
    });
}

// 获取当前页面的URL 对其带的参数进行处理
function getUrl(para){
    var paraArr = location.search.substring(1).split("&");
    for(var i = 0;i < paraArr.length;i++){
        if(para == paraArr[i].split('=')[0]){
            return paraArr[i].split('=')[1];
        }
    }
    return '';
}

// 获取当前页面的URL 对其带的参数进行处理
function getInfo(){
    var url = decodeURIComponent(location.toString());
    var paraArr = url.toString().split("_appInfo=");

    if(paraArr.length>1){
        return eval('(' + paraArr[1] + ')');
    }else{
        return ''
    }
    
}

// 处理默认的保留二位小数
function toDecimal2(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return false;
    }
    var fM = Math.round(x*100)/100;
    var s = fM.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + 2) {
        s += '0';
    }
    return s;
}

// 判断ios系统还是android系统
function getBlatFrom(){
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    // 返回1是android系统
    if (isAndroid){
        return 1;
    }
    // 返回2是ios系统
    if (isiOS)  {
        return 2;
    }
    return 0;
}

// 时间戳转换
function formatDate(now) {
    now=new Date(now/1000);
    var year=now.getYear();
    var month=now.getMonth()+1;
    var date=now.getDate();
    // var hour=now.getHours();
    // var minute=now.getMinutes();
    // var second=now.getSeconds();
    // return "20"+year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second;
    return "20"+year+"-"+month+"-"+date;
}

// 获取cookie里面的参数
function getCookie(cookie_name){
    var allcookies = document.cookie;
    var cookie_pos = allcookies.indexOf(cookie_name);   //索引的长度
  
    // 如果找到了索引，就代表cookie存在，
    // 反之，就说明不存在。
    if (cookie_pos != -1){  // 把cookie_pos放在值的开始，只要给值加1即可。
        cookie_pos += cookie_name.length + 1;  // 这里容易出问题，所以请大家参考的时候自己好好研究一下
        var cookie_end = allcookies.indexOf(";", cookie_pos);
  
        if (cookie_end == -1){
            cookie_end = allcookies.length;
        }
  
        var value = unescape(allcookies.substring(cookie_pos, cookie_end));  //这里就可以得到你想要的cookie的值了。。。
    }
    return value;
};
