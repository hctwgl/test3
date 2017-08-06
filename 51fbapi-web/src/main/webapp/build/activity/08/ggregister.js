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
        var isState = $(".checkbtn").attr('isState');
        var mobileNum = $(".mobile").val(); //获取手机号
        if (isState == 0 || !mobileNum) {
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
                            $(".checkbtn").addClass("gray");
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
        var smsCode = $(".check").val();
        var registerMoblie = $(".mobile").val();
        if (/^1(3|4|5|7|8)\d{9}$/i.test(registerMoblie)) {
            $.ajax({
                url: "/app/user/commitChannelRegister",
                type: 'POST',
                dataType: 'JSON',
                data: {
                    "registerMoblie": registerMoblie,
                    "smsCode":smsCode
                },
                success: function (returnData) {
                    if (returnData.success) {
                        window.location.href = returnData.url;
                    } else {
                        requestMsg(returnData.msg);
                    }
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