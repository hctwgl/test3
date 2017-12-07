
$(function(){
    $.ajax({
        url: '/wechat/getSign',
        type: 'POST',
        dataType: 'json',
        data: {url: encodeURIComponent(window.location.href.split('#')[0])},
        success: function (result) {
             // 用户确认分享后执行的回调函数
            let d  = {
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: result.data.appId, // 必填，公众号的唯一标识
                timestamp: result.data.timestamp, // 必填，生成签名的时间戳
                nonceStr: result.data.nonceStr, // 必填，生成签名的随机串
                signature: result.data.sign,// 必填，签名，见附录1
                jsApiList : [
                            // 所有要调用的 API 都要加到这个列表中
                'onMenuShareTimeline',"onMenuShareAppMessage",'onMenuShareQQ','onMenuShareWeibo','onMenuShareQZone'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
            }
            wx.config(d);
        }
    });
})

wx.ready(function() {
    wx.checkJsApi({
        jsApiList : ['onMenuShareTimeline','onMenuShareAppMessage','onMenuShareQQ','onMenuShareWeibo','onMenuShareQZone']
    });
    
     //微信好友
    wx.onMenuShareAppMessage(shareInfo);
     //朋友圈
    wx.onMenuShareTimeline(shareInfo);
     //分享qq
    wx.onMenuShareQQ(shareInfo);
     //QQ空间
    wx.onMenuShareQZone(shareInfo);
     //腾讯微博
    wx.onMenuShareWeibo(shareInfo);
})

// 去掉弹窗地址栏
window.alert = function(name){
    var iframe = document.createElement("IFRAME");
    iframe.style.display="none";
    iframe.setAttribute("src", 'data:text/plain,');
    document.documentElement.appendChild(iframe);
    window.frames[0].window.alert(name);
    iframe.parentNode.removeChild(iframe);
}