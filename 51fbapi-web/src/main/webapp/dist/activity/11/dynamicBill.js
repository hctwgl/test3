"use strict";var vm=new Vue({el:"#dynamicBill",data:{content:"",contentOne:"",contentThree:"",ruleShow:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getUserOutDay",success:function success(data){self.content=eval("("+data+")"),console.log(self.content),0==self.content.success&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:location.pathname+"?type=pvuv"},success:function(e){console.log(e)}})},changeBillDay:function changeBillDay(){$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getOutDayList",success:function success(data){self.contentOne=eval("("+data+")"),window.location.href="changebillDay",1==self.contentOne.msg?window.location.href="cunpaidBill":2==self.contentOne.msg&&(window.location.href="changeTimeOver")}}),refundState(),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/barginIndex?type=changOne"},success:function(e){console.log(e)}})},ruleClick:function(){this.ruleShow="Y"},maskClick:function(){this.ruleShow=""}}});
//# sourceMappingURL=../../_srcmap/activity/11/dynamicBill.js.map
