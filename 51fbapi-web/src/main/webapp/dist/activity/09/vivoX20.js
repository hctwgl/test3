"use strict";var modelId=getUrl("modelId"),vm=new Vue({el:"#vivoX20",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo",data:{modelId:modelId},success:function success(data){self.content=eval("("+data+")").data.activityList.slice(0,3),console.log(eval("("+data+")").data),self.content.firstList=self.content[0].activityGoodsList,self.content.secondList=self.content[1].activityGoodsList,self.content.thirdList=self.content[2].activityGoodsList,console.log(self.content),self.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(t){"SELFSUPPORT"==t.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t.goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/09/vivoX20.js.map
