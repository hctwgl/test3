"use strict";var activityId=getUrl("activityId"),groupId=getUrl("groupId"),vm=new Vue({el:"#nationalDay",data:{content:{},couponCont:{}},created:function(){this.logData(),this.logCoupon()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/newEncoreActivityInfo",data:{activityId:activityId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(o){"SELFSUPPORT"==o.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+o.goodsId+'"}'},logCoupon:function logCoupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.couponCont=eval("("+data+")").data,console.log(self.couponCont)},error:function(){requestMsg("哎呀，出错了！")}})},couponClick:function(o){var t=o.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:t},success:function(o){if(o.success)requestMsg("优惠劵领取成功");else{var t=o.data.status;"USER_NOT_EXIST"==t&&(window.location.href=o.url),"OVER"==t&&requestMsg("您已经领取，快去使用吧"),"COUPON_NOT_EXIST"==t&&requestMsg(o.msg),"MORE_THAN"==t&&requestMsg(o.msg)}},error:function(){requestMsg("哎呀，出错了！")}})}}});
//# sourceMappingURL=../../_srcmap/activity/09/nationalDay.js.map
