"use strict";var activityId=getUrl("activityId"),vm=new Vue({el:"#selfHome",data:{discountMap:[],rebateMap:[],returnData:[]},created:function(){this.initial(),loading()},methods:{initial:function(){var a=this;$.ajax({url:"/fanbei-web/encoreActivityInfo",dataType:"json",data:{activityId:activityId},type:"post",success:function(e){console.debug(e),a.discountMap=e.data.recommendGoodsList.slice(0,3),a.rebateMap=e.data.recommendGoodsList.slice(3),a.returnData=e.data,a.$nextTick(function(){lazy.init(),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("请求失败")}})},goGoodsDetail:function(a){"SELFSUPPORT"==a.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+a.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+a.goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/08/selfHome.js.map
