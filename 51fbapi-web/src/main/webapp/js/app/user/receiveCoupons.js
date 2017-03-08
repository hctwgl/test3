/*
* @Author: Yangyang
* @Date:   2017-03-08 17:42:52
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-08 17:56:56
* @title:  领取优惠劵
*/



// 点击领取优惠劵
var userName = "13500000405";
var couponList = $("#couponList").val();

// 领取优惠劵
$(function(){
	$(".receiveCoupons_btn").click(function(event) {

		for (var i = 0; i < couponList.length; i++) {
			var couponIdNum = couponList[i].rid;
			console.log(couponIdNum);
		}

		$.ajax({
            url: "/app/user/pickCoupon",
            type: "POST",
            dataType: "JSON",
            data: {
                couponId: couponIdNum,
                userName: userName
            },
            success: function(returnData){

                if (returnData.success) {

                    $(this).parent().addClass("receiveCoupons_alreadyReceive");
					$(this).attr("disabled","true");

                } else {
                    requestMsg(returnData.msg);
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });
	});
});