/*
* @Author: Yangyang
* @Date:   2017-02-13 16:32:52
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-02-15 11:24:27
* @title:  注册
*/


// 判断手机号、接收验证码
$(function(){

	// var mobileNum=$(".register_mobile").val();
	

	// 60s倒计时
	var timerInterval ;
	var timerS = 60;
	function timeFunction(){
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
		// alert(123)
		var isState = $(this).attr("isState")
		var mobileNum=$(".register_mobile").val();
		console.log(mobileNum);
		if ( (isState==0 || !isState) && mobileNum.length==11 ){	
	     	$.ajax({
    			url: '/user/getVerifyCode',
    			type: 'POST',
    			dataType: 'JSON',
    			data: {
    				type : "R",
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

	// 提交
	$(".register_submitBtn").click(function(){

		var register_verification=$(".register_verification").val();
		var register_password=$(".register_password").val();

		$.ajax({
			// 校验验证码
			url: '/user/checkVerifyCode',
			type: 'POST',
			dataType: 'JSON',
			data: {
				type: "R",
				verifyCode: register_verification
			},
			success: function(returnData){

				if ( returnData.success ) {
					$.ajax({
						// 设置登录密码
						url: '/user/setRegisterPwd',
						type: 'POST',
						dataType: 'JSON',
						data: {
							password: register_password,
							verifyCode: register_verification
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
					requestMsg(returnData.msg);
				}
			},
			error: function(){
				requestMsg("请求失败");
			}
		})
	});
});