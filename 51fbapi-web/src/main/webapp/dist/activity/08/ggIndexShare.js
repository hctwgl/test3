"use strict";function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}var activityId=getUrl("activityId"),userName="",currentUrl=window.location.href,index=currentUrl.lastIndexOf("/"),urlName=currentUrl.slice(index+1);urlName=urlName.replace(/\?/g,"&");var num,vm=new Vue({el:"#ggIndexShare",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:activityId},success:function success(data){$(".positionImg").fadeOut(4e3),self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){for(var e=0;e<self.content.boluomeCouponList.length;e++)"Y"==self.content.boluomeCouponList[e].isHas&&$(".coupon").eq(e).addClass("changeGray")}),self.$nextTick(function(){var e=$(".cont1").html();$(".cont2").html(e),wordMove()}),self.$nextTick(function(){self.imgSwiper();for(var e=0;e<self.content.itemsList.length;e++)(num=self.content.itemsList[e].num)>=2&&$(".card").eq(e).find(".num").css("display","block")})}})},imgSwiper:function(){new Swiper(".banner",{loop:!0,speed:1e3,autoplay:2e3,autoplayDisableOnInteraction:!1})},joinAmountClick:function(){window.location.href="ggrankingList?activityId="+activityId},bannerClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},couponClick:function(e){userName=getCookie("userName");var n=this;if(userName){var t=n.content.boluomeCouponList[e].sceneId;$.ajax({url:"/H5GGShare/pickBoluomeCouponWeb",type:"POST",dataType:"JSON",data:{sceneId:t},success:function(t){t.success&&"N"==n.content.boluomeCouponList[e].isHas?(requestMsg(t.msg),$(".coupon").eq(e).addClass("changeGray")):requestMsg(t.msg)},error:function(){requestMsg("请求失败")}})}else window.location.href="gglogin?urlName="+urlName},cardClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},finalPrize:function(){var e=this;userName=getCookie("userName"),""!=userName&&userName?$.ajax({type:"get",url:"/H5GGShare/pickUpSuperPrize",data:{activityId:activityId},dataType:"JSON",success:function(n){if(console.log(n),n.success){$(".mask").css("display","block"),$(".alertFinalPrize").css("display","block"),e.content.superPrizeStatus="YN";for(var t=0;t<e.content.itemsList.length;t++){if(0==(num=e.content.itemsList[t].num))return"";$(".card").eq(t).find(".num").html("x"+(num-1)),num-1==0?($(".card").eq(t).find(".gray").css("display","block"),$(".card").eq(t).find(".num").css("display","none")):num-1==1&&$(".card").eq(t).find(".num").css("display","none")}}else"N"==e.content.superPrizeStatus?requestMsg(n.msg):"YN"==e.content.superPrizeStatus&&requestMsg(n.msg)},error:function(){requestMsg("请求失败")}}):window.location.href="gglogin?urlName="+urlName},presentClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},demandClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none"),$(".alertFinalPrize").css("display","none")},fixImgUrl:function(e){return"http://f.51fanbei.com/h5/app/activity/08/gg000"+e+".png"}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexShare.js.map
