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
    //点击立即登录

    // 叉叉相關問題

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

        //var userck = (/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(userNamePhone));
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
                    activityId: activityId,
                    refUserName: userName,
                    urlName: word
                },
                success: function (data) {
                    console.log(data)
                    if (data.success) {
                        if (word == "Z") {
                            window.location.href = urlName + "?userName=" + userName + "&activityId=" + activityId + "&userItemsId=" + userItemsId;
                        } else if (word == "S") {
                            window.location.href = urlName + "?userName=" + userName + "&itemsId=" + itemsId + "&activityId=" + activityId;
                        } else {
                            window.location.href = "ggIndexShare" + "?activityId=" + activityId + "&userName=" + userName;
                        }
                    } else if (data.url == "Login") {
                        requestMsg(data.msg);

                    } else if (data.url == "DownLoad") {
                        requestMsg(data.msg);
                        //跳转延迟
                        setTimeout(function () {
                            window.location.href = "ggregister?word=" + word + "&userName=" + userName + "&activityId=" + activityId + "&userItemsId=" + userItemsId + "&itemsId=" + itemsId + "&urlName=" + urlName;
                        }, 1500);
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


    //注册
    $("#gg_register").click(function () {
        //  alert(word);
        window.location.href = "ggregister?word=" + word + "&userName=" + userName + "&activityId=" + activityId + "&userItemsId=" + userItemsId + "&itemsId=" + itemsId + "&urlName=" + urlName;
    });

    //忘记密码
    $("#gg_forget").click(function () {
        // alert(word);
        window.location.href = "ggVerify?word=" + word + "&userName=" + userName + "&activityId=" + activityId + "&userItemsId=" + userItemsId + "&itemsId=" + itemsId + "&urlName=" + urlName;
    });
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
