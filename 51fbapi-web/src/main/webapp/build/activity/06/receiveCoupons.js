//点击我要领券时判断用户是否登录 此时要先判断系统 iPhone和andriod 
// getBlatFrom() == 2代表iOS
//（参考motherDay点击抢优惠券function）

var couponListString = $("#couponList").val();
var couponList = eval('(' + couponListString + ')');

var userName = $("#userName").val();

$(function(){

   // 点击领取优惠劵
    $(".couponLi").click(function(){   
        var i= $(this).index();
        var couponIdNum = couponList[i].rid;
        $.ajax({
            url: "/fanbei-web/pickCoupon",
            type: "POST",
            dataType: "JSON",
            data: {
                couponId: couponIdNum,
                userName: userName
            },
            success: function(returnData){
                if (returnData.success) {
                    requestMsg("优惠劵领取成功");
                    $(".couponLi").eq(i).find('.clickCoupon').html("去用券");
                    $(".couponLi").eq(i).find('.clickCoupon').click(function(){
                        window.location.href = "activity/activityHome";
                    })                   
                } else { 
                    /*console.log(returnData)
                    console.log($('.clickCoupon').attr('dataQuota'))
                    console.log($('.clickCoupon').attr('dataAleady'))    */               
                    var status = returnData.data["status"];
                    if (status == "USER_NOT_EXIST") { // 用户不存在
                        window.location.href = returnData.url;
                    }

                    if (status == "OVER") { // 优惠券个数超过最大领券个数
                        requestMsg(returnData.msg);
                        requestMsg("优惠券个数超过最大领券个数");                 
                    }

                    if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                        requestMsg(returnData.msg);
                        $(".couponLi").eq(i).css('display', 'none');
                    }
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });
        //$(this).unbind("click"); // 移除当前元素的点击时间(禁止重复点击)
    });
});

var returnNum = getBlatFrom();  // 判断1为Android，2为ios

/*------------跳转登陆+请求返回数据--------------*/
if(returnNum == 1){  // android机型
    $(".myCoupon").click(function(){
        alaAndroid.appLogin();  // 调用Android原生登陆
    });
}else{  // ios机型
    $(".myCoupon").click(function(){
        alaIos.appLogin();  // 调用ios原生登陆
    });
}


function loginSuccess() {
    if(returnNum == 1){  // android机型
        //(2222); 
        var jsonString = '{"className":"com.alfl.www.user.ui.VoucherMenuActivity"}';
        alaAndroid.openActivity(jsonString);
    }else{  // ios机型
        location.href="/fanbei-web/opennative?name=MINE_COUPON_LIST";
    }
}

//刷新页面
function refresh(){
    window.location.reload();
}