"use strict";var getShopId=function(a){switch(a){case"ele":return 10;case"yiguo":return 2;case"lumama":return 4;case"xiecheng":return 16;default:return null}},sceneId=null;window.onload=function(){$.ajax({url:"/fanbei-web/postMaidianInfo",data:{maidianInfo:"ggentry"},type:"POST",succuess:function(a){console.log(a)}}),$.ajax({url:"/H5GG/showCoupon",type:"GET",success:function success(data){data=eval("("+data+")"),sceneId=data.data.boluomeCouponList[0].sceneId,"Y"==data.data.boluomeCouponList[0].isHas?$(".coupon .button").text("立即使用").addClass("ishas"):$(".coupon .button").text("立即领取").removeClass("ishas"),$(".coupon .button.unpending").click(function(){$(this).hasClass("unpending")&&!$(this).hasClass("ishas")?($(".coupon .button.unpending").removeClass("unpending"),$.ajax({url:"/fanbei-web/pickBoluomeCouponV1",data:{sceneId:sceneId},type:"POST",success:function success(data){$(".coupon .button").addClass("unpending"),data=eval("("+data+")"),data.success?(requestMsg(data.msg),$.ajax({url:"/H5GG/showCoupon",type:"GET",success:function success(data){data=eval("("+data+")"),sceneId=data.data.boluomeCouponList[0].sceneId,"Y"==data.data.boluomeCouponList[0].isHas?$(".coupon .button").text("立即使用").addClass("ishas"):$(".coupon .button").text("立即领取").removeClass("ishas")}})):data.url?location.href=data.url:requestMsg(data.msg)},error:function(a){console.log(a),$(".coupon .button").addClass("unpending")}})):$(this).hasClass("ishas")&&(location.href="/fanbei-web/opennative?name=JUMP_BOLUOMI_PAGE")})}})};var drainage=function drainage(scase){$("."+scase+" .button").on("click",function(){$.ajax({url:"/fanbei-web/getBrandUrlV1",data:{shopId:getShopId(scase)},type:"POST",success:function success(data){data=eval("("+data+")"),data.success,location.href=data.url}}),$.ajax({url:"/fanbei-web/postMaidianInfo",data:{maidianInfo:"ggentry"+getShopId(scase)},type:"POST",succuess:function(a){console.log(a)}})})};drainage("ele"),drainage("yiguo"),drainage("lumama"),drainage("xiecheng");
//# sourceMappingURL=../../_srcmap/js/app/ggentry.js.map
