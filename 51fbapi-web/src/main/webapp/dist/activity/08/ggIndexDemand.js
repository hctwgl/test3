"use strict";function getData(i){for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").attr("data-slide-imgId",t);if(1==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img3");if(2==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+3));if(3==i)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+2));if(i>3&&i<6)for(var t=0;t<i;t++)$(".imgList .img:eq("+t+")").addClass("img"+(t+1));if(i>=6)for(var t=0;t<i;t++)t<5?$(".imgList .img:eq("+t+")").addClass("img"+(t+1)):$(".imgList .img:eq("+t+")").addClass("img5");touch&&k_touch(),imgClickFy()}function right(){for(var i=new Array,t=0;t<slideNub;t++)i[t]=$(".imgList .img[data-slide-imgId="+t+"]").attr("class");for(var t=0;t<slideNub;t++)0==t?$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[slideNub-1]):$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[t-1]);imgClickFy()}function left(){for(var i=new Array,t=0;t<slideNub;t++)i[t]=$(".imgList .img[data-slide-imgId="+t+"]").attr("class");for(var t=0;t<slideNub;t++)t==slideNub-1?$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[0]):$(".imgList .img[data-slide-imgId="+t+"]").attr("class",i[t+1]);imgClickFy()}function imgClickFy(){$(".imgList .img").removeAttr("onclick"),$(".imgList .img2").attr("onclick","left()"),$(".imgList .img4").attr("onclick","right()")}function k_touch(){function i(i){var t=i.targetTouches[0];e=t.pageX}function t(i){var t=i.targetTouches[0];r=e-t.pageX}function a(i){r<-100?(left(),r=0):r>100&&(right(),r=0)}var e=0,r=0,s=document.getElementById("slide");s.addEventListener("touchstart",i,!1),s.addEventListener("touchmove",t,!1),s.addEventListener("touchend",a,!1)}function alaShareData(){var i={appLogin:"Y",type:"share",shareAppTitle:"消费有返利 领取88.88元现金红包！",shareAppContent:"你的好友向你索要一张卡片，快赠送给他/她吧~",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:domainName+"/activity/ggIndexShare?loginSource=S&&cardRid"+cardRid,isSubmit:"Y",sharePage:"ggIndexShare"};return JSON.stringify(i)}var touch=!0,slideNub,protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,cardRid,activityId=getUrl("activityId"),userName=getCookie("userName");$(function(){$(".demandCard").click(function(){alert(0),$.ajax({type:"get",url:"/H5GG/askForItems",data:{activityId:activityId,userName:userName},success:function success(returnData){var returnData=eval("("+returnData+")").data;if(console.log(returnData),returnData.loginUrl)location.href=returnData.loginUrl;else{$(".imgList").empty();for(var presentCardList=returnData.itemsList,str="",j=0;j<presentCardList.length;j++)$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".demandTitle").css("display","block"),$(".sure").html("确定索要"),str+='<div class="img" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"></div>'}$(".imgList").append(str),slideNub=$(".imgList .img").size(),getData(slideNub),$(".sure").click(function(){cardRid=$(".img.img3").attr("rid"),console.log(cardRid),window.location.href=cardRid&&""!=cardRid?'/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"消费有返利 领取88.88元现金红包！","shareAppContent":"你的好友向你索要一张卡片，快赠送给他/她吧~","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"'+domainName+"/activity/ggIndexShare?loginSource=S&&cardRid"+cardRid+'","isSubmit":"Y","sharePage":"ggIndexShare"}':"ggIndex"})},error:function(){requestMsg("请求失败")}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexDemand.js.map
