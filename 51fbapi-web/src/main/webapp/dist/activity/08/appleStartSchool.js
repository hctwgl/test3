"use strict";var activityId=getUrl("activityId"),groupId=getUrl("groupId");new Vue({el:"#appleStartSchool",data:{content:[],couponCont:[]},created:function(){this.loginData(),this.logCoupon()},methods:{loginData:function loginData(){var self=this;$.ajax({url:"/fanbei-web/newEncoreActivityInfo",type:"post",data:{activityId:activityId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("请求失败")}})},logCoupon:function logCoupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.couponCont=eval("("+data+")").data,console.log(self.couponCont)},error:function(){requestMsg("请求失败")}})},goodClick:function(o){"SELFSUPPORT"==o.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+o.goodsId+'"}'},couponClick:function(o){var e=o.couponId;console.log(e),$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:e},success:function(e){if(console.log(e),e.success)requestMsg("优惠劵领取成功"),o.drawStatus="Y";else{var t=e.data.status;"USER_NOT_EXIST"==t&&(window.location.href=e.url),"OVER"==t&&(requestMsg(e.msg),requestMsg("优惠券个数超过最大领券个数")),"COUPON_NOT_EXIST"==t&&requestMsg(e.msg),"MORE_THAN"==t&&requestMsg(e.msg)}},error:function(){requestMsg("请求失败")}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/appleStartSchool.js.map
