    //获取数据
    //点击完成跳转到手机验证
    $(".btn").click(function (){
            var password = $(".blank-in").val();//获取登录密码
            var password_md5 = String(CryptoJS.MD5(password));//md5加密
            $.ajax({
                url: "/H5GGShare/boluomeActivityForgetPwd",
                type: 'POST',
                dataType: 'JSON',
                data: {
                    password: password_md5,
                },
                success: function (data) {
                    console.log(data)
                    if(data.success){
                     window.location.href ="data.url";
                    }else{
                        requestMsg(data.msg);
                    }
                       
                }
            })
        
    });
