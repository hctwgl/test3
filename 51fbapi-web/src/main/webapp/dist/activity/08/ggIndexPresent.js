"use strict";function getData(i){for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").attr("data-slide-imgId",t);if(1==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img3");if(2==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+3));if(3==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+2));if(i>3&&i<6)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+1));if(i>=6)for(var t=0;t<i;t++)t<5?$(".imgList .img:eq("+t+")").addClass("img"+(t+1)):$(".imgList .img:eq("+t+")").addClass("img5");touch&&k_touch(),imgClickFy()}function right(){alert(0);for(var i=new Array,t=0;t<slideNub;t++)i[t]=$(".imgList .img[data-slide-imgId="+t+"]").attr("class");for(var t=0;t<slideNub;t++)0==t?$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[slideNub-1]):$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[t-1]);imgClickFy()}function left(){alert(0);for(var i=new Array,t=0;t<slideNub;t++)i[t]=$(".imgList .img[data-slide-imgId="+t+"]").attr("class");for(var t=0;t<slideNub;t++)t==slideNub-1?$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[0]):$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[t+1]);imgClickFy()}function imgClickFy(){alert(0),$(".imgList .img").removeAttr("onclick"),$(".imgList .img2").attr("onclick","left()"),$(".imgList .img4").attr("onclick","right()")}function k_touch(){function i(i){var t=i.targetTouches[0];s=t.pageX}function t(i){var t=i.targetTouches[0];a=s-t.pageX}function e(){a<-100?(left(),a=0):a>100&&(right(),a=0)}var s=0,a=0,r=document.getElementById("slide");r.addEventListener("touchstart",i,!1),r.addEventListener("touchmove",t,!1),r.addEventListener("touchend",e,!1)}var userName="";getInfo().userName&&(userName=getInfo().userName);var touch=!0,slideNub,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,cardRid,activityId=getUrl("activityId"),name,itemsListRid,itemsId,userItemsList,numClick01,numClick02;$(function(){$(".presentCard").click(function(){$.ajax({type:"get",url:"/H5GG/sendItems",data:{activityId:activityId},success:function success(returnData){if(returnData=eval("("+returnData+")"),console.log(returnData),returnData.data.loginUrl)location.href=returnData.data.loginUrl;else if("Y"==$(".presentCard").attr("present")){$("body").addClass("overflowChange"),$("html").addClass("overflowChange"),$(".imgList").empty(),$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".surePresent").show(),$(".sureDemand").hide();var presentCardList=returnData.data.itemsList;userItemsList=returnData.data.userItemsList;for(var str="",j=0;j<presentCardList.length;j++)presentCardList[j].num>=2?str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><img class="garyCard" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><img class="cardBlur" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><p class="num">x'+(presentCardList[j].num-1)+"</p></div>":str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><img class="garyCard" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><img class="cardBlur" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"></div>';$(".imgList").append(str),slideNub=$(".imgList .img").size(),getData(slideNub),numClick02=$(".img.img3").attr("numClick"),numClick02>=2&&$(".img.img3").find(".garyCard").css("display","none"),$(".presentTitle span").eq(1).html($(".img.img3").attr("name")),$(".img").click(function(){var i=$(this).index();$(".presentTitle span").eq(1).html($(".img").eq(i).attr("name")),numClick01=$(this).attr("numClick"),numClick01<2?$(".surePresent").css("background","#B3B3B3"):($(".surePresent").css("background","#fb9659"),$(this).find(".garyCard").css("display","none"))})}else requestMsg("抱歉，你暂时没有可以赠送的卡片")},error:function(){requestMsg("请求失败")}})}),$(".surePresent").click(function(){var i=[];if(name=$(".img.img3").attr("name"),itemsListRid=$(".img.img3").attr("rid"),(numClick02=$(".img.img3").attr("numClick"))<2)$(this).css("background","#B3B3B3");else{for(var t=0;t<userItemsList.length;t++)(itemsId=userItemsList[t].itemsId)==itemsListRid&&i.push(userItemsList[t].rid);cardRid=i[0];var e='{"shareAppTitle":"全民集卡片 领取51元大奖","shareAppContent":"你的好友赠送了一张'+name+'卡给你，助你赢得51元大奖，速来领走吧~","shareAppImage":"http://f.51fanbei.com/h5/app/activity/08/gg31.png","shareAppUrl":"'+domainName+"/fanbei-web/activity/ggpresents?loginSource=Z&userName="+userName+"&activityId="+activityId+"&userItemsId="+cardRid+'","isSubmit":"Y","sharePage":"ggpresents"}',s=BASE64.encoder(e);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+s}}),$(".mask").click(function(){$("body").removeClass("overflowChange"),$("html").removeClass("overflowChange"),$(".surePresent").css("background","#fb9659")})});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexPresent.js.map
