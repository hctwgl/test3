

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
    var loginSource = param['loginSource'];

    var timerInterval;
    var timerS = 60;


    $('.yhicon').click(function(){
        $(".yhinp").val('');
        $('.yhicon').css("display","none");
    });

    $(".yhinp").keyup(function(){
    
    if($(".yhinp").val()==''){
        $('.yhicon').css("display","none");
    }else{
        $('.yhicon').css("display","block");
    }
    });


    // 密碼叉叉點擊清楚所有文字

    $('.big-one').click(function(){
        $("#mobile").val('');
        // console.log( $("#mobile").val());
        // $('.mmicon').css("display","none");
    });

    $('.big-two').click(function(){
        $("#password").val('');
        // console.log( $("#mobile").val());
        // $('.mmicon').css("display","none");
    });


    /* 
    $("#mobile").keyup(function(){
    if($("#mobile").val()==''){
        $('.mmicon').css("display","none");
    }else{
        $('.mmicon').css("display","block");
    }
    }); */

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
        var mobileNum = $(".mobile").val();
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
                            $(".registerMask").addClass("hide");
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
    });

    // 完成注册提交
    $(".loginbtn").click(function () {
        var smsCode = $(".check").val();//获取短信
        var registerMoblie = $(".mobile").val();//获取手机号
        var password=$("#password").val();//获取密码
        console.log(password);
        console.log(smsCode);
        console.log(registerMoblie);
        var yzcheck=$('#yzcheck').val();//获取验证码
        //var userck=(/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(registerMoblie));
        var userck = (/^1[3|4|5|7|8][0-9]{9}$/.test(registerMoblie)); //手机号正则验证11位
        var yztrue=(/^\d{6}$/.test(yzcheck));//6位数字正则验证 验证码
        var mmtrue=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(password);
        if (( (userck) && yztrue&&yzcheck!='')&& ( mmtrue&& password!=undefined)) {
            var password_md5 = String(CryptoJS.MD5(password));//md5加密
            $.ajax({
                url: "/H5GGShare/commitBouomeActivityRegister",
                type: 'post',
                data: {
                    "registerMobile": registerMoblie,
                    "smsCode":smsCode,
                    "password":password_md5,
                    "urlName":urlName,
                    token:token
                },
                success: function (returnData) {
                    console.log(returnData);
                    var a=JSON.parse(returnData);
                    console.log(a);
                    if (a.success) {
                        var urlName = param['urlName'];
                         requestMsg("注册成功");
                         setTimeout(function () {
                            window.location.href = "gglogin?urlName="+urlName+"&userName="+userName+"&activityId="+activityId+"&userItemsId="+userItemsId+"&itemsId="+itemsId + "&word=" + word+ "&loginSource=" + loginSource;
                        }, 1500);
                        
                    }else if(a.url=="Register"){
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