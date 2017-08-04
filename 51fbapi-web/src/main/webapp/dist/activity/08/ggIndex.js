"use strict";var vm=new Vue({el:"#ggIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function(){$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:1},success:function(t){console.log(t)}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
