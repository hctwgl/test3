    
$(function(){

          var number=localStorage.getItem('user');//接收上个页面的手机号
          var mesg=localStorage.getItem('mesg');//接收上个页面的短信验证  
        //   var password = $(".blank-in").val();//获取登录密码

          console.log(mesg);
          console.log(number);
        //点击完成跳转到手机验证
        $(".btn").click(function (){
                var password = $(".blank-in").val();//获取登录密码
                var password_md5 = String(CryptoJS.MD5(password));//md5加密
                console.log(password_md5);
           
                $.ajax({
                    url: "/H5GGShare/boluomeActivityResetPwd",
                    type: 'POST',
                    dataType: 'JSON',
                    data: {
                        password: password_md5,                       	
                        mobile:number,
                        verifyCode:mesg
                    },
                    success: function (data) {
                        console.log(data)
                        if(data.success){
                            
                            window.location.href ="gglogin";
                        }else{
                            requestMsg(data.msg);
                        }
                        
                    }
                  
                })
            
        });


})
