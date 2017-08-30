/**
 * Created by nizhiwei-labtop on 2017/8/30.
 */
var timerInterval ;
var timerS = 60;
function timeFunction(){ // 60s倒计时
    timerS--;
    if (timerS<=0) {
        $("#register_codeBtn").removeAttr("disabled");
        $("#register_codeBtn").text("获取验证码");
        clearInterval(timerInterval);
        timerS = 60;
    } else {
        $("#register_codeBtn").text(timerS+" s");
    }
}
// 提交注册
$(".submit").click(function(){
    // md5加密
    var register_password = $("#password").val();
    var password_md5 = String(CryptoJS.MD5(register_password));
    var passwordLength = register_password.length;
    // 正则判断密码为6-18位字母+字符的组合
    var pwdReg=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/;
    var password = pwdReg.test(register_password);
    var mobileNum = $("#mobile").val();
    var register_verification = $("#verification").val();
    // var channelCode = $("#channelCode").val();
    // var pointCode = $("#pointCode").val();

    var isState = $(".codeBtn").attr("isState");

    if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum) && mobileNum != "" ){ // 判断电话开头
        if ( register_verification != "" ) { // 验证码不能为空
            if ( password && 6 <= passwordLength && passwordLength <= 18 ) { // 密码6-18位
                if ($("#input_check").is(":checked")) { // 判断当前是否选中
                    if ( $(".codeBtn").attr("isState")==1 ) {
                        $.ajax({ // 设置登录密码
                            url: "/app/user/commitChannelRegister",
                            type: 'POST',
                            dataType: 'JSON',
                            data: {
                                registerMobile: mobileNum,
                                smsCode: register_verification,
                                password: password_md5
                            },
                            success: function(returnData){
                                if (returnData.success) {
                                    $('.success').show();
                                    $.ajax({
                                        url:'/fanbei-web/postMaidianInfo',
                                        type:'post',
                                        data:{maidianInfo:'/fanbei-web/activity/wyRegister?type=submit'},
                                        success:function (data) {
                                            console.log(data)
                                        }
                                    });
                                } else {
                                    requestMsg(returnData.msg);
                                }
                            },
                            error: function(){
                                requestMsg("注册失败");
                            }
                        })
                    } else {
                        requestMsg("请获取验证码");
                    }
                } else {
                    requestMsg("请阅读并同意《51返呗用户注册协议》");
                }
            }else{
                requestMsg("请填写6-18位的数字、字母、字符组成的密码");
            }
        } else {
            requestMsg("请输入正确的验证码");
        }
    } else{
        requestMsg("请填写正确的手机号");
    }
});

// 获取图形验证码
$(".codeBtn").click(function(){
    let mobileNum = $("#mobile").val();
    if ( !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
        $.ajax({
            url: "/app/user/getImgCode",
            type: "POST",
            dataType: "JSON",
            data: {mobile:mobileNum},
            success: function (r) {
                console.log(r);
                // 显示弹窗
                $(".registerMask").removeClass("hide");
                $(".imgVftCodeWrap").removeClass("hide");
                $("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+r.data);
                $("#imgVftCodeClose").click(function(){ // 关闭弹窗
                    $(".registerMask").addClass("hide");
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
});

// 刷新重新获取图片验证
$("#imgVftCodeRefresh").click(function(){
    var mobileNum = $("#mobile").val();
    $.ajax({
        url: "/app/user/getImgCode",
        type: "POST",
        dataType: "JSON",
        data: {mobile:mobileNum},
        success: function (r) {
            console.log(r);
            // 显示弹窗
            $(".registerMask").removeClass("hide");
            $(".imgVftCodeWrap").removeClass("hide");
            $("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+r.data);
            $("#imgVftCodeClose").click(function(){ // 关闭弹窗
                $(".registerMask").addClass("hide");
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
    var isState = $(this).attr("isState");
    var mobileNum = $("#mobile").val();
    var channelCode = $("#channelCode").val();
    var pointCode = $("#pointCode").val();
    var verifyImgCode=$("#imgVftCode").val();

    if ( !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
        $("#codeBtn").attr("disabled",true);
        $.ajax({
            url: "/app/user/getRegisterSmsCode",
            type: "POST",
            dataType: "JSON",
            data: {
                mobile: mobileNum,
                token: token,
                channelCode: channelCode,
                pointCode: pointCode,
                verifyImgCode:verifyImgCode
            },
            success: function(returnData){
                if (returnData.success) {
                    // 关闭弹窗
                    $(".registerMask").addClass("hide");
                    $(".imgVftCodeWrap").addClass("hide");
                    // 倒计时
                    $(".codeBtn").attr("isState",1).text(timerS+" s");
                    timerInterval = setInterval(timeFunction,1000);
                    $.ajax({
                        url:'/fanbei-web/postMaidianInfo',
                        type:'post',
                        data:{maidianInfo:'/fanbei-web/activity/wyRegister?type=code'},
                        success:function (data) {
                            console.log(data)
                        }
                    });
                } else {
                    requestMsg(returnData.msg);
                    $(".codeBtn").removeAttr("disabled");
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        })
    } else{
        requestMsg("请填写正确的手机号");
    }
});

function goApp() {
    let loadDateTime = new Date();
    $.ajax({
        url:'/fanbei-web/postMaidianInfo',
        type:'post',
        data:{maidianInfo:'/fanbei-web/activity/wyRegister?type=go'},
        success:function (data) {
            console.log(data)
        }
    });
    if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
        window.location.href = "com.91ala.www://home://";
        window.setTimeout(function () {
            let timeOutDateTime = new Date();
            if (timeOutDateTime - loadDateTime < 5000 && location.href.indexOf('com.91ala.www://home//') == -1){
                window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            } else {
                window.close();
            }
        },2000);
    } else if (navigator.userAgent.match(/android/i)) {
        window.location.href = "myapp://jp.app/openwith??isBrowser=1";
        setTimeout(function(){
            let timeOutDateTime = new Date();
            if (timeOutDateTime - loadDateTime < 5000){
                window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
        },2000);
    }
}