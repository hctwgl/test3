
//获取页面名称传到登录页
var currentUrl=window.location.href;
var index=currentUrl.indexOf('=');
var urlName01=currentUrl.slice(index+1,index+2);
var index=currentUrl.lastIndexOf('=');
var urlName02=currentUrl.slice(index+1);
console.log(urlName02)

var currentUrl = "http://192.168.96.210/fanbei-web/activity/ggIndexShare?loginSource=F&activityId=1&userName=15839790051&from=singlemessage&isappinstalled=1";
var index01=currentUrl.indexOf("?");
var str=currentUrl.substring(index01+1);////获取?后面的字符串
var arr=[];
arr=str.split("&");//获取?后面以&分隔的字符串
var activityId=arr[1].slice(arr[1].indexOf("=")+1);//获取arr数组里面的具体值
var userName=arr[2].slice(arr[2].indexOf("=")+1);



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
                    activityId:activityId,
                    refUserName:userName,
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

    