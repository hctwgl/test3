let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;
var shareInfo = {
    title: "20元话费、300M流量，3元超值购点击即领",
    desc: "51返呗超值新人礼：20元话费3元领，快来抢购吧",
    link: domainName+'/fanbei-web/activity/newUserShare',
    imgUrl: "https://f.51fanbei.com/h5/app/activity/11/newUser06.jpg",
    success: function() {
        requestMsg("分享成功！");
    },
    error: function() {
        requestMsg("分享失败！");
    },
    cancel: function (res) {
        // 用户取消分享后执行的回调函数
        requestMsg("取消分享！");
    }
};
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
//获取数据
let vm = new Vue({
    el: '#newUserShare',
    data: {
        content: {},
        ruleShow:false
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/activity/freshmanShareH5/homePage",
                success: function (data) {
                     console.log(data);
                    /*self.content=eval('('+data.data+')');*/
                    self.content=data.data.goodsList;
                     self.$nextTick(function () {
                        /*图片预加载*/
                        $(".first").each(function() {
                            var img = $(this);
                            img.load(function () {
                                $(".loadingMask").fadeOut();
                            });
                            setTimeout(function () {
                                $(".loadingMask").fadeOut();
                            },1000)
                        });
                        $(".loadingMask").fadeOut();
                        $("img.lazy").lazyload({
                            placeholder : "https://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
            //页面初始化加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/newUserShare?type=newUserShare_ini'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击活动详情
        activityDetailClick(){
            let self=this;
            self.ruleShow=true;
            $('body').addClass('overFlowClick');
            $('html').addClass('overFlowClick');
            $('.alertRule').animate({'left':'15.4%'},600);
        },
        //点击closeRule关闭mask和规则
        closeClick(){
            let self=this;
            self.ruleShow=false;
            $('body').removeClass('overFlowClick');
            $('html').removeClass('overFlowClick');
            $('.alertRule').animate({'left':'140%'},600);
        },
        //点击立即抢购
        buyNowClick(){
            window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
        }
    }
});


