"use strict";function hello(){var o=$(".myscroll").scrollTop();o>=$(".roll").height()?o=0:o++,$(".myscroll").scrollTop(o),setTimeout("hello()",speed)}var speed=40,cont=$(".roll").html();$(".copyRoll").html(cont),hello();var activityId=getUrl("activityId"),vm=new Vue({el:"#iphone8",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/newEncoreActivityInfo",data:{activityId:activityId},success:function success(data){data=eval("("+data+")"),console.log(data),self.content=data.data,data.success&&self.$nextTick(function(){$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(o){"SELFSUPPORT"==o.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+o.goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/08/iphone8.js.map
