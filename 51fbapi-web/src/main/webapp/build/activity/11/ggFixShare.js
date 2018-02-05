var token=formatDateTime()+Math.random().toString(36).substr(2);
// 防止风控被拒
function formatDateTime() {
    var date = new Date();
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    var minute = date.getMinutes();
    var second = date.getSeconds();
    return y +  m +  d +h +minute+second;
};
// 同盾校验编号的sessionId
var _fmOpt;
(function() {
    _fmOpt = {
        partner: 'alading',
        appName: 'alading_web',
        token: token
    };
    var cimg = new Image(1,1);
    cimg.onload = function() {
        _fmOpt.imgLoaded = true;
    };
    cimg.src = ('https:' == document.location.protocol ? 'https://' : 'http://') +"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId=" + _fmOpt.token;
    var fm = document.createElement('script'); fm.type = 'text/javascript'; fm.async = true;
    fm.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'static.fraudmetrix.cn/fm.js?ver=0.1&t=' + (new Date().getTime()/3600000).toFixed(0);
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(fm, s);
    // alert(json.msg);
})();
//获取页面名称传到登录页
var currentUrl = window.location.href;
var param = getUrlParam(currentUrl);
var timerInterval;
var timerS = 60;
var userName=param['userName'];
var typeFrom=param['typeFrom'];//渠道类型
var typeFromNum=param['typeFromNum'];//渠道类型数
var pageName=param['pageName'];//获取文件来源名称
var recommendCode=param['recommendCode'];//获取邀请码
$(function () {
    // 密碼叉叉點擊清楚所有文字
    $('.clearValOne').click(function(){
        $("#mobile").val('');
    });
    $('.clearValTwo').click(function(){
        $("#password").val('');
    });
    function timeFunction() { // 60s倒计时
        timerS--;
        if (timerS <= 0) {
            $(".checkbtn").text("获取验证码");
            clearInterval(timerInterval);
            timerS = 60;
            $(".checkbtn").attr("isState", 0);
        } else {
            $(".checkbtn").text(timerS + " s");
        }
    }

    // 获取图形验证码
    $(".checkbtn").click(function(){
        var mobileNum = $(".mobile").val();
        if ( !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
            $.ajax({
                url: "/app/user/getImgCode",
                type: "POST",
                dataType: "JSON",
                data: {mobile:mobileNum},
                success: function (r) {
                    console.log(r);
                    // 显示弹窗
                    $(".mask").show();
                    $(".imgVftCodeWrap").removeClass("hide");
                    $("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+r.data);
                    $("#imgVftCodeClose").click(function(){ // 关闭弹窗
                        $(".mask").hide();
                        $(".imgVftCodeWrap").addClass("hide");
                    })
                },
                error: function () {
                    requestMsg("请求失败")
                }
            });
        } else{
            requestMsg("请填写正确的手机号");
        }
        //点击加埋点
        $.ajax({
            url:'/fanbei-web/postMaidianInfo',
            type:'post',
            data:{maidianInfo:'/fanbei-web/activity/ggFixShare?type=new_img&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
            success:function (data) {
                console.log(data)
            }
        });
    });

    // 刷新重新获取图片验证
    $("#imgVftCodeRefresh").click(function(){
        var mobileNum = $(".mobile").val();
        $.ajax({
            url: "/app/user/getImgCode",
            type: "POST",
            dataType: "JSON",
            data: {mobile:mobileNum},
            success: function (r) {
                console.log(r);
                // 显示弹窗
                $(".mask").show();
                $(".imgVftCodeWrap").removeClass("hide");
                $("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+r.data);
                $("#imgVftCodeClose").click(function(){ // 关闭弹窗
                    $(".mask").hide();
                    $(".imgVftCodeWrap").addClass("hide");
                })
            },
            error: function () {
                requestMsg("请求失败")
            }
        });
    });

    // 获取验证码
    $("#imgVftCodeSbumit").click(function(){
        var isState = $(".checkbtn").attr('isState');//获取设置的状态码
        var mobileNum = $(".mobile").val(); //获取手机号
        var password=$('#password').val();//获取密码
        var verifyImgCode=$("#imgVftCode").val(); // 图形验证码
        if (isState == 0 || !isState) {
            var userck=(/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(mobileNum));
            if (userck ) {
                $.ajax({
                    url: "/app/user/getRegisterSmsCode",
                    type: "POST",
                    dataType: "json",
                    data: {
                        "mobile": mobileNum, //将手机号码传给后台
                        token:token,
                        verifyImgCode:verifyImgCode
                    },
                    success: function (returnData) {
                        if (returnData.success) {
                            // 关闭弹窗
                            $(".mask").hide();
                            $(".imgVftCodeWrap").addClass("hide");
                            // 倒计时
                            $(".checkbtn").attr("isState", 1);
                            $(".checkbtn").text(timerS + " s");
                            // $(".checkbtn").addClass("gray");
                            timerInterval = setInterval(timeFunction, 1000);
                        } else {
                            requestMsg(returnData.msg);
                        }
                    },
                    error: function () {
                        requestMsg("请求失败");
                    }
                })
            } else {
                console.log(userck);
                requestMsg("请填写正确的手机号");
            }
        }
        //点击加埋点
        $.ajax({
            url:'/fanbei-web/postMaidianInfo',
            type:'post',
            data:{maidianInfo:'/fanbei-web/activity/ggFixShare?type=new_sure&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
            success:function (data) {
                console.log(data)
            }
        });
    });

    // 完成注册提交
    $(".loginbtn").click(function () {
        var smsCode = $(".check").val();//获取短信
        var registerMobile = $(".mobile").val();//获取手机号
        var password=$("#password").val();//获取密码
        var yzcheck=$('#yzcheck').val();//获取验证码
        var userck = (/^1[3|4|5|7|8][0-9]{9}$/.test(registerMobile)); //手机号正则验证11位
        var yztrue=(/^\d{6}$/.test(yzcheck));//6位数字正则验证 验证码
        var mmtrue=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(password);
        var password_md5 = String(CryptoJS.MD5(password));//md5加密
        console.log(registerMobile)
        if (( (userck) && yztrue&&yzcheck!='')&& ( mmtrue&& password!=undefined)) {
            $.ajax({
                url: "/H5GGShare/boluomeActivityRegisterLogin",
                type: 'post',
                data: {
                    "registerMobile": registerMobile,
                    "smsCode":smsCode,
                    "password":password_md5,
                    token:token,
                    'inviteer':userName,
                    'activityId':'1000',
                    'typeFrom':typeFrom,
                    'typeFromNum':typeFromNum,
                    'recommendCode':recommendCode
                },
                success: function (returnData) {
                    console.log(0);
                    console.log(returnData);
                    var a=JSON.parse(returnData);
                    if (a.success) {
                        requestMsg("注册成功");
                        //注册成功加埋点
                        $.ajax({
                            url:'/fanbei-web/postMaidianInfo',
                            type:'post',
                            data:{maidianInfo:'/fanbei-web/activity/ggFixShare?type=new_success&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum,'maidianInfo1':registerMobile},
                            success:function (data) {
                                console.log(data)
                            }
                        });
                        window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
                    }else{
                        requestMsg(a.msg);
                    }
                } ,
                error: function () {
                    requestMsg("绑定失败");
                }
            })
        } else {
            if(!userck){// if else if 只走一条线 通了不走其他
                requestMsg("请填写正确的手机号");
            }else if(!yztrue){
                requestMsg("请填写正确的验证码");
            } else if(true){//上兩種都不是就是第三种不用判断
                requestMsg("请填写6-18位的数字、字母、字符组成的密码");
            }
        }
        //点击加埋点
        $.ajax({
            url:'/fanbei-web/postMaidianInfo',
            type:'post',
            data:{maidianInfo:'/fanbei-web/activity/ggFixShare?type=new_get&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
            success:function (data) {
                console.log(data)
            }
        });
    });
});

//截取字符串方法
function getUrlParam(url) {
    var param = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(url.indexOf("?")+1,url.length);
        var strs=[];
        strs = str.split("&");
        for(var i = 0; i < strs.length; i ++) {
            param[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
        }
    }
    return param;
}
//页面初始化数据
let vm = new Vue({
    el: '#ggFixShare',
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
            //初始化数据
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/homePage",
                success: function (data) {
                    self.content=eval('('+data+')').data;
                    console.log(self.content);
                    /*图片预加载*/
                    self.$nextTick(function () {
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
            //点击加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFixShare?type=new_ini&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击活动规则
        ruleClick(){
            let self=this;
            self.ruleShow=true;
        },
        //点击closeRule关闭mask和规则
        closeClick(){
            let self=this;
            self.ruleShow=false;
        },
        //点击已有账号
        hasUserNameClick(){
            //window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            window.location.href="ggFixShareLogin?typeFrom="+typeFrom+"&typeFromNum="+typeFromNum;
            //bombBox ();
        }
    }
});
function step(){
    //方法二
    setTimeout(function(){
        $('.lineBox01').addClass('lineShow01');
        $('.word01').addClass('wordShow01');
        $('.lineBox02').addClass('lineShow02');
        $('.word02').addClass('wordShow02');
        $('.lineBox03').addClass('lineShow03');
        $('.word03').addClass('wordShow03');
        $('.lineBox04').addClass('lineShow04');
        $('.word04').addClass('wordShow04');
    }, 500);
}
step();

function bombBox () {
    var loadDateTime = new Date();
    if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
        window.location.href = "com.91ala.www://home://";
        window.setTimeout(function () {
            var timeOutDateTime = new Date();
            if (timeOutDateTime - loadDateTime < 5000 && location.href.indexOf('com.91ala.www://home//') == -1) {
                window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            } else {
                window.close();
            }
        }, 2000);
    } else if (navigator.userAgent.match(/android/i)) {
        window.location.href = "myapp://jp.app/openwith??isBrowser=1";
        setTimeout(function () {
            var timeOutDateTime = new Date();
            if (timeOutDateTime - loadDateTime < 5000) {
                window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
        }, 2000);
    }
};
//二次分享
let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;
var shareInfo;
if(pageName=='ggFix'){
    shareInfo = {
        title: "有人@你~你有最高188元惊喜金待领取！",
        desc: "16元外卖1元购，笔笔订单返现金（可提现）~",
        link: domainName+'/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName='+userName+'&pageName=ggFix'+'&recommendCode='+recommendCode,
        imgUrl: "https://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",
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
}
if(pageName=='ggOverlord'){
    shareInfo = {
        title: "老铁~快来吃霸王餐啦~",
        desc: "节日剁手不吃土，来51返呗点16元外卖1元购，有福同享，你也快来>>>",
        link: domainName+'/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName='+userName+'&pageName=ggOverlord'+'&recommendCode='+recommendCode,
        imgUrl: "https://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",
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
}
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