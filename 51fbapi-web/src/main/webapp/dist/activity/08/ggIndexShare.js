"use strict";function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}var activityId=getUrl("activityId"),userName=getCookie("userName"),currentUrl=window.location.href,index=currentUrl.lastIndexOf("/"),urlName=currentUrl.slice(index+1);console.log(userName);var num,vm=new Vue({el:"#ggIndexShare",data:{content:{},finalPrizeMask:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:activityId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){var e=$(".cont1").html();$(".cont2").html(e),wordMove()}),self.$nextTick(function(){function e(){n==s&&($(".banner .bannerList").css({left:0}),n=1),-1==n&&($(".banner .bannerList").css({left:6.25*-(s-1)+"rem"}),n=s-2),$(".banner .bannerList").stop().animate({left:6.25*-n+"rem"},500),n==s-1?$(".banner .num li").eq(0).addClass("on").siblings().removeClass("on"):$(".banner .num li").eq(n).addClass("on").siblings().removeClass("on")}var n=0,a=$(".banner .bannerList li").first().clone();$(".banner .bannerList").append(a);var s=self.content.bannerList.length+1,t=6.25*s+"rem";$(".banner .bannerList li").width("6.25rem"),$(".banner .bannerList").width(t);for(var o=0;o<s-1;o++)$(".banner .num").append("<li></li>");$(".banner .num li").first().addClass("on"),setInterval(function(){n++,e()},1500)});for(var couponList=self.content.boluomeCouponList,i=0;i<couponList.length;i++)couponList[i]=eval("("+couponList[i]+")");for(var j=0;j<self.content.itemsList.length;j++)num=self.content.itemsList[j].num,self.finalPrizeMask=0==num}})},bannerClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},couponClick:function(e){if(userName=getCookie("userName")){var n=e.sceneId;$.ajax({url:"/H5GGShare/pickBoluomeCouponWeb",type:"POST",dataType:"JSON",data:{sceneId:n},success:function(e){e.success,requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})}else window.location.href="gglogin?urlName="+urlName},cardClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},finalPrize:function(){var e=this;userName=getCookie("userName"),""!=userName&&userName?e.finalPrizeMask||$.ajax({type:"get",url:"/H5GGShare/pickUpSuperPrize",data:{activityId:activityId},dataType:"JSON",success:function(e){console.log(e),e.success,requestMsg(e.msg)},error:function(){requestMsg("请求失败")}}):window.location.href="gglogin?urlName="+urlName},presentClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},demandClick:function(){userName=getCookie("userName"),window.location.href=""!=userName&&userName?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":"gglogin?urlName="+urlName},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none")}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexShare.js.map
