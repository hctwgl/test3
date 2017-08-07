
    //点击立即登录
    $(".loginbtn").click(function () {
        var userName = $(".pinp").val();//获取手机号
        var password = $(".check").val();//获取密码
        if (/^1(3|4|5|7|8)\d{9}$/i.test(userName)) {
            var password_md5 = String(CryptoJS.MD5(userName));//md5加密
            $.ajax({
                url: "/boluomeActivityLogin",
                type: 'POST',
                dataType: 'JSON',
                data: {
                    userName: userName,
                    password: password_md5
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
        } else {
            requestMsg("请填写正确的手机号");
        }
    });