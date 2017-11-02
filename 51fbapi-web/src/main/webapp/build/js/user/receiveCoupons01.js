/*
* @Author: Yangyang
* @Date:   2017-03-08 17:42:52
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-24 17:50:09
* @title:  领取优惠劵
*/


var couponListString = $("#couponList").val();
var couponList = eval('(' + couponListString + ')');
var userName = $("#userName").val();

// 领取优惠劵
$(function(){

    $(".receiveCoupons_main li").click(function(){
        
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
                    $(".receiveCoupons_main li").eq(i).addClass("receiveCoupons_ylq");

                } else {
                    
                    var status = returnData.data["status"];

                    if (status == "USER_NOT_EXIST") { // 用户不存在
                        window.location.href = returnData.url;
                    }

                    if (status == "OVER") { // 优惠券个数超过最大领券个数
                        requestMsg(returnData.msg);
                        $(".receiveCoupons_main li").eq(i).addClass("receiveCoupons_ylq");
                    }

                    if (status == "MORE_THAN") { // 优惠券已领取完
                        requestMsg(returnData.msg);
                        $(".receiveCoupons_main li").eq(i).addClass("receiveCoupons_ylw");
                    }

                    if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                        requestMsg(returnData.msg);
                        $(".receiveCoupons_main li").eq(i).css('display', 'none');;
                    }
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });
        $(this).unbind("click"); // 移除当前元素的点击时间(禁止重复点击)
    });
});

