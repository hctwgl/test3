"use strict";function wordMove(){var i=$(".personAmount").scrollLeft();i>=$(".cont1").width()?i=0:i++,$(".personAmount").scrollLeft(i),setTimeout("wordMove()",speed)}function move(){i==size&&($(".banner .bannerList").css({left:0}),i=1),-1==i&&($(".banner .bannerList").css({left:6.25*-(size-1)+"rem"}),i=size-2),$(".banner .bannerList").stop().animate({left:6.25*-i+"rem"},500),i==size-1?$(".banner .num li").eq(0).addClass("on").siblings().removeClass("on"):$(".banner .num li").eq(i).addClass("on").siblings().removeClass("on")}function getData(i){for(var e=0;e<i;e++)$(".imgList .img:eq("+e+")").attr("data-slide-imgId",e);if(1==i)for(var e=0;e<i;e++)$(".imgList .img:eq("+e+")").addClass("img3");if(2==i)for(var e=0;e<i;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+3));if(3==i)for(var e=0;e<i;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+2));if(i>3&&i<6)for(var e=0;e<i;e++)$(".imgList .img:eq("+e+")").addClass("img"+(e+1));if(i>=6)for(var e=0;e<i;e++)e<5?$(".imgList .img:eq("+e+")").addClass("img"+(e+1)):$(".imgList .img:eq("+e+")").addClass("img5");touch&&k_touch(),imgClickFy()}function right(){for(var i=new Array,e=0;e<slideNub;e++)i[e]=$(".imgList .img[data-slide-imgId="+e+"]").attr("class");for(var e=0;e<slideNub;e++)0==e?$(".imgList .img[data-slide-imgId="+e+"]").attr("class",i[slideNub-1]):$(".imgList .img[data-slide-imgId="+e+"]").attr("class",i[e-1]);imgClickFy()}function left(){for(var i=new Array,e=0;e<slideNub;e++)i[e]=$(".imgList .img[data-slide-imgId="+e+"]").attr("class");for(var e=0;e<slideNub;e++)e==slideNub-1?$(".imgList .img[data-slide-imgId="+e+"]").attr("class",i[0]):$(".imgList .img[data-slide-imgId="+e+"]").attr("class",i[e+1]);imgClickFy()}function imgClickFy(){$(".imgList .img").removeAttr("onclick"),$(".imgList .img2").attr("onclick","left()"),$(".imgList .img4").attr("onclick","right()")}function k_touch(){function i(i){var e=i.targetTouches[0];t=e.pageX}function e(i){var e=i.targetTouches[0];s=t-e.pageX}function a(i){s<-100?(left(),s=0):s>100&&(right(),s=0)}var t=0,s=0,r=document.getElementById("slide");r.addEventListener("touchstart",i,!1),r.addEventListener("touchmove",e,!1),r.addEventListener("touchend",a,!1)}function alaShareData(){var i={appLogin:"Y",type:"share",shareAppTitle:"消费有返利 领取88.88元现金红包！",shareAppContent:"你的好友赠送了一张饿了么至尊卡给你，快领走吧~",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:domainName+"/activity/ggIndexShare?cardRid="+cardRid,isSubmit:"Y",sharePage:"ggIndexShare"};return JSON.stringify(i)}var speed=20,cont=$(".cont1").html();$(".cont2").html(cont);var i=0,liWidth="6.25rem",clone=$(".banner .bannerList li").first().clone();$(".banner .bannerList").append(clone);var size=$(".banner .bannerList li").length,ulWidth=6.25*size+"rem";$(".banner .bannerList li").width(liWidth),$(".banner .bannerList").width(ulWidth);for(var j=0;j<size-1;j++)$(".banner .num").append("<li></li>");$(".banner .num li").first().addClass("on"),setInterval(function(){i++,move()},1500);var touch=!0,slideNub,domainName=domainName(),cardRid;$(function(){$(".presentCard").click(function(){$.ajax({type:"get",url:"/H5GG/sendItems",data:{activityId:1,userName:15839790051},success:function(i){if(console.log(i),i.data.loginUrl)location.href=i.data.loginUrl;else{for(var e,a,t=i.data.itemsList,s=i.data.userItemsList,r=0,n="",d=0;d<t.length;d++)r+=t[d].num,0==r||r==t.length?requestMsg(i.msg):($(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".sure").html("确定赠送"),t[d].num>=2?n+='<div class="img" rid="'+t[d].rid+'"><img src="'+t[d].iconUrl+'"><p class="num">'+(t[d].num-1)+"</p></div>":n+='<div class="img" rid="'+t[d].rid+'"><img src="'+t[d].iconUrl+'"><p class="cardMask"></p></div>');$(".imgList").append(n),slideNub=$(".imgList .img").size(),getData(slideNub),$(".sure").click(function(){e=$(".img.img3").attr("rid");for(var i=0;i<s.length;i++)if((a=s[i].itemsId)==e){var t=[];t.push(s[i].rid)}cardRid=t[0],console.log(cardRid),window.location.href=cardRid&&""!=cardRid?'/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"消费有返利 领取88.88元现金红包！","shareAppContent":"你的好友赠送了一张饿了么至尊卡给你，快领走吧~","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"'+domainName+"/activity/ggIndexShare?cardRid"+cardRid+'","isSubmit":"Y","sharePage":"ggIndexShare"}':"ggIndex"})}},error:function(){requestMsg("请求失败")}})}),$(".demandCard").click(function(){alert(1),$.ajax({type:"get",url:"/H5GG/askForItems",data:{activityId:1,userName:15839790051},success:function success(returnData){var returnData=eval("("+returnData+")").data;if(console.log(returnData),returnData.loginUrl)location.href=returnData.loginUrl;else for(var presentCardList=returnData.itemsList,str="",j=0;j<presentCardList.length;j++)$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".sure").html("确定赠送"),str+='<div class="img"><img src="'+presentCardList[j].iconUrl+'"></div>';$(".imgList").append(str),slideNub=$(".imgList .img").size(),getData(slideNub)},error:function(){requestMsg("请求失败")}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndexAnimate.js.map
