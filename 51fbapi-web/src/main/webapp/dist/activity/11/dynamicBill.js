"use strict";var vm=new Vue({el:"#dynamicBill",data:{content:"",contentOne:"",contentThree:"",ruleShow:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getUserOutDay",success:function success(data){if(self.content=eval("("+data+")"),console.log(self.content),0==self.content.success){var creadiv=function(e,n,t){var a=document.createElement("div");a.style.position="absolute",a.style.left="111px",a.style.right="0px",a.innerText="您还未登录，请先进行登录",document.body.appendChild(a)};location.href="http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN",creadiv(),$("body").style("background-color","red")}}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:location.pathname+"?type=pvuv"},success:function(e){console.log(e)}})},changeBillDay:function changeBillDay(){$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getOutDayList",success:function success(data){self.contentOne=eval("("+data+")"),window.location.href="/fanbei-web/activity/changebillDay",1==self.contentOne.msg?window.location.href="cunpaidBill":2==self.contentOne.msg&&(window.location.href="changeTimeOver")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/barginIndex?type=changOne"},success:function(e){console.log(e)}})},ruleClick:function(){this.ruleShow="Y"},maskClick:function(){this.ruleShow=""}}});
//# sourceMappingURL=../../_srcmap/activity/11/dynamicBill.js.map
