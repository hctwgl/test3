"use strict";var type=getUrl("type"),vm=new Vue({el:"#recreationPayIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function(){var t=this;$.ajax({type:"post",url:"/game/pay/goods",data:{type:type},success:function(e){t.content=e.data,console.log(t.content)},error:function(){requestMsg("哎呀，出错了！")}})},fixNum:function(t){return t.toFixed(1)}}});
//# sourceMappingURL=../../_srcmap/activity/11/recreationPayIndex.js.map
