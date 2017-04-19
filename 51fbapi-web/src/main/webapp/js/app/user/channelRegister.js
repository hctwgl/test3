/*
* @Author: Yangyang
* @Date:   2017-04-12 10:53:28
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-18 16:29:31
* @title:  渠道注册
*/


var sessionId = $("#sessionId").val();
var tdHost = $("#tdHost").val();
// 同盾校验编号的sessionId
 (function() {
     _fmOpt = {
         partner: 'alading',
         appName: 'register_professional_web',
         token: sessionId,
     };
     var cimg = new Image(1,1);
     cimg.onload = function() {
         _fmOpt.imgLoaded = true;
     };
     cimg.src = tdHost+"/fp/clear.png?partnerCode=alading&appName=register_professional_web&tokenId=" + _fmOpt.token;
     var fm = document.createElement('script'); fm.type = 'text/javascript'; fm.async = true;
     fm.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'static.fraudmetrix.cn/fm.js?ver=0.1&t=' + (new Date().getTime()/3600000).toFixed(0);
     var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(fm, s);
 	// alert(json.msg);
 })();



// 判断手机号、接收验证码
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
	};

	// 获取验证码
	$(".register_codeBtn").click(function(){

		var isState = $(this).attr("isState");
		var mobileNum = $("#register_mobile").val();

		if ( (isState==0 || !isState) && mobileNum.length==11 && !isNaN(mobileNum) ){	
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
	});

	
	$(".register_submitBtn").click(function(){ // 完成注册提交

		// md5加密
		var register_password = $("#register_password").val();
		// 正则判断密码为6-18位字母+字符的组合
		var pwdReg = /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/;			  
		var password = pwdReg.test(register_password);
		
		if ( password ) {
			var password_md5 = String(CryptoJS.MD5(register_password));

			if ($("#input_check").is(":checked")) { // 判断当前是否选中

				var recommendCode = getUrl("recommendCode"); // 从分享链接中获取code
				var mobileNum = $("#register_mobile").val();
				var register_verification = $("#register_verification").val();
				var channelCode = $("#channelCode").val();
				var pointCode = $("#pointCode").val();

				var passwordLength = register_password.length;
				if (passwordLength >= 6) {

					$.ajax({ // 设置登录密码
						url: "/app/user/commitChannelRegister",
						type: 'POST',
						dataType: 'JSON',
						data: {
							registerMobile: mobileNum,
							smsCode: register_verification,
							password: password_md5,
							recommendCode: recommendCode,
							channelCode: channelCode,
							pointCode: pointCode
						},
						success: function(returnData){
							if ( returnData.success ) {
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
					requestMsg("请填写6-18位的数字、字母、字符组成的密码");
				}

			} else {
				requestMsg("请阅读并同意《51返呗用户注册协议》");
			}

		}else{
			requestMsg("请输入数字和字符组合的密码");
		}
	});
});

// 点击眼睛显示密码
$(function(){

	$(".register_passwordEyeClosed").click(function() {
		
		var passwordType = $(".register_password").attr("type");
		if (passwordType == "password") {
			$(".register_password").attr("type","text");
			$(this).addClass("register_passwordEye");
		} else {
			$(".register_password").attr("type","password");
			$(this).removeClass("register_passwordEye");
		}
	});
});