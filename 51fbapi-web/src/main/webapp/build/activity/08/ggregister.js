
$(function(){
	var timerInterval ;
	var timerS = 60;
	function timeFunction(){ // 60s倒计时
        timerS--;
        if (timerS<=0) {
             $(".checkbtn").text("获取验证码");
         	clearInterval(timerInterval);
         	timerS = 60;
			$(".checkbtn").attr("isState",0);
        } else {
         	$(".checkbtn").text(timerS+" s");
        }
	}

	// 获取验证码
	$(".checkbtn").click(function(){
		var isState = $(this).attr("check");//获取短信验证码
		var mobileNum = $(".pinp").val();//获取手机号
		if (isState==0 || !isState){
			if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)){
                $.ajax({
                    url: "/app/user/getRegisterSmsCode",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        smsCode:isState,//将手机号码传给后台
                        registerMoblie: mobileNum//将验证码传给后台
                    },
                    success: function(returnData){
                        if (returnData.success) {
                            $(".checkbtn").attr("isState",1);
                            $(".checkbtn").text(timerS+" s");
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
	$(".loginbtn").click(function(){

		var register_password = $("#register_password").val();
        var mobileNum = $("#register_mobile").val();
        if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)){

            if ($("#input_check").is(":checked")) { // 判断当前是否选中

                if ( /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(register_password) ) { 		// 正则判断密码为8-16位字母+字符的组合
                    var password_md5 = String(CryptoJS.MD5(register_password));            // md5加密
                    var register_verification = $("#register_verification").val();
					$.ajax({
						url: "/app/user/commitChannelRegister",
						type: 'POST',
						dataType: 'JSON',
						data: {
							registerMobile: mobileNum,
							smsCode: register_verification,
							password: password_md5,
							channelCode: 'Xdt',
							pointCode: 'Xdt1',
							token: token
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
