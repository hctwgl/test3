"use strict";var vm=new Vue({el:"#ggIndex",data:{content:{}},created:function(){this.logData()},methods:{logData:function(){$.ajax({type:"get",url:"/H5GGShare/initHomePage",data:{activityId:1},success:function(s){console.log(s)}})},presentClick:function presentClick(){$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".presentTitle").css("display","block"),$(".sure").html("确定赠送");var self=this;$.ajax({type:"get",url:"/H5GGShare/sendItems",data:{activityId:1},success:function success(data){self.content=eval("("+data+")").data,console.log(data)}})},demandClick:function demandClick(){$(".alertPresent").css("display","block"),$(".mask").css("display","block"),$(".demandTitle").css("display","block"),$(".sure").html("确定索要");var self=this;$.ajax({type:"get",url:"/H5GGShare/sendItems",data:{itemsId:itemsId,userId:userId},success:function success(data){self.content=eval("("+data+")").data,console.log(data)}})},ruleClick:function(){$(".alertRule").css("display","block"),$(".mask").css("display","block")},close:function(){$(".alertPresent").css("display","none"),$(".mask").css("display","none"),$(".title").css("display","none"),$(".alertRule").css("display","none"),$(".mask").css("display","none")}}});
//# sourceMappingURL=../../_srcmap/activity/08/ggIndex.js.map
