$(function(){
//获取页面名称传到登录页
var currentUrl = window.location.href;
var param = getUrlParam(currentUrl);
var word = param['word'];
var urlName = param['urlName'];
var userName = param['userName'];
var activityId = param['activityId'];
var userItemsId = param['userItemsId'];
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
                urlName:word
            },
            success: function (data) {
                console.log(data)
                if(data.success){
                    if(word=="Z"){
                        window.location.href = urlName + "?userName=" + userName +"&activityId=" + activityId + "&userItemsId" + userItemsId;
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