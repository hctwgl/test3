"use strict";function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}var _typeof="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},activityId=getUrl("activityId"),userName="",currentUrl=window.location.href,index=currentUrl.lastIndexOf("/"),urlName=currentUrl.slice(index+1),num,vm=new Vue({el:"#ggIndexShare",data:{content:{},finalPrizeMask:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:activityId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),console.log(_typeof(self.content.boluomeCouponList[0])),self.$nextTick(function(){var e=$(".cont1").html();$(".cont2").html(e),wordMove()}),self.$nextTick(function(){function move(){i==size&&($(".banner .bannerList").css({left:0}),i=1),-1==i&&($(".banner .bannerList").css({left:6.25*-(size-1)+"rem"}),i=size-2),$(".banner .bannerList").stop().animate({left:6.25*-i+"rem"},500),i==size-1?$(".banner .num li").eq(0).addClass("on").siblings().removeClass("on"):$(".banner .num li").eq(i).addClass("on").siblings().removeClass("on")}var i=0,liWidth="6.25rem",clone=$(".banner .bannerList li").first().clone();$(".banner .bannerList").append(clone);var size=self.content.bannerList.length+1,ulWidth=6.25*size+"rem";$(".banner .bannerList li").width(liWidth),$(".banner .bannerList").width(ulWidth);for(var j=0;j<size-1;j++)$(".banner .num").append("<li></li>");$(".banner .num li").first().addClass("on"),setInterval(function(){i++,move()},1500);for(var couponList=self.content.boluomeCouponList,i=0;i<couponList.length;i++)couponList[i]=eval("("+couponList[i]+")");for(var j=0;j<self.content.itemsList.length;j++)num=self.content.itemsList[j].num,0==num?self.finalPrizeMask=!0:1==num?self.finalPrizeMask=!1:(self.finalPrizeMask=!1,$(".card").eq(j).find(".num").css("display","block"))})}})},joinAmountClick:function(){window.location.href="ggrankingList?activityId="+activityId},bannerClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},couponClick:function(e){if(userName=getCookie("userName")){var n=e.sceneId;$.ajax({url:"/H5GGShare/pickBoluomeCouponWeb",type:"POST",dataType:"JSON",data:{sceneId:n},success:function(e){e.success,requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})}else window.location.href="gglogin?urlName="+urlName},cardClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},finalPrize:function(){var e=this;userName=getCookie("userName"),""!=userName&&userName?e.finalPrizeMask||$.ajax({type:"get",url:"/H5GGShare/pickUpSuperPrize",data:{activityId:activityId},dataType:"JSON",success:function(n){if(console.log(n),n.success){requestMsg(n.msg);for(var t=0;t<e.content.itemsList.length;t++){if(0==(num=e.content.itemsList[t].num))return"";$(".card").eq(t).find(".num").html("x"+(num-1)),num-1==0?($(".card").eq(t).find(".cardMask").css("display","block"),$(".card").eq(t).find(".num").css("display","none")):num-1==1&&$(".card").eq(t).find(".num").css("display","none")}}else requestMsg(n.msg)},error:function(){requestMsg("请求失败")}}):window.location.href="gglogin?urlName="+urlName},presentClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},demandClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none")}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexShare.js.map
