"use strict";function wordMove(){var t=$(".personAmount").scrollLeft();t>=$(".cont1").width()?t=0:t++,$(".personAmount").scrollLeft(t),setTimeout("wordMove()",speed)}var _taq=[];_taq.push({convert_id:"72607366923",event_type:"form"});var activityId=getUrl("activityId"),userName="",currentUrl=window.location.href,index=currentUrl.lastIndexOf("/"),urlName=currentUrl.slice(index+1);urlName=urlName.replace(/\?/g,"&");var num,typeFrom=getUrl("typeFrom"),typeFromNum=getUrl("typeFromNum");$(function(){"Jrtt"==typeFrom||"Jmtt"==typeFrom?$(".companyWord02").show():$(".companyWord01").show()});var vm=new Vue({el:"#ggNewAdjr",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:activityId},success:function success(data){$(".positionImg").fadeOut(4e3),self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){for(var t=0;t<self.content.boluomeCouponList.length;t++)"Y"==self.content.boluomeCouponList[t].isHas&&$(".coupon").eq(t).addClass("changeGray")}),self.$nextTick(function(){var t=$(".cont1").html();$(".cont2").html(t),wordMove()}),self.$nextTick(function(){for(var t=0;t<self.content.itemsList.length;t++)(num=self.content.itemsList[t].num)>=2&&$(".card").eq(t).find(".num").css("display","block")})}})},joinAmountClick:function(){window.location.href="ggrankingList?activityId="+activityId},couponClick:function(t){$("#mobile").focus()},cardClick:function(){$(window).scrollTop(0),$("#mobile").focus()},finalPrize:function(){$(window).scrollTop(0),$("#mobile").focus()},presentClick:function(){$(window).scrollTop(0),$("#mobile").focus()},demandClick:function(){$(window).scrollTop(0),$("#mobile").focus()},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none"),$(".alertFinalPrize").css("display","none")},fixImgUrl:function(t){return"http://f.51fanbei.com/h5/app/activity/10/ggNewCard0"+t+".png"},getNowClick:function(){}}}),speed=20;
//# sourceMappingURL=../../_srcmap/activity/10/ggNewAdjr.js.map
