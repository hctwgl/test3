/*
* @Author: Yangyang
* @Date:   2017-02-15 09:59:54
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-25 18:08:38
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
