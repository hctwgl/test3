let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;
let currentUrl = window.location.href;
let param = getUrlParam(currentUrl);
let shopId= param['shopId03'];
let userName = "";//获取用户名
if(getInfo().userName){
    userName=getInfo().userName;
};
let url=window.location.href;//获取完整URL
//获取页面文件名
function GetPageName(url){
    let tmp= [];//临时变量，保存分割字符串
    tmp=url.split("/");//按照"/"分割
    let pp = tmp[tmp.length-1];//获取最后一部分，即文件名和参数
    tmp=pp.split("?");//把参数和文件名分割开
    return tmp[0];
}
let pageName=GetPageName(url);
let recommendCode;//获取邀请码
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
            //倒计时
            let currentStamp=Date.parse(new Date());
            let endStamp=Date.parse(new Date("2017/12/22 16:00:00"));
            let diff=(endStamp-currentStamp)/1000;
            showTimerS(diff);
            diff--;
            window.setInterval(function(){
                showTimerS(diff);
                $('.timeOut').html(showTimerS(diff));
                diff--;
            }, 1000);
            //左右移动动画
            let cont = $(".cont1").html();
            $(".cont2").html(cont);
            wordMove();
            let self = this;
            //页面基本数据初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/inviteFriend",
                success: function (data) {
                    console.log(data);
                    self.baseData=data.data;
                    //复制邀请码
                    setTimeout(function(){
                        let clipboard = new Clipboard('.invitecode');
                        clipboard.on('success', function(e) {
                            console.log(e);
                        });
                        $('.copycode').on('click', ()=>{
                            alert('已复制到剪贴板，可粘贴');
                        })
                    },0);
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
                    self.content.couponAmount=self.content.couponAmount.toString();
                    self.inviteSumMoney=self.content.couponAmount.split('');
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
        //点击有福同享--分享++埋点
        inviteButtonClick(){
            recommendCode=$('.invitecode').html();
            let dat='{"shareAppTitle":"老铁~快来吃霸王餐啦~","shareAppContent":"节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>","shareAppImage":"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg","shareAppUrl":"' + domainName + '/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName='+userName+'&pageName='+pageName+'&recommendCode='+recommendCode+'","isSubmit":"Y","sharePage":"ggFixShare"}';
            let base64 = BASE64.encoder(dat);
            window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params='+base64;
            //点击有福同享加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggOverlord?typeFrom=callPeople'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //去使用我的优惠券+埋点
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
            //点击去使用我的优惠券加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggOverlord?typeFrom=toUseCoupon'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //日期格式转换
        fixDate(date){
            return date.replace(/-/g,'.');
        }
    }
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
    recommendCode=$('.invitecode').html();
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "老铁~快来吃霸王餐啦~",  // 分享的title
        'shareAppContent': "节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName="+userName+"&pageName="+pageName+"&recommendCode="+recommendCode,  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggFixShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};

//首页顶部栏动画-------------------------
var speed = 30;
function wordMove(){
    var left = $(".personAmount").scrollLeft();
    if(left >= $(".cont1").width()){
        left = 0;
    }else{
        left++;
    }
    $(".personAmount").scrollLeft(left);
    setTimeout("wordMove()",speed);
}
//倒计时
function showTimerS( diff ){
    let hour=0,
        minute=0,
        second=0;//时间默认值

    if(diff > 0){
        hour = Math.floor(diff / (60 * 60));
        minute = Math.floor(diff / 60) - (hour * 60);
        second = Math.floor(diff) - (hour * 60 * 60) - (minute * 60);
    }
    if (hour <= 9){
        hour = '0' + hour;
    }
    if (minute <= 9){
        minute = '0' + minute;
    }
    if (second <= 9) {
        second = '0' + second;
    }

    return hour+':'+minute+':'+second
}