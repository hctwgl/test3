"use strict";var getShopId=function(a){switch(a){case"ele":return 22;case"yiguo":return 17;case"lumama":return 24;case"xiecheng":return 33;default:return null}};window.onload=function(){$.ajax({url:"/fanbei-web/postMaidianInfo",data:{maidianInfo:"ggentry"},type:"POST",succuess:function(a){console.log(a)}})},$(".coupon .button").click(function(){$.ajax({url:"/fanbei-web/pickBoluomeCouponV1",dataType:"JSON",data:{sceneId:"9022"},type:"POST",success:function success(data){data=eval("("+data+")"),console.log(data),data.success?(alert(data),requestMsg(data.msg),requestMsg("3333")):data.url?location.href=data.url:requestMsg(data.msg)}})});var drainage=function drainage(scase){$("."+scase+" .button").on("click",function(){$.ajax({url:"/fanbei-web/getBrandUrlV1",data:{shopId:getShopId(scase)},type:"POST",success:function success(data){data=eval("("+data+")"),data.success?location.href=data.url:requestMsg(data.msg)}}),$.ajax({url:"/fanbei-web/postMaidianInfo",data:{maidianInfo:getShopId(scase)},type:"POST",succuess:function(a){console.log(a)}})})};drainage("ele"),drainage("yiguo"),drainage("lumama"),drainage("xiecheng");
//# sourceMappingURL=../../_srcmap/js/app/ggentry.js.map
