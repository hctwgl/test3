"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName);var vm=new Vue({el:"#ggIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:1},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),wordMove();for(var couponList=self.content.boluomeCouponList,i=0;i<couponList.length;i++)couponList[i]=eval("("+couponList[i]+")")}})},couponClick:function(e){var s=e.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:s},success:function(e){if(e.success)requestMsg("恭喜您领券成功");else{var s=e.data.status;"USER_NOT_EXIST"==s&&(window.location.href=e.url),"OVER"==s&&(requestMsg(e.msg),requestMsg("您已领过优惠券了，快去使用吧 ~")),"COUPON_NOT_EXIST"==s&&(requestMsg(e.msg),requestMsg("您下手慢了哦，优惠券已领完，下次再来吧 ~"))}},error:function(){requestMsg("请求失败")}})},cardClick:function(e){var s=e.refId;$.ajax({type:"post",url:"/fanbei-web/getBrandUrl",data:{shopId:s,userName:userName},dataType:"JSON",success:function(e){e.success?(console.log(e),location.href=e.url):location.href=e.url},error:function(){requestMsg("请求失败")}})},finalPrize:function(){},presentClick:function presentClick(){$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".sure").html("确定赠送");var self=this;$.ajax({type:"get",url:"/H5GGShare/sendItems",data:{activityId:1},success:function success(data){self.content=eval("("+data+")").data,console.log(data)}})},demandClick:function demandClick(){$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".demandTitle").css("display","block"),$(".sure").html("确定索要");var self=this;$.ajax({type:"get",url:"/H5GGShare/sendItems",data:{itemsId:itemsId,userId:userId},success:function success(data){self.content=eval("("+data+")").data,console.log(data)}})},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none")}}});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
