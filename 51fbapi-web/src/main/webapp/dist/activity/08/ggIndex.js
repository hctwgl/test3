"use strict";function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}function alaShareData(){var e={appLogin:"Y",type:"share",shareAppTitle:"全民集卡片 领取51元大奖",shareAppContent:"我正在51返呗收集卡片，参与活动享霸王餐，免费领取51元啦~",shareAppImage:"http://f.51fanbei.com/h5/app/activity/08/gg31.png",shareAppUrl:domainName+"/fanbei-web/activity/ggIndexShare?loginSource=F&activityId="+activityId+"&userName="+userName+"&sharePage=ggIndexShare",isSubmit:"Y",sharePage:"ggIndexShare"};return JSON.stringify(e)}var activityId=getUrl("activityId"),userName="";getInfo().userName&&(userName=getInfo().userName);var num,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#ggIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GG/initHomePage",data:{activityId:activityId},success:function success(data){data=eval("("+data+")"),console.log(data),data.success&&(self.content=data.data,console.log(self.content),self.$nextTick(function(){for(var e=0;e<self.content.boluomeCouponList.length;e++)"Y"==self.content.boluomeCouponList[e].isHas&&$(".coupon").eq(e).addClass("changeGray")}),self.$nextTick(function(){var e=$(".cont1").html();$(".cont2").html(e),wordMove()}),self.$nextTick(function(){if(self.imgSwiper(),"YN"==self.content.superPrizeStatus)for(var e=0;e<self.content.itemsList.length;e++)num=self.content.itemsList[e].num,num>=1&&($(".presentButton").attr("present","Y"),$(".presentButton").attr("superPrize","Y")),num>=2&&$(".card").eq(e).find(".num").css("display","block");else for(var t=0;t<self.content.itemsList.length;t++)(num=self.content.itemsList[t].num)>=2&&($(".card").eq(t).find(".num").css("display","block"),$(".presentButton").attr("present","Y"),$(".presentButton").attr("superPrize","N"))}),self.$nextTick(function(){self.content.popupWords&&($(".toast").show(),$(".mask").show())}))}})},imgSwiper:function(){new Swiper(".banner",{loop:!0,speed:1300,autoplay:2e3,autoplayDisableOnInteraction:!1})},joinAmountClick:function(){window.location.href="ggrankingList?activityId="+activityId},bannerClick:function(e){var t=e.value2;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:t},success:function(e){console.log(e),e.success,location.href=e.url},dataType:"JSON",error:function(){requestMsg("请求失败")}})},couponClick:function(e){var t=this,n=t.content.boluomeCouponList[e].sceneId;$.ajax({url:"/fanbei-web/pickBoluomeCouponV1",type:"POST",dataType:"JSON",data:{sceneId:n},success:function(n){n.success?"N"==t.content.boluomeCouponList[e].isHas?(requestMsg(n.msg),$(".coupon").eq(e).addClass("changeGray")):requestMsg(n.msg):window.location.href=n.url},error:function(){requestMsg("请求失败")}})},cardClick:function(e){var t=e.refId;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:t},dataType:"JSON",success:function(e){e.success,location.href=e.url},error:function(){requestMsg("请求失败")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggIndex?activityId=1&type=card&typeId="+t},success:function(e){console.log(e)}})},finalPrize:function(){var e=this;$.ajax({type:"get",url:"/H5GG/pickUpSuperPrize",data:{activityId:activityId},dataType:"JSON",success:function(t){if(console.log(t),t.success){$(".mask").css("display","block"),$(".alertFinalPrize").css("display","block"),e.content.superPrizeStatus="YN",$(".presentButton").attr("superPrize","Y");for(var n=0;n<e.content.itemsList.length;n++){if(0==(num=e.content.itemsList[n].num))return"";$(".card").eq(n).find(".num").html("x"+(num-1)),console.log(num-1),num-1==0?($(".card").eq(n).find(".gray").css("display","block"),$(".card").eq(n).find(".num").css("display","none")):num-1==1?($(".card").eq(n).find(".num").css("display","none"),$(".presentButton").attr("present","Y")):num-1>=2&&$(".presentButton").attr("present","Y")}}else"NY"==e.content.superPrizeStatus?window.location.href=t.data.loginUrl:(e.content.superPrizeStatus,requestMsg(t.msg))},error:function(){requestMsg("请求失败")}})},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none"),$(".alertFinalPrize").css("display","none"),$(".toast").css("display","none")},fixImgUrl:function(e){return"http://f.51fanbei.com/h5/app/activity/10/ggNewCard0"+e+".png"},toastClick:function(){var e=this,t=e.content.shopId;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:t},dataType:"JSON",success:function(e){e.success,location.href=e.url},error:function(){requestMsg("请求失败")}})}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
