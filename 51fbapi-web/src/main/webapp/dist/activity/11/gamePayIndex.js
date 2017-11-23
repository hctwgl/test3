"use strict";var vm=new Vue({el:"#gamePayIndex",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function(){$.ajax({type:"post",url:"/game/pay/goods",data:{type:"GAME"},success:function(e){console.log(e)},error:function(){requestMsg("哎呀，出错了！")}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/gamePayIndex.js.map
