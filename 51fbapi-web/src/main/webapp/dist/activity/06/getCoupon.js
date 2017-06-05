"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName),$(function(){$.ajax({url:"/fanbei-web/receiveCoupons",data:{userName:userName},type:"get",success:function(e){console.log(e)}})});
//# sourceMappingURL=../../_srcmap/activity/06/getCoupon.js.map
