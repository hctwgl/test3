let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;
let currentUrl = window.location.href;
let param = getUrlParam(currentUrl);
let shopId= param['shopId03'];

//获取数据
let vm = new Vue({
    el: '#ggOverlord',
    data: {
        content: {},
        couponLength:'',
        inviteSumMoney:'',
        baseData:''
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            //页面基本数据初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/inviteFriend",
                success: function (data) {
                    console.log(data);
                    self.baseData=data.data;
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //外卖券奖励列表初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/returnCoupon",
                success: function (data) {
                    console.log(data);
                    /*self.content=eval('('+data.data+')');*/
                    self.content=data.data;
                    //console.log(self.content);
                    self.couponLength=self.content.returnCouponList.length;
                    self.content.inviteAmount=self.content.inviteAmount.toString();
                    self.inviteSumMoney=self.content.inviteAmount.split('');
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
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击有福同享--分享
        inviteButtonClick(){
            let dat='{"shareAppTitle":"老铁~快来吃霸王餐啦~","shareAppContent":"节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>","shareAppImage":"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg","shareAppUrl":"' + domainName + '/fanbei-web/activity/ggFixShare","isSubmit":"Y","sharePage":"ggFixShare"}';
            let base64 = BASE64.encoder(dat);
            window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params='+base64;
        },
        //去使用我的优惠券
        toCashClick(){
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrlV1',
                data:{'shopId':shopId},
                dataType:'JSON',
                success: function (returnData) {
                    console.log(returnData);
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
        },
        //日期格式转换
        fixDate(date){
            return date.replace(/-/g,'.');
        }
    }
});
//复制邀请码
$(function(){
    let clipboard = new Clipboard('.invitecode');
    clipboard.on('success', function(e) {
        console.log(e);
    });
    $('.copycode').on('click', ()=>{
        alert('已复制到剪贴板，可粘贴');
    })
});

//截取字符串方法
function getUrlParam(url) {
    var param = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(url.indexOf("?") + 1, url.length);
        var strs = [];
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            param[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return param;
}

// app调用web的分享方法
function alaShareData(){
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "老铁~快来吃霸王餐啦~",  // 分享的title
        'shareAppContent': "节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggFixShare",  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggFixShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};