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
                        window.location.href = urlName + "?userName=" + userName +"&activityId=" + activityId + "&userItemsId=" + userItemsId;
                    }else if (word == "S"){
                        window.location.href = urlName + "?userName=" + userName +"&itemsId=" + itemsId;
                    }else {
                        window.location.href ="ggIndexShare" + "?activityId=" + activityId + "&userName=" + userName;
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



    $("#gg_register").click(function(){
        window.location.href= "ggregister?word=" + word + "&userName=" + userName +"&activityId=" + activityId + "&userItemsId=" + userItemsId +"&itemsId=" + itemsId;
    });

    $("#gg_forget").click(function(){
        window.location.href= "ggVerify?word=" + word + "&userName=" + userName +"&activityId=" + activityId + "&userItemsId=" + userItemsId +"&itemsId=" + itemsId;
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