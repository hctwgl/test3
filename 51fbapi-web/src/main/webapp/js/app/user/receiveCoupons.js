/*
* @Author: Yangyang
* @Date:   2017-02-14 16:52:58
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-01 10:41:43
* @title:  领取优惠券
*/


// 领取优惠劵
$(function(){
	$(".receiveCoupons_btn").click(function(event) {
		$(this).parent().addClass("receiveCoupons_alreadyReceive");
		$(this).attr("disabled","true");
	});
});