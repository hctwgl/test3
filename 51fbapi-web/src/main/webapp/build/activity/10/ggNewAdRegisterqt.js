

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
$(function () {
    //获取页面名称传到登录页
    var currentUrl = window.location.href;
    var param = getUrlParam(currentUrl);
    var urlName = param['urlName'];//主页
    var activityId=param['activityId'];
    var timerInterval;
    var timerS = 60;
    var typeFrom=param['typeFrom'];//渠道类型
    var typeFromNum=param['typeFromNum'];//渠道类型数
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


/* 
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
            data:{maidianInfo:'/fanbei-web/activity/ggNewAdqt?activityId=1&type=new_img&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
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
            data:{maidianInfo:'/fanbei-web/activity/ggNewAdqt?activityId=1&type=new_sure&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
            success:function (data) {
                console.log(data)
            }
        });
    });
 */


    // 
    // ---------------------------start

    // 获取验证码
    function getCode() {
        var isState = $(".checkbtn").attr('isState'); //获取设置的状态码
        var mobileNum = $(".mobile").val(); //获取手机号
        var password = $('#password').val(); //获取密码
        var verifyImgCode = $("#imgVftCode").val(); // 图形验证码

        if (isState == 0 || !isState) {
            var userck = (/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(mobileNum));
            if (userck) {
                $.ajax({
                    url: "/app/user/getRegisterSmsCode4Geetest",
                    type: "POST",
                    dataType: "json",
                    data: {
                        "mobile": mobileNum, //将手机号码传给后台
                        token: token
                    },
                    success: function (returnData) {
                        if (returnData.success) {
                            // 关闭弹窗
                            // $(".registerMask").addClass("hide");
                            // $(".imgVftCodeWrap").addClass("hide");
                            // 倒计时
                            $(".checkbtn").attr("isState", 1);
                            $(".checkbtn").text(timerS + " s");
                            // $(".checkbtn").addClass("gray");
                            timerInterval = setInterval(timeFunction, 1000);
                            requestMsg("验证码已发送");
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
            url: '/fanbei-web/postMaidianInfo',
            type: 'post',
            data: { maidianInfo: '/fanbei-web/activity/ggNewAdqt?activityId=1&type=new_sure&typeFrom=' + typeFrom + '&typeFromNum=' + typeFromNum },
            success: function (data) {
                console.log(data)
            }
        });
    }

    //第三方图片验证
    $.ajax({
        url: "/fanbei-web/getGeetestCode",
        type: "get",
        dataType: "json",
        success: function (data) {
            initGeetest({
                gt: data.gt,
                challenge: data.challenge,
                new_captcha: data.new_captcha, // 用于宕机时表示是新验证码的宕机
                offline: !data.success, // 表示用户后台检测极验服务器是否宕机，一般不需要关注
                product: "bind", // 产品形式，包括：float，popup
            }, function (captchaObj) {
                document.getElementById('checkbtn').addEventListener('click', function () {
                    var mobileNum = $("#mobile").val();
                    if (!(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum))) { // 验证码不能为空、判断电话开头
                        requestMsg('请输入手机号')
                    } else {
                        $.ajax({
                            url: '/app/user/checkMobileRegistered',
                            type: 'post',
                            data: {
                                mobile: mobileNum
                            },
                            success: function (data) {
                                data = JSON.parse(data);
                                if (data.data == 'N') {
                                    captchaObj.verify(); //调起图片验证
                                    maidianFn("getCodeSuccess");
                                } else {
                                    maidianFn("getCodeRegistered");
                                    if (data.msg == "用户已存在") {
                                        requestMsg("您已注册，请直接登录")
                                    } else {
                                        requestMsg(data.msg)
                                    }
                                }
                            }
                        })
                    }
                });
                captchaObj.onSuccess(function () {
                    var result = captchaObj.getValidate();
                    $.ajax({
                        url: '/fanbei-web/verifyGeetestCode',
                        type: 'POST',
                        dataType: 'json',
                        data: {
                            userId: data.userId,
                            geetestChallenge: result.geetest_challenge,
                            geetestValidate: result.geetest_validate,
                            geetestSeccode: result.geetest_seccode
                        },
                        success: function (data) {
                            if (data.data.status === 'success') {
                                maidianFn("sendCodeSuccess");
                                getCode();
                            } else if (data.data.status === 'fail') {
                                maidianFn("sendCodeFail");
                                requestMsg(data.msg);
                            }
                        }
                    })
                });
            });
        }
    });

    maidianFn('ggNewAdqt');

    // ---------------------------end

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
                    'activityId':activityId,
                    'typeFrom':typeFrom,
                    'typeFromNum':typeFromNum
                },
                success: function (returnData) {
                    console.log(0);
                    console.log(returnData);
                    var a=JSON.parse(returnData);
                    if (a.success) {
                        requestMsg("注册成功");
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
            data:{maidianInfo:'/fanbei-web/activity/ggNewAdqt?activityId=1&type=new_get&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
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