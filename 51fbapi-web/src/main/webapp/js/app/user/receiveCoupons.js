/*
* @Author: Yangyang
* @Date:   2017-03-08 17:42:52
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-09 11:13:12
* @title:  领取优惠劵
*/


var couponListString = $("#couponList").val();
var couponList = eval('(' + couponListString + ')');
var userName = $("#userName").val();

// 领取优惠劵
$(function(){

    $(".receiveCoupons_main li").bind("click",function(){
        
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

                    $(".receiveCoupons_main li").eq(i).addClass("receiveCoupons_alreadyReceive");

                } else {
                    
                    if (returnData.url.length>0) {
                        window.location.href = returnData.url;
                    }else{
                        requestMsg(returnData.msg);
                    }
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });

        $(this).unbind("click");
    });
});
