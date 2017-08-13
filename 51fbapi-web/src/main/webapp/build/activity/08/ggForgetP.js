    
$(function(){
        //获取页面名称传到登录页
        var currentUrl = window.location.href;
        var param = getUrlParam(currentUrl);
        var word = param['word'];
        var urlName = param['urlName'];
        var userName = param['userName'];
        var activityId = param['activityId'];
        var userItemsId = param['userItemsId'];
        var itemsId = param['itemsId'];
        var number=localStorage.getItem('user');//接收上个页面的手机号
        var mesg=localStorage.getItem('mesg');//接收上个页面的短信验证  
        //   var password = $(".blank-in").val();//获取登录密码
        console.log(mesg);
        console.log(number);
        //点击完成跳转到手机验证
        $(".btn").click(function (){
                var password = $(".blank-in").val();//获取登录密码
                var password_md5 = String(CryptoJS.MD5(password));//md5加密
                var mmtrue=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(password);//密码正则验证
                console.log(password_md5);
                console.log(mmtrue);
                if(mmtrue){
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
                                    window.location.href ="gglogin?urlName="+urlName+"&userName="+userName+"&activityId="+activityId+"&userItemsId="+userItemsId+"&itemsId="+itemsId + "&word=" + word;;
                                }else{
                                    requestMsg(data.msg);
                                }
                            }
                        
                        })
                    }else{
                        requestMsg('请填写6-18位的数字、字母、字符组成的密码');
                    }
                });
})
//截取字符串方法
function getUrlParam(url) {
    var param = new Object(); 
    if (url.indexOf("?") != -1) { 
        var str = url.substr(url.indexOf("?")+1,url.length); 
        var strs=[];
        strs = str.split("&"); 
        for(var i = 0; i < strs.length; i ++) { 
            param[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
        } 
    } 
    return param; 

}