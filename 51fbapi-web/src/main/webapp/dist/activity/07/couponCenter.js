"use strict";var tabWidth=0,liWidth=0,ulWidth=0,vm=new Vue({el:"#couponCenter",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/couponCategoryInfo",success:function success(data){function format(t){var o=new Date(t),n=o.getFullYear(),e=o.getMonth()+1,i=o.getDate();return e<10&&(e="0"+e),i<10&&(i="0"+i),n+"."+e+"."+i}self.content=eval("("+data+")").data,console.log(self.content),""!=self.content.bannerImage&&void 0!=self.content.bannerImage||$(".banner").css("display","none");var liLength=self.content.couponCategoryList.length;self.$nextTick(function(){if($(".navList li").eq(0).addClass("border"),$(".contList li").eq(0).addClass("active"),liLength<2)$("#tabNav").hide(),$("#content").css("padding-top",0);else if(liLength>=2&&liLength<=5){tabWidth=$("#tabNav").width();for(var t=0;t<liLength;t++)liWidth+=$(".navList").find("li").eq(t).width();var o=(tabWidth-liWidth)/(liLength+1);$(".navList").find("li").css("margin-left",o)}else{tabWidth=$("#tabNav").width();for(var n=0,e=0;e<5;e++)n+=$(".navList").find("li").eq(e).width();for(var t=0;t<liLength;t++)liWidth+=$(".navList").find("li").eq(t).width();var o=(tabWidth-n)/6;$(".navList").find("li").css("margin-left",o),$(".kong").css("width",o),ulWidth=liWidth+(liLength+1)*o,$(".navList").width(ulWidth)}});for(var diff=0,i=0;i<liLength;i++){for(var couponCategory=self.content.couponCategoryList[i],j=0;j<couponCategory.couponInfoList.length;j++){var currentTime=couponCategory.couponInfoList[j].currentTime,startTime=couponCategory.couponInfoList[j].gmtStart,endTime=couponCategory.couponInfoList[j].gmtEnd;couponCategory.couponInfoList[j].start=format(startTime),couponCategory.couponInfoList[j].end=format(endTime),diff=(endTime-currentTime)/1e3;var h=parseInt(diff/3600),state;state=0<h&&h<48?"timeOver":"noTimeOver",couponCategory.couponInfoList[j].state=state}for(var j=0;j<couponCategory.couponInfoList.length;j++)for(var k=0;k<couponCategory.couponInfoList.length;k++)if(couponCategory.couponInfoList[j].gmtEnd<couponCategory.couponInfoList[k].gmtEnd){var tmp=couponCategory.couponInfoList[k];couponCategory.couponInfoList[k]=couponCategory.couponInfoList[j],couponCategory.couponInfoList[j]=tmp}}},error:function(){requestMsg("请求失败")}})},liClick:function(t){$(".navList li").eq(t).addClass("border"),$(".navList li").eq(t).siblings().removeClass("border"),$(".contList").find("li").eq(t).show().siblings().hide()},couponClick:function(t){var o=t.couponId,n=t.shopUrl,e=t.type;"N"!=t.isDraw||"FULLVOUCHER"!=e&&"CASH"!=e&&"ACTIVITY"!=e?"Y"==t.isDraw&&$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:o},success:function(o){if(o.success)requestMsg("优惠劵领取成功"),t.isDraw="N";else{var n=o.data.status;"USER_NOT_EXIST"==n&&(window.location.href=o.url),"OVER"==n&&(requestMsg(o.msg),requestMsg("优惠券个数超过最大领券个数")),"COUPON_NOT_EXIST"==n&&(requestMsg(o.msg),$(".couponLi").eq(i).css("display","none"))}},error:function(){requestMsg("请求失败")}}):window.location.href=n||"/fanbei-web/opennative?name=APP_HOME"}}});
//# sourceMappingURL=../../_srcmap/activity/07/couponCenter.js.map
