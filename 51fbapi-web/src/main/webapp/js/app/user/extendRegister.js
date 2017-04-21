/*
* @Author: nizhiwei
* @Date:   2017-04-18
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-19 16:37:58
* @title:  返呗注册
*/

$(function(){
	var timerInterval ;
	var timerS = 60;
	function timeFunction(){ // 60s倒计时
        timerS--;
        if (timerS<=0) {
         	$(".register_codeBtn").text("获取验证码");
         	clearInterval(timerInterval);
         	timerS = 60;
			$(".register_codeBtn").attr("isState",0);
        } else {
         	$(".register_codeBtn span").text(timerS+" s");
        }
	}

	// 获取验证码
	$(".register_codeBtn").click(function(){
		var isState = $(this).attr("isState");
		var mobileNum = $("#register_mobile").val();
		if (isState==0 || !isState){
			if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)){
                $.ajax({
                    url: "/app/user/getRegisterSmsCode",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        mobile: mobileNum
                    },
                    success: function(returnData){
                        if (returnData.success) {
                            $(".register_codeBtn").attr("isState",1);
                            $(".register_codeBtn span").text(timerS+" s");
                            timerInterval = setInterval(timeFunction,1000);
                        } else {
                            requestMsg(returnData.msg);
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                })
            } else{
                requestMsg("请填写正确的手机号");
            }
		}
	});

    // 完成注册提交

	$(".register_submitBtn").click(function(){

		var register_password = $("#register_password").val();
        var mobileNum = $("#register_mobile").val();
        if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)){

            if ($("#input_check").is(":checked")) { // 判断当前是否选中

                if ( /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(register_password) ) { 		// 正则判断密码为8-16位字母+字符的组合
                    var password_md5 = String(CryptoJS.MD5(register_password));            // md5加密
                    var recommendCode = getUrl("recommendCode");                          // 从分享链接中获取code
                    var register_verification = $("#register_verification").val();
					$.ajax({
						url: "/app/user/commitChannelRegister",
						type: 'POST',
						dataType: 'JSON',
						data: {
							registerMobile: mobileNum,
							smsCode: register_verification,
							password: password_md5,
							recommendCode: recommendCode,
							channelCode: 'Xdt',
							pointCode: 'Xdt1'
						},
						success: function(returnData){
							if ( returnData.success ) {
								window.location.href = returnData.url;
							} else {
								requestMsg(returnData.msg);
							}
						},
						error: function(){
							requestMsg("绑定失败");
						}
					})
                }else{
                    requestMsg("请填写6-18位的数字、字母组成的密码");
                }
            } else {
                requestMsg("请阅读并同意《51返呗用户注册协议》");
            }
		}else{
            requestMsg("请填写正确的手机号");
        }
	});
});
