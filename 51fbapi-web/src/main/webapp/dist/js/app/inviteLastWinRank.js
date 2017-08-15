"use strict";var finished=0,vm=new Vue({el:"#inviteLastWinRank",data:{returnData:[]},created:function(){this.initial()},methods:{initial:function(){var t=this;$.ajax({url:"/fanbei-web/getPrizeByLastMonth",dataType:"json",type:"post",success:function(e){console.log(e),t.returnData=e},error:function(){requestMsg("请求失败")}})}}});
//# sourceMappingURL=../../_srcmap/js/app/inviteLastWinRank.js.map
