"use strict";var vm=new Vue({el:"#rankingList",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/listBank",dataType:JSON,data:{activityId:1},success:function success(data){console.log(111111),self.content=eval("("+data+")").data,console.log(self.content)}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/ggrankingList.js.map
