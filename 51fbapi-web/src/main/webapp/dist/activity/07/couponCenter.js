"use strict";var tabWidth=0,liWidth=0,ulWidth=0,vm=new Vue({el:"#couponCenter",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/couponCategoryInfo",success:function success(data){function format(t){var e=new Date(t),o=e.getFullYear(),n=e.getMonth()+1,i=e.getDate();return n<10&&(n="0"+n),i<10&&(i="0"+i),o+"."+n+"."+i}self.content=eval("("+data+")").data,console.log(self.content);var liLength=self.content.couponCategoryList.length;self.$nextTick(function(){$(".navList li").eq(0).find("span").addClass("border"),$(".contList li").eq(0).addClass("active"),liLength<2?$("#tabNav").hide():liLength>=2&&liLength<=5&&(tabWidth=$("#tabNav").width(),liWidth=tabWidth/liLength,$(".navList").find("li").width(liWidth))});for(var diff=0,i=0;i<liLength;i++)for(var couponCategory=self.content.couponCategoryList[i],j=0;j<couponCategory.couponInfoList.length;j++){var currentTime=couponCategory.couponInfoList[j].currentTime,startTime=couponCategory.couponInfoList[j].gmtStart,endTime=couponCategory.couponInfoList[j].gmtEnd;couponCategory.couponInfoList[j].start=format(startTime),couponCategory.couponInfoList[j].end=format(endTime),diff=(endTime-currentTime)/1e3;var h=parseInt(diff/3600),state;state=h<48?"timeOver":"noTimeOver",couponCategory.couponInfoList[j].state=state}},error:function(){requestMsg("请求失败")}})},liClick:function(t){console.log(t),$(".navList li").eq(t).find("span").addClass("border"),$(".navList li").eq(t).siblings().find("span").removeClass("border"),$(".contList").find("li").eq(t).show().siblings().hide()},couponClick:function(t){var e=t.couponId,o=t.shopUrl,n=t.type;"FULLVOUCHER"!=n&&"CASH"!=n&&"ACTIVITY"!=n||("Y"==t.isDraw?window.location.href=o||"/fanbei-web/opennative?name=APP_HOME":$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:e},success:function(e){if(e.success)requestMsg("优惠劵领取成功"),t.isDraw="Y";else{var o=e.data.status;"USER_NOT_EXIST"==o&&(window.location.href=e.url),"OVER"==o&&(requestMsg(e.msg),requestMsg("优惠券个数超过最大领券个数")),"COUPON_NOT_EXIST"==o&&(requestMsg(e.msg),$(".couponLi").eq(i).css("display","none"))}},error:function(){requestMsg("请求失败")}}))}}});
//# sourceMappingURL=../../_srcmap/activity/07/couponCenter.js.map
