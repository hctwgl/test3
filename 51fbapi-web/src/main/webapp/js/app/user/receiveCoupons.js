/*
* @Author: Yangyang
* @Date:   2017-03-08 17:42:52
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-08 17:56:56
* @title:  领取优惠劵
*/



// 点击领取优惠劵
var couponListString = $("#couponList").val();
var couponList = eval('(' + couponListString + ')');

var userName = $("#userName").val();

// 领取优惠劵
$(function(){
	$(".receiveCoupons_btn").click(function(event) {

        var kkk= $(this).index();
        console.log(kkk);
        var couponIdNum = couponList[kkk].rid;

		
		$.ajax({
            url: "/fanbei-web/pickCoupon",
            type: "POST",
            dataType: "JSON",
            data: {
                couponId: couponIdNum,
                userName: userName
            },
            success: function(returnData){
                alert(returnData.success);
                console.log(returnData);

                if (returnData.success) {

                    $(this).parent().addClass("receiveCoupons_alreadyReceive");
					$(this).attr("disabled","true");

                } else {
                    if (returnData.url.length>0) {
                        window.location.href = returnData.url;

                    }
                    console.log(returnData);
                    requestMsg(returnData.msg);
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });
	});
});