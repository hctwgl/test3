

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

    // 获取图形验证码
    $(".btn").click(function(){
        var mobileNum = $(".phoneNumber-right").val();
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
        var mobileNum = $(".phoneNumber-right").val();
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

    //点击获取验证码
    $("#imgVftCodeSbumit").click(function () {
        var userName = $(".phoneNumber-right").val(); //获取手机号
        var userck = (/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(userName)); //手机号正则验证
        // var mesg= $(".mesg-right").val();//获取验证码
        var isState = $(".btn").attr('isState');//获取设置的状态码
        var verifyImgCode=$("#imgVftCode").val(); // 图形验证码

        if (isState == 0 || !isState) {
            if (userck) {
                // var password_md5 = String(CryptoJS.MD5(password));//md5加密
                $.ajax({
                    url: "/H5GGShare/boluomeActivityForgetPwd",
                    type: 'POST',
                    dataType: 'JSON',
                    data: {
                        mobile: userName,
                        token:token,
                        verifyImgCode:verifyImgCode
                    },
                    success: function (data) {
                        console.log(data)
                        if (data.success) {
                            // 关闭弹窗
                            $(".registerMask").addClass("hide");
                            $(".imgVftCodeWrap").addClass("hide");
                            // 倒计时
                            $(".btn").attr("isState", 1);
                            $(".btn").text(timerS + " s");
                            timerInterval = setInterval(timeFunction, 1000);

                        } else {
                            requestMsg(data.msg);
                        }
                    },
                    error: function () {
                        requestMsg("请求失败")
                    }
                })
            } else {
                requestMsg("请填写正确的手机号");
            }
            localStorage.setItem("user", userName); //将手机号存储到本地
            //localStorage.setItem("mesg",mesg); //将短信验证码存储到本地
            console.log(userName);
        }
    });



    //点击下一步 跳到忘记密码页
    $('.nextStep').click(function () {
        var mesg = $(".mesg-right").val(); //获取验证码 
        var userName = $(".phoneNumber-right").val(); //获取手机号
        // var userck = (/^1[3|4|5|7|8][0-9]{9}$/.test(userName)); //手机号正则验证

        // if (userck) {
        //     requestMsg("请填写正确的手机号");
        //     return false;
        // }
        // if (mesg == '') {
        //     requestMsg("请填写正确的验证码");
        //     return false;
        // }
        //  if (mesg.replace(/\s/g, '') == '') {
        //     requestMsg("验证码不能为空");
        //     return false;
        // }

        $.ajax({
            url: "/H5GGShare/boluomeActivityCheckVerifyCode",
            type: 'POST',
            dataType: 'JSON',
            data: {
                mobile: userName,
                verifyCode: mesg,
                token:token
            },
            success: function (data) {
                if(data.url=="ForgetPwd"){
                    requestMsg(data.msg);
                }
                console.log(data)
                if (!data.success) {
                    requestMsg(data.msg);
                    return false;
                }
                localStorage.setItem("mesg", mesg); //将短信验证码存储到本地
                window.location.href= "ggForgetP?urlName="+urlName+"&userName="+userName+"&activityId="+activityId+"&userItemsId="+userItemsId+"&itemsId="+itemsId + "&word=" + word;;
            },
            error: function () {
                requestMsg("请求失败")
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