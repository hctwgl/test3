

// 从分享链接中获取code
var recommendCode = getUrl("recommendCode");


var timerInterval ;
var timerS = 60;
function timeFunction(){ // 60s倒计时
    timerS--;
    if (timerS<=0) {
        $("#codeBtn").removeAttr("disabled");
        $("#codeBtn").text("获取验证码");
        clearInterval(timerInterval);
        timerS = 60;
    } else {
        $("#codeBtn").text(timerS+" s");
    }
};

var vm=new Vue({
    el: '#inviteShare',
    methods:{
        getCode(){
            console.log(222);
            var isState = $(this).attr("isState");
            var mobileNum = $("#tel").val();

            if ( !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
                $("#codeBtn").attr("disabled",true);
                $.ajax({
                    url: "/app/user/getRegisterSmsCode",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        mobile: mobileNum,
                    },
                    beforeSend: function(){
                        $("#codeBtn").attr("isState",1);
                        $("#codeBtn").text(timerS+" s");
                        timerInterval = setInterval(timeFunction,1000);
                    },
                    success: function(returnData){
                        console.log(returnData);
                        if (returnData.success) {
                            console.log(returnData);

                            // $("#register_codeBtn").attr("isState",1);
                            // $("#register_codeBtn").text(timerS+" s");
                            // timerInterval = setInterval(timeFunction,1000);
                        } else {
                            requestMsg(returnData.msg);
                            $("#codeBtn").removeAttr("disabled");
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                })
            } else{
                requestMsg("请填写正确的手机号");
            }
        },
        goRegister(){

            var pwdLength = ($("#password").val()).length;
            var pwdMd5 = String(CryptoJS.MD5($("#password").val())); // md5加密
            var pwdReg=(/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/).test($("#password").val()); // 正则判断密码为6-18位字母+字符的组合

            var telNum = $("#tel").val();
            var VerifiCode = $("#VerifiCode").val();

            var isState = $("#register_codeBtn").attr("isState");

            if(/^1(3|4|5|7|8)\d{9}$/i.test(telNum) && telNum != "" ){ // 判断电话开头
                if ( VerifiCode != "" ) { // 验证码不能为空
                    if ( pwdReg && 6 <= pwdLength && pwdLength <= 18 ) { // 密码6-18位
                        if ($("#inputCheck").is(":checked")) { // 判断当前是否选中
                            if ( $("#codeBtn").attr("isState")==1 ) {

                                $.ajax({ // 设置登录密码
                                    url: "/app/user/commitRegister",
                                    type: 'POST',
                                    dataType: 'JSON',
                                    data: {
                                        registerMobile: telNum,
                                        smsCode: VerifiCode,
                                        password: pwdMd5,
                                        recommendCode: recommendCode
                                    },
                                    success: function(returnData){
                                        if ( returnData.success ) {
                                            $("#register_submitBtn").attr("disabled",true);
                                            window.location.href = returnData.url;
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
            };
        }
    }
});