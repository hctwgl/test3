/*
 * @Date:   2017-04-12 10:53:28
 */
var token = formatDateTime() + Math.random().toString(36).substr(2);

var style = $("#style").val();  // 样式
var os = getBlatFrom(); // 1是android，2是ios
console.log(style);
var channelCode = getUrl('channelCode');
var pointCode = getUrl('pointCode');

var protocolRegisterName = '《爱上街用户注册协议》';
if (style == 19) {
    protocolRegisterName = '《爱上街用户注册协议》';
}

function toMaidian(data, data2) {
    maidianFnNew(data, channelCode, pointCode, data2);
}
function login() {
    toMaidian('toLogin')
    if (style == '25') {
        if (os == 1) {
            window.location.href = 'http://sftp.51fanbei.com/jiekuanchaoren_v3.9.1_app.apk'
        } else {
            window.location.href = 'https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%B0%8F%E9%A2%9D%E5%88%86%E6%9C%9F%E8%B4%B7%E6%AC%BE%E5%80%9F%E9%92%B1%E5%80%9F%E8%B4%B7%E8%BD%AF%E4%BB%B6/id1263792729?mt=8'
        }
    } else if (style == '24') {
        if (os == 1) {
            window.location.href = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www.borrowSuperman'
        } else {
            window.location.href = 'https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%B0%8F%E9%A2%9D%E5%88%86%E6%9C%9F%E8%B4%B7%E6%AC%BE%E5%80%9F%E9%92%B1%E5%80%9F%E8%B4%B7%E8%BD%AF%E4%BB%B6/id1263792729?mt=8'
        }
    } else {
        window.location.href = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www'
    }
}

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
    return y + m + d + h + minute + second;
}

// 同盾校验编号的sessionId
var _fmOpt;
(function () {
    _fmOpt = {
        partner: 'alading',
        appName: 'alading_web',
        token: token
    };
    var cimg = new Image(1, 1);
    cimg.onload = function () {
        _fmOpt.imgLoaded = true;
    };
    cimg.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + "fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId=" + _fmOpt.token;
    var fm = document.createElement('script');
    fm.type = 'text/javascript';
    fm.async = true;
    fm.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'static.fraudmetrix.cn/fm.js?ver=0.1&t=' + (new Date().getTime() / 3600000).toFixed(0);
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(fm, s);
    // alert(json.msg);
})();

// 检测流量
(function (root) {
    root._tt_config = true;
    var ta = document.createElement('script');
    ta.type = 'text/javascript';
    ta.async = true;
    ta.src = document.location.protocol + '//' + 's3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js';
    ta.onerror = function () {
        var request = new XMLHttpRequest();
        var web_url = window.encodeURIComponent(window.location.href);
        var js_url = ta.src;
        // if ( style==16 ) { // 钜美头条访问量
        //     var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url=' + web_url + '&js_url=' + js_url + '&convert_id=63736236689';
        // } else if ( style==14 ) {
        //     var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url='+web_url+'&js_url='+js_url+'&convert_id=62421367574';
        // } else {
        var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url=' + web_url + '&js_url=' + js_url;
        // }
        request.open('GET', url, true);
        request.send(null);
    }
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(ta, s);
})(window);

