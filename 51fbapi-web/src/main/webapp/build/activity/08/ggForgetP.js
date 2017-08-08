    
$(function(){

        //获取数据
        //点击完成跳转到手机验证
        var password = $(".blank-in").val();//获取登录密码
        $(".btn").click(function (){
                
                var password_md5 = String(CryptoJS.MD5(password));//md5加密
                $.ajax({
                    url: "/H5GGShare/boluomeActivityResetPwd",
                    type: 'POST',
                    dataType: 'JSON',
                    data: {
                        password: password_md5
                    },
                    success: function (data) {
                        console.log(data)
                        if(data.success){
                        window.location.href ="http://localhost/fanbei-web/activity/gglogin";
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
