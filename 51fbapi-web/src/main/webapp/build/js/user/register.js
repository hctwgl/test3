/*
* @Author: yoe
* @Date:   2017-02-13 16:32:52
* @Last Modified by:   yoe
* @Last Modified time: 2017-05-16 14:43:02
* @title:  注册
*/


// 根据判断姓名和验证码来控制按钮的颜色
function changeBtn() {

	var mobileNum = $("#register_mobile").val();
	var verificationNum = $("#register_verification").val();
	var passwordNum = $("#register_password").val();

	if ( mobileNum == "" ) {
		$(".register_mobileIcon").addClass("registerIcon_hide");
	} else {
		$(".register_mobileIcon").removeClass("registerIcon_hide");
	};

	if ( verificationNum == "" ) {
		$(".register_verificationIcon").addClass("registerIcon_hide");
	} else {
		$(".register_verificationIcon").removeClass("registerIcon_hide");
	};

	if ( passwordNum == "" ) {
		$(".register_passwordIcon").addClass("registerIcon_hide");
	} else {
		$(".register_passwordIcon").removeClass("registerIcon_hide");
	};

	// 默认状态下提交按钮的样式
	if ( mobileNum != "" && verificationNum != "" && passwordNum != "" ) {
		$(".register_submitBtn").removeClass("btnc_cf");
		$(".register_submitBtn").attr("disabled",false);
	} else{
		$(".register_submitBtn").attr("disabled",true);
		$(".register_submitBtn").addClass("btnc_cf");
	};
};


// 点击删除按钮清空vul
$(function(){

	$(".register_mobileIcon").click(function(){
		$(".register_mobile").val("");
		$(".register_mobileIcon").addClass("registerIcon_hide");
	});

	$(".register_verificationIcon").click(function(){
		$(".register_verification").val("");
		$(".register_verificationIcon").addClass("registerIcon_hide");
	});
});

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

	$(".register_codeBtn").click(function(){ // 获取验证码

		var isState = $(this).attr("isState");
		var mobileNum = $("#register_mobile").val();

		if ( (isState==0 || !isState) && !isNaN(mobileNum)  ){
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

		// 从分享链接中获取code
		var recommendCode = getUrl("recommendCode"); 
		var mobileNum = $("#register_mobile").val();
		var register_verification = $("#register_verification").val();

		// md5加密
		var register_password = $("#register_password").val();
		var password_md5 = String(CryptoJS.MD5(register_password));
		var passwordLength = register_password.length;

		// 正则判断密码为6-18位字母+字符的组合
		var pwdReg = /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/;			  
		var password = pwdReg.test(register_password);

		if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum) && mobileNum != "" ){ // 判断电话开头
		
			if ( register_verification != "" ) { // 验证码不能为空

				if (  password && 6 <= passwordLength <= 18 ) { // 密码6-18位

					if ($("#input_check").is(":checked")) { // 判断协议是否勾选

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
						requestMsg("请阅读并同意《51返呗用户注册协议》");
					}

				} else {
					requestMsg("请填写6-18位的数字、字母、字符组成的密码");
				}
			}else{
				requestMsg("请输入验证码");
			}
		} else{
            requestMsg("请填写正确的手机号");
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