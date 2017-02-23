/*
* @Author: Yangyang
* @Date:   2017-02-14 16:52:58
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-02-23 17:51:58
* @title:  领取优惠券
*/


// 领取优惠劵
$(function(){
	$(".cardCoupons_btn").click(function(event) {
		$(this).parent().addClass("cardCoupons_alreadyReceive");
		$(this).attr("disabled","true");
	});
})