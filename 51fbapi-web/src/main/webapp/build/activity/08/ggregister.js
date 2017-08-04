
//点击获取验证码
$(function(){
    $(".check").click(function(){
            var timerInterval ;
            var timerS = 60;
            function timeFunction(){ // 60s倒计时
                timerS--;
                if (timerS<=0) {
                    $(".checkbtn").text("获取验证码");
                    clearInterval(timerInterval);
                    timerS = 60;
                    $(".pinp").attr("isState",0);
                } else {
                    $(".checkbtn").text(timerS+" s");
                }
            }
    })
	

	//点击注册
	$(".loginbtn").click(function(){
		var isState = $(this).attr("check");
		var mobileNum = $(".check").val();
		if (isState==0 || !isState){
			if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)){
                $.ajax({
                    url: "/app/user/commitRegister",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        registerMoblie: isState,
						smsCode: 'mobileNum'
					
                    },
                    success: function(returnData){
                        alert(1111111111)
                        console.log(returnData);
                        if (returnData.success) {
                            $(".check").attr("isState",1);
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


});
