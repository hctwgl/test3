"use strict";function getData(t){for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").attr("data-slide-imgId",e);if(1==t)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img3");if(2==t)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+3));if(3==t)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+2));if(t>3&&t<6)for(var e=0;e<t;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+1));if(t>=6)for(var e=0;e<t;e++)e<5?$(".imgList .img:eq("+e+")").addClass("img"+(e+1)):$(".imgList .img:eq("+e+")").addClass("img5");hasTouchInit||k_touch(),imgClickFy()}function right(){for(var t=new Array,e=0;e<slideNub;e++)t[e]=$(".imgList .img[data-slide-imgId="+e+"]").attr("class");for(var e=0;e<slideNub;e++)0==e?$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[slideNub-1]):$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[e-1]);imgClickFy(),numClick03=$(".img.img3").attr("numClick"),$(".presentTitle span").eq(1).html($(".img.img3").attr("name")),numClick03<2?$(".surePresent").css("background","#B3B3B3"):$(".surePresent").css("background","#fb9659")}function left(){for(var t=new Array,e=0;e<slideNub;e++)t[e]=$(".imgList .img[data-slide-imgId="+e+"]").attr("class");for(var e=0;e<slideNub;e++)e==slideNub-1?$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[0]):$(".imgList .img[data-slide-imgId="+e+"]").attr("class",t[e+1]);imgClickFy(),numClick03=$(".img.img3").attr("numClick"),$(".presentTitle span").eq(1).html($(".img.img3").attr("name")),numClick03<2?$(".surePresent").css("background","#B3B3B3"):$(".surePresent").css("background","#fb9659")}function imgClickFy(){$(".imgList .img").removeAttr("onclick"),$(".imgList .img2").attr("onclick","left()"),$(".imgList .img4").attr("onclick","right()")}function k_touch(){function t(t){var e=t.targetTouches[0];s=e.pageX}function e(t){var e=t.targetTouches[0];a=s-e.pageX}function i(){a<-50?(left(),a=0):a>50&&(right(),a=0)}var s=0,a=0,r=document.getElementById("slide");r.addEventListener("touchstart",t,!1),r.addEventListener("touchmove",e,!1),r.addEventListener("touchend",i,!1),hasTouchInit=!0}var userName="";getInfo().userName&&(userName=getInfo().userName);var slideNub,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,cardRid,activityId=getUrl("activityId"),name,itemsListRid,itemsId,userItemsList,numClick01,numClick02,numClick03,hasTouchInit=!1;$(function(){$(".presentCard").click(function(){$.ajax({type:"get",url:"/H5GG/sendItems",data:{activityId:activityId},success:function success(returnData){if(returnData=eval("("+returnData+")"),console.log(returnData),returnData.data.loginUrl)location.href=returnData.data.loginUrl;else if("Y"==$(".presentCard").attr("present")){$("body").addClass("overflowChange"),$("html").addClass("overflowChange"),$(".imgList").empty(),$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".surePresent").show(),$(".sureDemand").hide();var presentCardList=returnData.data.itemsList;userItemsList=returnData.data.userItemsList;for(var str="",j=0;j<presentCardList.length;j++)presentCardList[j].num>=2?str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><img class="cardBlur" src="'+presentCardList[j].iconUrl+'"><p class="num">x'+(presentCardList[j].num-1)+"</p></div>":str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img class="garyCard" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><img class="cardBlur" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"></div>';$(".imgList").append(str),slideNub=$(".imgList .img").size(),getData(slideNub),$(".presentTitle span").eq(1).html($(".img.img3").attr("name")),$(".img").click(function(){var t=$(this).index();$(".presentTitle span").eq(1).html($(".img").eq(t).attr("name")),numClick01=$(this).attr("numClick"),numClick01<2?$(".surePresent").css("background","#B3B3B3"):$(".surePresent").css("background","#fb9659")})}else requestMsg("抱歉，你暂时没有可以赠送的卡片")},error:function(){requestMsg("请求失败")}})}),$(".surePresent").click(function(){var t=[];if(name=$(".img.img3").attr("name"),itemsListRid=$(".img.img3").attr("rid"),(numClick02=$(".img.img3").attr("numClick"))<2)$(this).css("background","#B3B3B3");else{for(var e=0;e<userItemsList.length;e++)(itemsId=userItemsList[e].itemsId)==itemsListRid&&t.push(userItemsList[e].rid);cardRid=t[0];var i='{"shareAppTitle":"全民集卡片 领取51元大奖","shareAppContent":"你的好友赠送了一张'+name+'卡给你，助你赢得51元大奖，速来领走吧~","shareAppImage":"http://f.51fanbei.com/h5/app/activity/08/gg31.png","shareAppUrl":"'+domainName+"/fanbei-web/activity/ggpresents?loginSource=Z&userName="+userName+"&activityId="+activityId+"&userItemsId="+cardRid+"&sharePage=ggpresents_userItemsId_"+cardRid+'","shareCodeUrl":&quot;'+domainName+"/H5GGShare/submitShareCode?userItemsId="+cardRid+'&shareAppUrl="/fanbei-web/activity/ggpresents?loginSource=Z&amp;userName='+userName+"&amp;activityId="+activityId+"&amp;userItemsId="+cardRid+'&quot;,"isSubmit":"Y","sharePage":"ggpresents_userItemsId_'+cardRid+'"}',s=BASE64.encoder(i);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+s}}),$(".mask").click(function(){$("body").removeClass("overflowChange"),$("html").removeClass("overflowChange"),$(".surePresent").css("background","#fb9659")})});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexPresent.js.map
