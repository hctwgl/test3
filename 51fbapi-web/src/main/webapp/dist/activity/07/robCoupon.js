"use strict";var activityId=getUrl("activityId"),vm=new Vue({el:"#robCoupon",data:{content:{},couponContent:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/encoreActivityInfo",data:{activityId:activityId},success:function success(data){function showTime(e){diff=parseInt((e-currentTime)/1e3);var t=0,n=0,i=0;t=Math.floor(diff/3600),n=Math.floor(diff/60)-60*t,i=Math.floor(diff)-60*t*60-60*n,t<=9&&(t="0"+t),n<=9&&(n="0"+n),i<=9&&(i="0"+i),$(".countTime").find("span").eq(0).html(t),$(".countTime").find("span").eq(1).html(n),$(".countTime").find("span").eq(2).html(i)}function interval(e,t,n,i,o){currentTime<e?(e-=1e3,showTime(e),$(".timeName").html("10:00")):currentTime>=e&&currentTime<o?currentTime<t?(t-=1e3,showTime(t),$(".timeName").html("14:00")):currentTime>=t&&currentTime<n?(n-=1e3,showTime(n),$(".timeName").html("10:00")):currentTime>=n&&(i-=1e3,showTime(i),$(".timeName").html("14:00")):($(".timeName").html("14:00"),$(".desWord").find("p").eq(2).hide(),$(".countTime").hide());setInterval(function(){currentTime<e?(e-=1e3,showTime(e),$(".timeName").html("10:00")):currentTime>=e&&currentTime<o?currentTime<t?(t-=1e3,showTime(t),$(".timeName").html("14:00")):currentTime>=t&&currentTime<n?(n-=1e3,showTime(n),$(".timeName").html("10:00")):currentTime>=n&&(i-=1e3,showTime(i),$(".timeName").html("14:00")):($(".timeName").html("14:00"),$(".desWord").find("p").eq(2).hide(),$(".countTime").hide())},1e3)}self.content=eval("("+data+")"),self.content=self.content.data,console.log(self.content);var currentTime=self.content.currentTime,validStartTime=self.content.validStartTime+36e6,startMore=self.content.validStartTime+864e5,validEndTime=self.content.validEndTime,date=new Date(currentTime),year=date.getFullYear(),month=date.getMonth()+1,day=date.getDate(),dateStr01=year+"/"+month+"/"+day+" 10:00:00",dateStr02=year+"/"+month+"/"+day+" 16:34:00",dateStr03=year+"/"+month+"/"+(day+1)+" 10:00:00",currentTen=new Date(dateStr01).getTime(),currentFourteen=new Date(dateStr02).getTime(),nextTen=new Date(dateStr03).getTime(),diff=0;interval(validStartTime,currentTen,currentFourteen,nextTen,validEndTime)},error:function(){requestMsg("请求失败")}}),$.ajax({type:"post",url:"/fanbei-web/superCouponList",success:function success(data){self.couponContent=eval("("+data+")"),self.couponContent=self.couponContent.data,console.log(self.couponContent)},error:function(){requestMsg("请求失败")}})},buyNow:function(e){var t=this;window.location.href=t.content.notifyUrl+'&params={"goodsId":"'+e+'"}'},ruleClick:function(){$(".mask").show(),$(".rule").show()},maskClick:function(){$(".mask").hide(),$(".rule").hide()},couponClick:function(e){var t=this,n=t.couponContent.couponInfoList[e].couponId,i=t.couponContent.userName;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:n,userName:i},success:function(e){if(e.success)requestMsg("优惠劵领取成功");else{var t=e.data.status;"USER_NOT_EXIST"==t&&(window.location.href=e.url),"OVER"==t&&(requestMsg(e.msg),requestMsg("优惠券个数超过最大领券个数")),"COUPON_NOT_EXIST"==t&&requestMsg(e.msg)}},error:function(){requestMsg("请求失败")}})},txtFix:function(e){return function(e,t){for(var n=0,i=0;i<e.length;i++){var o=e.charAt(i);if(encodeURI(o).length>2?n+=1:n+=.5,n>=t){var r=n==t?i+1:i;return e.substr(0,r)}}}(e,20)}}});
//# sourceMappingURL=../../_srcmap/activity/07/robCoupon.js.map
