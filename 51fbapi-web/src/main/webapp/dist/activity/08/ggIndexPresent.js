"use strict";function getData(t){for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").attr("data-slide-imgId",e);if(1==t)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img3");if(2==t)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+3));if(3==t)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+2));if(t>3&&t<6)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+1));if(t>=6)for(var e=0;e<t;e++)e<5?$(".imgList .img:eq("+e+")").addClass("img"+(e+1)):$(".imgList .img:eq("+e+")").addClass("img5");touch&&k_touch(),imgClickFy()}function right(){for(var t=new Array,e=0;e<slideNub;e++)t[e]=$(".imgList .img[data-slide-imgId="+e+"]").attr("class");for(var e=0;e<slideNub;e++)0==e?$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[slideNub-1]):$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[e-1]);imgClickFy()}function left(){for(var t=new Array,e=0;e<slideNub;e++)t[e]=$(".imgList .img[data-slide-imgId="+e+"]").attr("class");for(var e=0;e<slideNub;e++)e==slideNub-1?$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[0]):$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[e+1]);imgClickFy()}function imgClickFy(){$(".imgList .img").removeAttr("onclick"),$(".imgList .img2").attr("onclick","left()"),$(".imgList .img4").attr("onclick","right()")}function k_touch(){function t(t){var e=t.targetTouches[0];a=e.pageX}function e(t){var e=t.targetTouches[0];s=a-e.pageX}function i(t){s<-100?(left(),s=0):s>100&&(right(),s=0)}var a=0,s=0,r=document.getElementById("slide");r.addEventListener("touchstart",t,!1),r.addEventListener("touchmove",e,!1),r.addEventListener("touchend",i,!1)}function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"消费有返利 领取51元大奖！",shareAppContent:"你的好友赠送了一张"+name+"卡片给你，快领走吧~",shareAppImage:"http://f.51fanbei.com/h5/app/activity/08/ggShare.png",shareAppUrl:domainName+"/fanbei-web/activity/ggpresents?loginSource=Z&userName="+userName+"&activityId="+activityId+"&userItemsId="+cardRid,isSubmit:"Y",sharePage:"ggpresents"};return JSON.stringify(t)}var userName="";getInfo().userName&&(userName=getInfo().userName);var touch=!0,slideNub,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,cardRid,activityId=getUrl("activityId"),name,itemsListRid,itemsId,userItemsList,numClick01,numClick02;$(function(){$(".presentCard").click(function(){$.ajax({type:"get",url:"/H5GG/sendItems",data:{activityId:activityId},success:function success(returnData){if(returnData=eval("("+returnData+")"),console.log(returnData),returnData.data.loginUrl)location.href=returnData.data.loginUrl;else if("Y"==$(".presentCard").attr("present")){$("body").addClass("overflowChange"),$("html").addClass("overflowChange"),$(".imgList").empty(),$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".surePresent").show(),$(".sureDemand").hide();var presentCardList=returnData.data.itemsList;userItemsList=returnData.data.userItemsList;for(var str="",j=0;j<presentCardList.length;j++)presentCardList[j].num>=2?str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><p class="num">x'+(presentCardList[j].num-1)+"</p></div>":str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img class="cardBlur" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"></div>';$(".imgList").append(str),slideNub=$(".imgList .img").size(),getData(slideNub),$(".presentTitle span").eq(1).html($(".img.img3").attr("name")),$(".img").click(function(){var t=$(this).index();$(".presentTitle span").eq(1).html($(".img").eq(t).attr("name")),numClick01=$(this).attr("numClick"),numClick01<2?$(".surePresent").css("background","#B3B3B3"):$(".surePresent").css("background","#fb9659")})}else requestMsg("抱歉，你暂时没有可以赠送的卡片")},error:function(){requestMsg("请求失败")}})}),$(".surePresent").click(function(){var arr=[];if(name=$(".img.img3").attr("name"),itemsListRid=$(".img.img3").attr("rid"),(numClick02=$(".img.img3").attr("numClick"))>=2){for(var i=0;i<userItemsList.length;i++)(itemsId=userItemsList[i].itemsId)==itemsListRid&&arr.push(userItemsList[i].rid);cardRid=arr[0],cardRid&&""!=cardRid&&$.ajax({type:"get",url:"/H5GG/doSendItems",data:{userItemsId:cardRid},success:function success(returnData){if(returnData=eval("("+returnData+")"),returnData.success){var dat='{"shareAppTitle":"消费有返利 领取51元大奖！","shareAppContent":"你的好友赠送了一张'+name+'卡片给你，快领走吧~","shareAppImage":"http://f.51fanbei.com/h5/app/activity/08/ggShare.png","shareAppUrl":"'+domainName+"/fanbei-web/activity/ggpresents?loginSource=Z&userName="+userName+"&activityId="+activityId+"&userItemsId="+cardRid+'","isSubmit":"Y","sharePage":"ggpresents"}',base64=BASE64.encoder(dat);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+base64}},error:function(){requestMsg("请求失败")}})}}),$(".mask").click(function(){$("body").removeClass("overflowChange"),$("html").removeClass("overflowChange"),$(".surePresent").css("background","#fb9659")})});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexPresent.js.map