// 判断手机号、接收验证码
$(function () {
    var timerInterval;
    var timerS = 60;

    function timeFunction() { // 60s倒计时
        timerS--;
        if (timerS <= 0) {
            $("#register_codeBtn").removeAttr("disabled");
            $("#register_codeBtn").text("点击获取");
            clearInterval(timerInterval);
            timerS = 60;
        } else {
            $("#register_codeBtn").text(timerS + " s");
        }
    }

    // 获取验证码
    function getCode() {
        var isState = $(this).attr("isState");
        var mobileNum = $("#register_mobile").val();
        var channelCode = $("#channelCode").val();
        var pointCode = $("#pointCode").val();

        if (!isNaN(mobileNum) && (/^1(3|4|5|6|7|8|9)\d{9}$/i.test(mobileNum))) {  // 验证码不能为空、判断电话开头
            $("#register_codeBtn").attr("disabled", true);
            $.ajax({
                url: "/app/user/getRegisterSmsCode4Geetest",
                type: "POST",
                dataType: "JSON",
                data: {
                    mobile: mobileNum,
                    token: token,
                    channelCode: channelCode,
                    pointCode: pointCode,
                    bsqToken: token
                },
                success: function (returnData) {
                    if (returnData.success) {
                        // 关闭弹窗
                        $(".registerMask").addClass("hide");
                        // 倒计时
                        $("#register_codeBtn").attr("isState", 1);
                        $("#register_codeBtn").text(timerS + " s");
                        timerInterval = setInterval(timeFunction, 1000);
                    } else {
                        requestMsg(returnData.msg);
                        $("#register_codeBtn").removeAttr("disabled");
                    }
                },
                error: function () {
                    requestMsg(returnData.msg);
                }
            })
        } else {
            requestMsg("请填写正确的手机号");
        }
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
                document.getElementById('register_codeBtn').addEventListener('click', function () {
                    var mobileNum = $("#register_mobile").val();
                    if (!(/^1(3|4|5|6|7|8|9)\d{9}$/i.test(mobileNum))) {  // 验证码不能为空、判断电话开头
                        requestMsg('请输入正确的手机号')
                    } else {
                        $.ajax({
                            url: '/app/user/checkMobileRegistered',
                            type: 'post',
                            data: {mobile: mobileNum},
                            success: function (data) {
                                data = JSON.parse(data);
                                if (data.data == 'N') {
                                    captchaObj.verify();//调起图片验证
                                    toMaidian("getCodeSuccess");
                                } else {
                                    toMaidian("getCodeRegistered");
                                    requestMsg(data.msg)
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
                                toMaidian("sendCodeSuccess");
                                getCode();
                            } else if (data.data.status === 'fail') {
                                toMaidian("sendCodeFail");
                                requestMsg(data.msg);
                            }
                        }
                    })
                });
            });
        }
    });

    toMaidian('channelRegister');

    // 提交注册
    $("#register_submitBtn").click(function () { // 完成注册提交
        toMaidian('registerBtn');
        if (style != '8' && style != '9' && style != '10' && style != '11' && style != '12' && style != '13' && style != '14' && style != '15' && style != '19') { //样式8,9无密码
            // md5加密
            var register_password = $("#register_password").val();
            var password_md5 = String(CryptoJS.MD5(register_password));
            var passwordLength = register_password.length;
            // 正则判断密码为6-18位字母+字符的组合
            var pwdReg = /^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/;
            var password = pwdReg.test(register_password);
        } else {
            var password_md5 = ''
        }
        var mobileNum = $("#register_mobile").val();
        var register_verification = $("#register_verification").val();
        var channelCode = $("#channelCode").val();
        var pointCode = $("#pointCode").val();
        var isState = $("#register_codeBtn").attr("isState");
        if (/^1(3|4|5|6|7|8|9)\d{9}$/i.test(mobileNum) && mobileNum != "") { // 判断电话开头
            if (register_verification != "") { // 验证码不能为空
                if (style == '8' || style == '9' || style == '10' || style == '11' || style == '12' || style == '13' || style == '14' || style == '15' || style == '19' || (password && 6 <= passwordLength && passwordLength <= 18)) { // 密码6-18位
                    if ($("#input_check").is(":checked") || (style == '12' || style == '13' || style == '14' || style == '15')) { // 判断当前是否选中
                        if ($("#register_codeBtn").attr("isState") == 1) {
                            //_taq.push({convert_id:"59212981134", event_type:"form"});// 检测访问量
                            $.ajax({ // 设置登录密码
                                url: "/app/user/commitChannelRegister",
                                type: 'POST',
                                dataType: 'JSON',
                                data: {
                                    registerMobile: mobileNum,
                                    smsCode: register_verification,
                                    password: password_md5,
                                    channelCode: channelCode,
                                    pointCode: pointCode,
                                    token: token,
                                    bsqToken: token
                                },
                                success: function (returnData) {
                                    if (returnData.success) {
                                        toMaidian("registerSuccess", mobileNum);
                                        // js判断微信和QQ
                                        let ua = navigator.userAgent.toLowerCase();
                                        if (style == '25') {
                                            if (os == 1) {
                                                window.location.href = 'http://sftp.51fanbei.com/jiekuanchaoren_v3.9.1_app.apk'
                                                return false
                                            } else {
                                                window.location.href = 'https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%B0%8F%E9%A2%9D%E5%88%86%E6%9C%9F%E8%B4%B7%E6%AC%BE%E5%80%9F%E9%92%B1%E5%80%9F%E8%B4%B7%E8%BD%AF%E4%BB%B6/id1263792729?mt=8'
                                                return false
                                            }
                                        } else if (style == '24') {
                                            if (os == 1) {
                                                window.location.href = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www.borrowSuperman'
                                                return false
                                            } else {
                                                window.location.href = 'https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%B0%8F%E9%A2%9D%E5%88%86%E6%9C%9F%E8%B4%B7%E6%AC%BE%E5%80%9F%E9%92%B1%E5%80%9F%E8%B4%B7%E8%BD%AF%E4%BB%B6/id1263792729?mt=8'
                                                return false
                                            }
                                        }
                                        if (os == 1 && ua.match(/MicroMessenger/i) != "micromessenger" && ua.match(/QQ/i) != "qq") {
                                            window.location.href = 'http://sftp.51fanbei.com/51fanbei_app.apk';//安卓除了腾讯系，直接下载apk
                                            // setTimeout(function () {
                                            //     window.location.href="https://app.51fanbei.com//unionlogin/welcome?isNew=1";
                                            // },500)
                                        }
                                        //转化代码
                                        if (style == '14') {
                                            _taq.push({convert_id: "92097724391", event_type: "form"});
                                        }
                                        window.location.href = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
                                    } else {
                                        maidianFnNew("registerFail", channelCode, pointCode, returnData.msg);
                                        requestMsg(returnData.msg);
                                    }
                                },
                                error: function () {
                                    requestMsg("注册失败");
                                }
                            })
                        } else {
                            requestMsg("请获取验证码");
                        }
                    } else {
                        requestMsg("请阅读并同意" + protocolRegisterName);
                    }
                } else {
                    requestMsg("请填写6-18位的数字、字母、字符组成的密码");
                }
            } else {
                requestMsg("请输入正确的验证码");
            }
        } else {
            requestMsg("请填写正确的手机号");
        }
    });

    //返回顶部
    $('.goTop').click(function () {
        $('body,html').animate({scrollTop: 0}, 300);
    })

    //样式15--点击商品返回顶部
    $('.goodCont').click(function () {
        $('body,html').animate({scrollTop: 0}, 300);
    })

    /*//样式18
    if(style=='18'){
        $('.applyBtn').click(function(){
            toMaidian('applyBtn');
            let userVal=$('.word02 input').val();
            let mobileVal=$('.word03 input').val();
            if((userVal!='')&&(/^1(3|4|5|6|7|8|9)\d{9}$/i.test(mobileVal))){
                 requestMsg('申请成功！');
            }else{
                requestMsg('请输入正确格式的联系人或手机号！');
            }
        })

    }*/
});


