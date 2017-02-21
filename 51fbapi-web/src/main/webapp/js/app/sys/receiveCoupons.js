/*
* @Author: Yangyang
* @Date:   2017-02-14 16:52:58
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-02-18 15:23:20
* @title:  领取优惠券
*/


// 领取优惠劵
$(function(){
	$(".cardCoupons_btn").click(function(event) {
		$(this).addClass('cardCoupons_btn_gray');
		$(this).attr("disabled","true");
	});
})