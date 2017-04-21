/*
* @Author: Yangyang
* @Date:   2017-02-15 09:59:54
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-21 20:11:01
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
    var paraArr = url.split("_appInfo=");
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