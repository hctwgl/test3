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
                } else {                    
                    var status = returnData.data["status"];

                    if (status == "USER_NOT_EXIST") { // 用户不存在
                        window.location.href = returnData.url;
                    }

                    if (status == "OVER") { // 优惠券个数超过最大领券个数
                        requestMsg(returnData.msg);
                        requestMsg("优惠券个数超过最大领券个数");                       
                    }

                    if (status == "MORE_THAN") { // 优惠券已领取完
                        requestMsg(returnData.msg);
                        $(".couponLi").eq(i).find('.clickCoupon').css('background','#999999');

                    }

                    if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                        requestMsg(returnData.msg);
                        $(".couponLi").eq(i).css('display', 'none');;
                    }
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });
        $(this).unbind("click"); // 移除当前元素的点击时间(禁止重复点击)
    });

    //点击我的优惠券
    $('.myCoupon').click(function(){
    	location.href="/fanbei-web/opennative?name=MINE_COUPON_LIST";
    })

});



