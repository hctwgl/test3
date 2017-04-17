/*
* @Author: Yangyang
* @Date:   2017-04-12 10:53:28
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-17 18:02:48
* @title:  渠道注册
*/


// 根据判断姓名和验证码来控制按钮的颜色
// function changeBtn() {

// 	var mobileNum = $("#register_mobile").val();
// 	var verificationNum = $("#register_verification").val();
// 	var passwordNum = $("#register_password").val();

// 	if ( mobileNum == "" ) {
// 		$(".register_mobileIcon").addClass("registerIcon_hide");
// 	} else {
// 		$(".register_mobileIcon").removeClass("registerIcon_hide");
// 	};

// 	if ( verificationNum == "" ) {
// 		$(".register_verificationIcon").addClass("registerIcon_hide");
// 	} else {
// 		$(".register_verificationIcon").removeClass("registerIcon_hide");
// 	};

// 	if ( passwordNum == "" ) {
// 		$(".register_passwordIcon").addClass("registerIcon_hide");
// 	} else {
// 		$(".register_passwordIcon").removeClass("registerIcon_hide");
// 	};

// 	// 默认状态下提交按钮的样式
// 	if ( mobileNum != "" && verificationNum != "" && passwordNum != ""　) {
// 		$(".register_submitBtn").removeClass("btnC_gray");
// 	} else{
// 		$(".register_submitBtn").addClass("btnC_gray");
// 	};
// };

// 点击删除按钮清空vul
// $(function(){

// 	$(".register_mobileIcon").click(function(){
// 		$(".register_mobile").val("");
// 		$(".register_mobileIcon").addClass("registerIcon_hide");
// 	});

// 	$(".register_verificationIcon").click(function(){
// 		$(".register_verification").val("");
// 		$(".register_verificationIcon").addClass("registerIcon_hide");
// 	});
// });

var sessionId = $("#sessionId").val();
console.log(sessionId);

// 同盾校验编号的sessionId
(function() {
    _fmOpt = {
        partner: 'alading',
        appName: 'eds_web',
        token: json.msg                            
    };
    var cimg = new Image(1,1);
    cimg.onload = function() {
        _fmOpt.imgLoaded = true;
    };
    cimg.src = "https://fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=eds_web&tokenId=" + _fmOpt.token;
    var fm = document.createElement('script'); fm.type = 'text/javascript'; fm.async = true;
    fm.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'static.fraudmetrix.cn/fm.js?ver=0.1&t=' + (new Date().getTime()/3600000).toFixed(0);
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(fm, s);
    $("#tokenId").val(_fmOpt.token);
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

				var passwordLength = register_password.length;
				if (passwordLength >= 6) {

					$.ajax({ // 设置登录密码
						url: "/app/user/commitRegister",
						type: 'POST',
						dataType: 'JSON',
						data: {
							registerMobile: mobileNum,
							smsCode: register_verification,
							password: password_md5,
							recommendCode: recommendCode
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