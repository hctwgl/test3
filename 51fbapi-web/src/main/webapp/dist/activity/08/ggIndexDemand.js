"use strict";function getData(i){for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").attr("data-slide-imgId",t);if(1==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img3");if(2==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+3));if(3==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+2));if(i>3&&i<6)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+1));if(i>=6)for(var t=0;t<i;t++)t<5?$(".imgList .img:eq("+t+")").addClass("img"+(t+1)):$(".imgList .img:eq("+t+")").addClass("img5");touch&&k_touch(),imgClickFy()}function right(){for(var i=new Array,t=0;t<slideNub;t++)i[t]=$(".imgList .img[data-slide-imgId="+t+"]").attr("class");for(var t=0;t<slideNub;t++)0==t?$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[slideNub-1]):$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[t-1]);imgClickFy()}function left(){for(var i=new Array,t=0;t<slideNub;t++)i[t]=$(".imgList .img[data-slide-imgId="+t+"]").attr("class");for(var t=0;t<slideNub;t++)t==slideNub-1?$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[0]):$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[t+1]);imgClickFy()}function imgClickFy(){$(".imgList .img").removeAttr("onclick"),$(".imgList .img2").attr("onclick","left()"),$(".imgList .img4").attr("onclick","right()")}function k_touch(){function i(i){var t=i.targetTouches[0];e=t.pageX}function t(i){var t=i.targetTouches[0];s=e-t.pageX}function a(i){s<-100?s=0:s>100&&(s=0)}var e=0,s=0,r=document.getElementById("slide");r.addEventListener("touchstart",i,!1),r.addEventListener("touchmove",t,!1),r.addEventListener("touchend",a,!1)}function alaShareData(){var i={appLogin:"Y",type:"share",shareAppTitle:"消费有返利 领取51元大奖！",shareAppContent:"你的好友向你索要一张"+name+"卡片，快赠送给他/她吧~",shareAppImage:"http://f.51fanbei.com/h5/app/activity/08/ggShare.png",shareAppUrl:domainName+"/fanbei-web/activity/ggdemand?loginSource=S&activityId="+activityId+"&userName="+userName+"&itemsId="+itemsId,isSubmit:"Y",sharePage:"ggdemand"};return JSON.stringify(i)}var touch=!0,slideNub,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,itemsId,activityId=getUrl("activityId"),userName="";getInfo().userName&&(userName=getInfo().userName);var name;$(function(){$(".demandCard").click(function(){$.ajax({type:"get",url:"/H5GG/askForItems",data:{activityId:activityId},success:function success(returnData){var returnData=eval("("+returnData+")").data;if(returnData.loginUrl)location.href=returnData.loginUrl;else{$("body").addClass("overflowChange"),$("html").addClass("overflowChange"),$(".imgList").empty(),$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".demandTitle").css("display","block"),$(".sureDemand").show(),$(".surePresent").hide();for(var presentCardList=returnData.itemsList,str="",j=0;j<presentCardList.length;j++)str+='<div class="img" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"></div>';$(".imgList").append(str),slideNub=$(".imgList .img").size(),getData(slideNub)}},error:function(){requestMsg("请求失败")}})}),$(".sureDemand").click(function(){if(itemsId=$(".img.img3").attr("rid"),name=$(".img.img3").attr("name"),itemsId&&""!=itemsId){var i='{"shareAppTitle":"消费有返利 领取51元大奖！","shareAppContent":"你的好友向你索要一张'+name+'卡片，快赠送给他/她吧~","shareAppImage":"http://f.51fanbei.com/h5/app/activity/08/ggShare.png","shareAppUrl":"'+domainName+"/fanbei-web/activity/ggdemand?loginSource=S&activityId="+activityId+"&userName="+userName+"&itemsId="+itemsId+'","isSubmit":"Y","sharePage":"ggdemand"}',t=BASE64.encoder(i);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+t}else requestMsg("索要失败")}),$(".mask").click(function(){$("body").removeClass("overflowChange"),$("html").removeClass("overflowChange")})});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexDemand.js.map
