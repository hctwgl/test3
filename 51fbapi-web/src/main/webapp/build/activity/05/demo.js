/*
* @Author: yoe
* @Date:   2017-05-22 20:43:19
* @Last Modified by:   yoe
* @Last Modified time: 2017-06-02 21:16:28
*/



var returnNum = getBlatFrom();  // 判断1为Android，2为ios


/*------------跳转登陆+请求返回数据--------------*/
if(returnNum == 1){  // android机型
    $(".footer").click(function(){
        alaAndroid.appLogin();  // 调用Android原生登陆
    });
}else{  // ios机型
    $(".footer").click(function(){
        alaIos.appLogin();  // 调用ios原生登陆
    });
}

function alaRequestMsg(msg){
    requestMsg(msg);
}


// 登陆后回调客户端loginSuccess操作领劵
function loginSuccess(obj) {

    var obj = JSON.stringify(obj); // json对象转换成json字符串
    var obj = JSON.parse(obj);  // 将json字符串转换成json对象

    var userName = obj.userName;
    var mobile = obj.mobile;

    $.ajax({
        url: 'pickBoluomeCoupon',
        data:{'sceneId':'387','userName':userName},
        type: 'post',
        success:function (data) {
            data=eval('(' + data + ')');
            if(data.success){
                requestMsg("领劵成功")
            }else{
                if(data.url){
                    location.href=data.url;
                }else{
                   alaIos.alaRequestMsg(data.msg);
                }
            }
        }
    });
}




// 分享
var dataObj = {
    'shareAppTitle': '分享抓娃娃游戏获取优惠券',
    'shareAppContent': '抓娃娃游戏',
    'shareAppUrl': 'www.baidu.com'
}

var dataStr = JSON.stringify(dataObj);  // jsons数组转换成json对象
console.log(dataStr);

if(returnNum == 1){  // android机型
    alaAndroid.appLogin(dataStr);  // 调用Android原生登陆
}

function shareData(dataStr){
    return dataStr;
}









/*------------关闭首页webView弹窗----------------*/
/*if(returnNum == 1){  // android机型
    $(".footer").click(function(){
        alaAndroid.closeWebView();  // 关闭首页webView弹窗
    });

}else{  // ios机型
    $(".footer").click(function(){
        alaIos.closeWebView();  // 关闭首页webView弹窗
    });
}*/



/*------------单纯跳转的页面----------------*/
// if(returnNum == 1){  // android机型
//     $(".footer").click(function(){

//         /*var jsonObj = {
//             "className": "SignInActivity",
//             "isLogin": "Y"
//         }
//         var jsonString = jsonObj.stringify();*/

//         var jsonString = '{"className":"com.alfl.www.business.ui.SignInActivity","isLogin":"Y"}'

//         alaAndroid.openActivity(jsonString);
//     });

// }else{  // ios机型
//     $(".footer").click(function(){
//         /*alert(1111);
//         var jsonObj = {
//             "className":"ALASignInViewController",
//             "isLogin":"Y"
//         }
//         alert(jsonObj);
//         var jsonString = jsonObj.stringify();
//         alert(jsonString);*/

//         var jsonString = '{"className":"ALASignInViewController","isLogin":"Y"}'
//         alaIos.openActivity(jsonString);
//     });
// }



