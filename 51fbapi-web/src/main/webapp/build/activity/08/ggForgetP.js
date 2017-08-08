    
$(function(){

        var user = JSON.parse(localStorage.getItem("user")); //获取到手机验证页的手机号
        var paw = JSON.parse(localStorage.getItem("paw")); //获取到手机验证页的短信验证码

        //获取数据
        //点击完成跳转到手机验证
        $(".btn").click(function (){
                var password = $(".blank-in").val();//获取登录密码
                var password_md5 = String(CryptoJS.MD5(password));//md5加密
                $.ajax({
                    url: "/H5GGShare/boluomeActivityResetPwd",
                    type: 'POST',
                    dataType: 'JSON',
                    data: {
                        password: password_md5,
                        mobile:user,
                        verifyCode:paw
                    },
                    success: function (data) {
                        console.log(data)
                        if(data.success){
                        window.location.href ="data.url";
                        }else{
                            requestMsg(data.msg);
                        }
                        
                    }
                    // localStorage.removeItem("user");//清除localStorage
                    // localStorage.removeItem("paw");
                })
            
        });

        //点击完成跳转到登录
        $('.btn').click(function(){
            window.location.href='http://localhost/fanbei-web/activity/gglogin';
        })
})
