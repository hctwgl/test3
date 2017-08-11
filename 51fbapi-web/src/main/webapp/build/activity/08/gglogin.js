
//获取页面名称传到登录页
var currentUrl=window.location.href;
var index=currentUrl.indexOf('=');
var urlName01=currentUrl.slice(index+1,index+2);
var index=currentUrl.lastIndexOf('=');
var urlName02=currentUrl.slice(index+1);
console.log(urlName02)


    //点击立即登录
    $(".loginbtn").click(function () {
        var userName = $(".pinp").val();//获取手机号
        var password = $(".check").val();//获取密码
        if (/^1(3|4|5|7|8)\d{9}$/i.test(userName)) {
            var password_md5 = String(CryptoJS.MD5(password));//md5加密
            $.ajax({
                url: "/H5GGShare/boluomeActivityLogin",
                type: 'POST',
                dataType: 'JSON',
                data: {
                    userName: userName,
                    password: password_md5,
                    activityId:	1,
                    refUserName:17839218825,
                    urlName:urlName02

                },
                success: function (data) {
                    console.log(data)
                    if(data.success){
                    
                         if(urlName01=="Z"){
                            window.location.href =urlName02;
                        }else{
                            window.location.href ="ggIndexShare";
                        } 
                    }else{
                        requestMsg(data.msg);
                    }
                       
                }
            })
        } else {
            requestMsg("请填写正确的手机号");
        }
    });

    