"use strict";function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}function alaShareData(){var e={appLogin:"Y",type:"share",shareAppTitle:"全民集卡片 领取51元大奖",shareAppContent:"我正在51返呗收集卡片，集齐可领取51元红包！消费有返利又可以赚钱，快来参加啦~",shareAppImage:"http://f.51fanbei.com/h5/app/activity/08/gg31.png",shareAppUrl:domainName+"/fanbei-web/activity/ggIndexShare?loginSource=F&activityId="+activityId+"&userName="+userName+"&sharePage=ggIndexShare",isSubmit:"Y",sharePage:"ggIndexShare"};return JSON.stringify(e)}var activityId=getUrl("activityId"),userName="";getInfo().userName&&(userName=getInfo().userName);var num,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#ggIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GG/initHomePage",data:{activityId:activityId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),console.log(self.content.superPrizeStatus),self.$nextTick(function(){for(var e=0;e<self.content.boluomeCouponList.length;e++)"Y"==self.content.boluomeCouponList[e].isHas&&$(".coupon").eq(e).addClass("changeGray")}),self.$nextTick(function(){var e=$(".cont1").html();$(".cont2").html(e),wordMove()}),self.$nextTick(function(){self.imgSwiper();for(var e=0;e<self.content.itemsList.length;e++)(num=self.content.itemsList[e].num)>=2&&($(".card").eq(e).find(".num").css("display","block"),$(".presentCard").attr("present","Y"))})}})},imgSwiper:function(){new Swiper(".banner",{loop:!0,speed:1e3,autoplay:2e3,autoplayDisableOnInteraction:!1})},joinAmountClick:function(){window.location.href="ggrankingList?activityId="+activityId},bannerClick:function(e){var t=e.value2;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:t},success:function(e){console.log(e),e.success,location.href=e.url},dataType:"JSON",error:function(){requestMsg("请求失败")}})},couponClick:function(e){var t=this,n=t.content.boluomeCouponList[e].sceneId;$.ajax({url:"/fanbei-web/pickBoluomeCouponV1",type:"POST",dataType:"JSON",data:{sceneId:n},success:function(n){n.success?"N"==t.content.boluomeCouponList[e].isHas?(requestMsg(n.msg),$(".coupon").eq(e).addClass("changeGray")):requestMsg(n.msg):window.location.href=n.url},error:function(){requestMsg("请求失败")}})},cardClick:function(e){var t=e.refId;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:t},dataType:"JSON",success:function(e){e.success,location.href=e.url},error:function(){requestMsg("请求失败")}})},finalPrize:function(){var e=this;$.ajax({type:"get",url:"/H5GG/pickUpSuperPrize",data:{activityId:activityId},dataType:"JSON",success:function(t){if(console.log(t),t.success){$(".mask").css("display","block"),$(".alertFinalPrize").css("display","block"),e.content.superPrizeStatus="YN";for(var n=0;n<e.content.itemsList.length;n++){if(0==(num=e.content.itemsList[n].num))return"";$(".card").eq(n).find(".num").html("x"+(num-1)),console.log(num-1),num-1==0?($(".card").eq(n).find(".gray").css("display","block"),$(".card").eq(n).find(".num").css("display","none"),$(".presentCard").attr("present","N")):num-1==1?($(".card").eq(n).find(".num").css("display","none"),$(".presentCard").attr("present","N")):num-1>=2&&$(".presentCard").attr("present","Y")}}else"N"==e.content.superPrizeStatus&&""==t.url?window.location.href=t.data.loginUrl:(e.content.superPrizeStatus,requestMsg(t.msg))},error:function(){requestMsg("请求失败")}})},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none"),$(".alertFinalPrize").css("display","none")},fixImgUrl:function(e){return"http://f.51fanbei.com/h5/app/activity/08/gg000"+e+".png"}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
