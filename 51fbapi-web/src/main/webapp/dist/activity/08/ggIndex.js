"use strict";function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}function alaShareData(){var e={appLogin:"Y",type:"share",shareAppTitle:"消费有返利 领取88.88元现金红包！",shareAppContent:"我正在51返呗玩场景点亮活动，你也一起来玩吧~",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:domainName+"/fanbei-web/activity/ggIndexShare?loginSource=F",isSubmit:"Y",sharePage:"ggIndexShare"};return JSON.stringify(e)}var activityId=getUrl("activityId"),num,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#ggIndex",data:{content:{},finalPrizeMask:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GG/initHomePage",data:{activityId:activityId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){var e=$(".cont1").html();$(".cont2").html(e),wordMove()}),self.$nextTick(function(){function e(){n==s&&($(".banner .bannerList").css({left:0}),n=1),-1==n&&($(".banner .bannerList").css({left:6.25*-(s-1)+"rem"}),n=s-2),$(".banner .bannerList").stop().animate({left:6.25*-n+"rem"},500),n==s-1?$(".banner .num li").eq(0).addClass("on").siblings().removeClass("on"):$(".banner .num li").eq(n).addClass("on").siblings().removeClass("on")}var n=0,t=$(".banner .bannerList li").first().clone();$(".banner .bannerList").append(t);var s=self.content.bannerList.length+1,a=6.25*s+"rem";$(".banner .bannerList li").width("6.25rem"),$(".banner .bannerList").width(a);for(var o=0;o<s-1;o++)$(".banner .num").append("<li></li>");$(".banner .num li").first().addClass("on"),setInterval(function(){n++,e()},1500)});for(var couponList=self.content.boluomeCouponList,i=0;i<couponList.length;i++)couponList[i]=eval("("+couponList[i]+")");for(var j=0;j<self.content.itemsList.length;j++)num=self.content.itemsList[j].num,0==num?self.finalPrizeMask=!0:1==num?self.finalPrizeMask=!1:(self.finalPrizeMask=!1,$(".presentCard").attr("present","Y"))}})},bannerClick:function(e){var n=e.value2;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:n},success:function(e){console.log(e),e.success,location.href=e.url},dataType:"JSON",error:function(){requestMsg("请求失败")}})},couponClick:function(e){var n=e.sceneId;$.ajax({url:"/fanbei-web/pickBoluomeCouponV1",type:"POST",dataType:"JSON",data:{sceneId:n},success:function(e){e.success?requestMsg(e.msg):window.location.href=e.url},error:function(){requestMsg("请求失败")}})},cardClick:function(e){var n=e.refId;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:n},dataType:"JSON",success:function(e){e.success,location.href=e.url},error:function(){requestMsg("请求失败")}})},finalPrize:function(){this.finalPrizeMask||$.ajax({type:"get",url:"/H5GG/pickUpSuperPrize",data:{activityId:activityId},dataType:"JSON",success:function(e){console.log(e),e.success,requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none")}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
