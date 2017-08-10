
//获取页面名称传到登录页
 var currentUrl=window.location.href;
var index=currentUrl.indexOf('=');
var urlName=currentUrl.slice(index+1);
console.log(urlName)

$(function () {
    var timerInterval;
    var timerS = 60;

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

    // 获取验证码
    $(".checkbtn").click(function () {
        var isState = $(".checkbtn").attr('isState');//获取设置的状态码
        var mobileNum = $(".mobile").val(); //获取手机号
        var password=$('#password').val();//获取密码

        if (isState == 0 || !isState) {
            if (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) {
                $.ajax({
                    url: "/app/user/getRegisterSmsCode",
                    type: "POST",
                    dataType: "json",
                    data: {
                        "mobile": mobileNum, //将手机号码传给后台
                    },
                    success: function (returnData) {

                        if (returnData.success) {
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
                requestMsg("请填写正确的手机号");
            }
        }
    });

    // 完成注册提交
    $(".loginbtn").click(function () {
        var smsCode = $(".check").val();//获取短信
        var registerMoblie = $(".mobile").val();//获取手机号
        var password=$("#password").val();//获取密码
        console.log(password);
        console.log(smsCode);
        console.log(registerMoblie);

        if (/^1(3|4|5|7|8)\d{9}$/i.test(registerMoblie)) {
            var password_md5 = String(CryptoJS.MD5(password));//md5加密
            $.ajax({
                url: "/H5GGShare/commitBouomeActivityRegister",
                type: 'post',
                data: {
                    "registerMobile": registerMoblie,
                    "smsCode":smsCode,
                    "password":password_md5
                    ,"urlName":urlName
                },
                success: function (returnData) {
                    console.log(returnData);
                    var a=JSON.parse(returnData);
                    console.log(a);
                    if (a.success) {
                        alert(urlName)
                        window.location.href = "gglogin?urlName="+urlName;
                    
                    } //else {
                    //     requestMsg(returnData.msg);
                    // }
                },
                error: function () {
                    requestMsg("绑定失败");
                }
            })


        } else {
            requestMsg("请填写正确的手机号");
        }
    });
});

