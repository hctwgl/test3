"use strict";var modelId=getUrl("modelId"),groupId=getUrl("groupId"),protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#watches",data:{content:"",renderdata:[],couponCont:{},list:{}},created:function(){this.logData(),this.logCoupon()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo",data:{modelId:modelId},success:function success(data){data=eval("("+data+")"),self.content=data.data;var m=self.content.qualityGoodsList,c=JSON.stringify(m);m=JSON.parse(c),self.renderdata=m,self.list=self.content.qualityGoodsList,self.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}})},logCoupon:function logCoupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.couponCont=eval("("+data+")").data.couponInfoList,console.log(self.couponCont),console.log(self.couponCont.couponId)},error:function(){requestMsg("哎呀，出错了！")}})},couponClick:function(){var o=this,t=o.couponCont.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:t},success:function(o){if(o.success)requestMsg("优惠劵领取成功");else{var t=o.data.status;"USER_NOT_EXIST"==t&&(window.location.href=o.url),"OVER"==t&&requestMsg("您已经领取，快去使用吧"),"COUPON_NOT_EXIST"==t&&requestMsg(o.msg),"MORE_THAN"==t&&requestMsg(o.msg)}},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(o){"SELFSUPPORT"==o.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+o.goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/10/watches.js.map
