"use strict";var finished=0,vm=new Vue({el:"#inviteRank",data:{returnData:[]},created:function(){this.initial()},methods:{initial:function(){var t=this;$.ajax({url:"/fanbei-web/recommendListSort",dataType:"json",type:"get",success:function(e){console.log(e),t.returnData=e.data},error:function(){requestMsg("请求失败")}})}}});
//# sourceMappingURL=../../_srcmap/js/app/inviteRank.js.map
