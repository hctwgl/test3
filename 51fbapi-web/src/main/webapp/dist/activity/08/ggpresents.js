"use strict";var vm=new Vue({el:"#presents",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{userItemsId:1},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content)}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/ggpresents.js.map
