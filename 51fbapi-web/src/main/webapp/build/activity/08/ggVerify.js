$(function () {

    //获取页面名称传到登录页
    var currentUrl = window.location.href;
    var param = getUrlParam(currentUrl);
    var word = param['word'];
    var urlName = param['urlName'];
    var userName = param['userName'];
    var activityId = param['activityId'];
    var userItemsId = param['userItemsId'];
    var itemsId = param['itemsId'];
    //倒计时
    var timerInterval;
    var timerS = 60;

    function timeFunction() { // 60s倒计时
        timerS--;
        if (timerS <= 0) {
            $(".btn").text("获取验证码");
            clearInterval(timerInterval);
            timerS = 60;
            $(".btn").attr("isState", 0);
        } else {
            $(".btn").text(timerS + " s");
        }
    }



    //点击获取验证码
    $(".btn").click(function () {
        var userName = $(".phoneNumber-right").val(); //获取手机号
        var userck = (/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(userName)); //手机号正则验证
        // var mesg= $(".mesg-right").val();//获取验证码  
        if (userck) {
            // var password_md5 = String(CryptoJS.MD5(password));//md5加密
            $.ajax({
                url: "/H5GGShare/boluomeActivityForgetPwd",
                type: 'POST',
                dataType: 'JSON',
                data: {
                    mobile: userName,
                },
                success: function (data) {
                    console.log(data)
                    if (data.success) {
                        $(".btn").attr("isState", 1);
                        $(".btn").text(timerS + " s");
                        timerInterval = setInterval(timeFunction, 1000);

                    } else if (data.url == "ForgetPwd") {
                        requestMsg(data.msg);
                    }
                }

            })
        } else {
            requestMsg("请填写正确的手机号");
        }
        localStorage.setItem("user", userName); //将手机号存储到本地
        //    localStorage.setItem("mesg",mesg); //将短信验证码存储到本地 

        console.log(userName);

    });



    //点击下一步 跳到忘记密码页
    $('.nextStep').click(function () {
        var mesg = $(".mesg-right").val(); //获取验证码 
        var userName = $(".phoneNumber-right").val(); //获取手机号

        if (userName == '') {
            requestMsg("请填写正确的手机号");
            return false;
        }
        if (mesg == '') {
            requestMsg("请填写正确的验证码");
            return false;
        }
        $.ajax({
            url: "/H5GGShare/boluomeActivityCheckVerifyCode",
            type: 'POST',
            dataType: 'JSON',
            data: {
                mobile: userName,
                verifyCode: mesg
            },
            success: function (data) {
                console.log(data)
                if (!data.success) {
                    requestMsg(data.msg);
                    return false;
                }

                localStorage.setItem("mesg", mesg); //将短信验证码存储到本地

                window.location.href= "ggForgetP?urlName="+urlName+"&userName="+userName+"&activityId="+activityId+"&userItemsId="+userItemsId+"&itemsId="+itemsId + "&word=" + word;;


            }

        })



    })


})

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