"use strict";var vm=new Vue({el:"#ggIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/H5GGShare/initHomePage",data:{activityId:1},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content)}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