// 拖动进度条相关代码
var tag = false, ox = 0, left = 300 * document.documentElement.clientWidth / 375, bgleft = 0;
var totalLength = 300 * document.documentElement.clientWidth / 375;
$('.progress_btn').on('touchstart', function (e) {
    var originalEvent = e.originalEvent;
    var touches = originalEvent.touches;
    var touch;
    if (touches) {
        touch = touches[0];
    } else {
        touch = e;
    }
    console.log(touch);
    ox = touch.pageX - left;
    tag = true;
});
$(document).on('touchend', function () {
    tag = false;
});
$(document).on('touchmove', function (e) {//鼠标移动
    var originalEvent = e.originalEvent; // 这里要判断移动端多点触控的问题，jquery扩展的event对象没有这个属性
    var touches = originalEvent.touches;// 获取源生event对象
    var touch;
    if (touches) { // 如果有touches属性，则代表是移动端touchmove事件
        touch = touches[0]; // 取到多个触控点中的第一个，这样才能获取到触控点的对应位置
    } else {
        touch = e;
    }
    if (tag) {
        left = touch.pageX - ox;
        if (left <= 0) {
            left = 0;
        } else if (left > totalLength) {
            left = totalLength;
        }
        $('.progress_btn').css('left', left);
        $('.progress_bar').width(left / (100 * (document.documentElement.clientWidth / 750)) + "rem");
        $('.text').html("￥" + parseInt(left / totalLength * 19500 + 500));
        $("#leftMoney").html("￥" + parseInt(left / totalLength * 19500 + 500));
        $("#rightMoney").html("￥" + (parseInt((left / totalLength * 19500 + 500)) * 0.001).toFixed(2));
    }
});


// 去下载
(function (root) {
    $(".to_loadapp_btn").on('click', function () {
        let ua = navigator.userAgent.toLowerCase();

        // if (os==1 && ua.match(/MicroMessenger/i)!="micromessenger" && ua.match(/QQ/i) != "qq"){
        //     root.location.href='http://sftp.51fanbei.com/51fanbei_app_' + channelCode + '.apk';
        //     return;
        // };
        // root.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";

        // 在IOS
        if (os == 2) {
            root.location.href = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            return;
        }
        ;

        // 在安卓腾讯系
        if (os == 1 && ua.match(/MicroMessenger/i) == "micromessenger" && ua.match(/QQ/i) == "qq") {
            root.location.href = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            return;
        }
        //埋点
        maidianFnNew('channel_' + style + '_download');
        root.location.href = 'http://sftp.51fanbei.com/ishangjie_app_' + channelCode + '.apk';

    })

})(window);