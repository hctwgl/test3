"use strict";var modelId=getUrl("modelId"),vm=new Vue({el:"#gamePoint",data:{content:{},isActive:!0},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo?modelId="+modelId,data:{modelId:modelId},success:function success(data){console.log(data),self.content=eval("("+data+")").data},error:function(){requestMsg("请求失败")}})},isshow:function(){this.isActive=!this.isActive},buyNow:function(t){window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/07/gamePoint.js.map
