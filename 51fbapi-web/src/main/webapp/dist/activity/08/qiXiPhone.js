"use strict";var activityId=getUrl("activityId"),vm=new Vue({el:"#selfHome",data:{discountMap:[],rebateMap:[],returnData:[]},created:function(){this.initial()},methods:{initial:function(){var e=this;$.ajax({url:"/fanbei-web/encoreActivityInfo",dataType:"json",data:{activityId:activityId},type:"post",success:function(a){console.log(a),e.discountMap=a.data.recommendGoodsList.slice(0,3),e.rebateMap=a.data.recommendGoodsList.slice(3),e.returnData=a.data,e.$nextTick(function(){$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("请求失败")}})},goGoodsDetail:function(e){"SELFSUPPORT"==e.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+e.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+e.goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/08/qiXiPhone.js.map
