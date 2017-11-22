

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
    var urlName = param['urlName'];
    var typeFrom=param['typeFrom'];//渠道类型
    var typeFromNum=param['typeFromNum'];//渠道类型数
    //用戶名叉叉點擊清楚所有文字
    $('.yhicon').click(function () {
        $("#yhinp").val('');
        $('.yhicon').css("display", "none");
    });

    $("#yhinp").keyup(function () {

        if ($("#yhinp").val() == '') {
            $('.yhicon').css("display", "none");
            } else {
            $('.yhicon').css("display", "block");
        }
    });

    // 密碼叉叉點擊清楚所有文字
    $('.mmicon').click(function () {
            $(".check").val('');
            $('.mmicon').css("display", "none");
    });


    $(".check").keyup(function () {
            if ($(".check").val() == '') {
                $('.mmicon').css("display", "none");
            } else {
                $('.mmicon').css("display", "block");
            }
    });


    $(".loginbtn").click(function () {
        var userNamePhone = $("#yhinp").val(); //获取手机号
        var password = $(".check").val(); //获取密码

        var userck = (/^1[3|4|5|7|8][0-9]{9}$/.test(userNamePhone)); //手机号正则验证11位
        if (userck && /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(password)) {

            var password_md5 = String(CryptoJS.MD5(password)); //md5加密
            $.ajax({
                url: "/H5GGShare/boluomeActivityLogin",
                type: 'POST',
                dataType: 'JSON',
                data: {
                    userName: userNamePhone,
                    password: password_md5,
                    activityId: 1000,
                    token:token,
                    'typeFrom':typeFrom,
                    'typeFromNum':typeFromNum
                },
                success: function (data) {
                    if (data.success) {
                        bombBox ();
                    } else if (data.url == "DownLoad") {
                        requestMsg(data.msg);
                        //跳转延迟
                        setTimeout(function () {
                            window.location.href = "ggFixShareLogin?typeFrom="+typeFrom+"&typeFromNum="+typeFromNum;
                        }, 1500);
                    }else if (data.url == "Login") {
                        requestMsg(data.msg);
                    }
                }
            })

        } else {
            console.log(userck + '000');
            if (!userck) {
                requestMsg("请填写正确的手机号");
            } else {
                requestMsg("请填写6-18位的数字、字母、字符组成的密码");
            }

        }
    });

    //忘记密码
    $("#gg_forget").click(function () {
        window.location.href = "ggFixShareVerify?&typeFrom="+typeFrom+"&typeFromNum="+typeFromNum;
    });
})
//截取字符串方法
function getUrlParam(url) {
    var param = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(url.indexOf("?") + 1, url.length);
        var str = url.substr(url.indexOf("?") + 1, url.length);
        var strs = [];
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            param[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return param;
}

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
