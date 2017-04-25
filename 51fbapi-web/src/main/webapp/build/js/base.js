/*
* @Author: Yangyang
* @Date:   2017-02-15 09:59:54
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-17 10:26:41
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