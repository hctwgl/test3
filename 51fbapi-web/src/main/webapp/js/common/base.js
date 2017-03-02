/*
* @Author: Yangyang
* @Date:   2017-02-15 09:59:54
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-01 21:41:09
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