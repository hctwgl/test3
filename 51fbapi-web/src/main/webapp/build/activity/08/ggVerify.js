$(function(){
 
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

        //获取数据
        $(".btn").click(function () {
            var userName = $(".phoneNumber-right").val();//获取手机号
            var mesg= $(".mesg-right").val();//获取验证码
            if (/^1(3|4|5|7|8)\d{9}$/i.test(userName)) {
                // var password_md5 = String(CryptoJS.MD5(password));//md5加密
                $.ajax({
                    url: "/H5GGShare/boluomeActivityForgetPwd",
                    type: 'POST',
                    dataType: 'JSON',
                    data: {      	
                        mobile: userName,
                    },
                    success: function (data) {
                        console.log(data)
                        if(data.success){
                            $(".btn").attr("isState", 1);
                            $(".btn").text(timerS + " s");
                            timerInterval = setInterval(timeFunction, 1000);

                            localStorage.setItem("user",userName); //将手机号存储到本地
                            localStorage.setItem("mesg",mesg); //将短信验证码存储到本地
                            
                        }else{
                            requestMsg(data.msg);
                        }
                        
                    }
                })
            } else {
                requestMsg("请填写正确的手机号");
            }
        });

        //点击下一步 跳到忘记密码页
        $('.nextStep').click(function(){
            window.location.href='http://localhost/fanbei-web/activity/ggForgetP';
        })
}) 

