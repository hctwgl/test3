"use strict";function refundState(e){var t=new mui.PopPicker;t.setData(e),t.pickers[0].setSelectedIndex(0,2e3),t.show(function(e){var n=e[0],a=n.slice(2,4),o=n.slice(66,-1);console.log(a,"theFirst"),console.log(o,"theSecond");var s="",c="";s+="<i class='monthOne startTime'>每月<i id='outDay'>"+a+"</i>",$(".monthOne").html(s),c+="<i class='monthTwo startTime'>每月<i id='payDay'>"+o+"</i>",$(".monthTwo").html(c),console.log(n),t.dispose()})}var vm=new Vue({el:"#changbillDay",data:{contentOne:"",contentTwo:"",calendar:"",ruleShow:""},created:function(){this.logData()},mounted:function(){},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getOutDayList",success:function success(data){self.contentOne=eval("("+data+")"),console.log(self.contentOne,"self.contentOne"),0==self.contentOne.success&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN")}})},commit:function commit(){var self=this,outDay=document.getElementById("outDay").innerHTML,payDay=document.getElementById("payDay").innerHTML;localStorage.setItem("outDay",outDay),localStorage.setItem("payDay",payDay),console.log(outDay,"peng2222"),$.ajax({type:"post",url:"/fanbei-web/changeOutDay/updateOutDay",data:{outDay:outDay,payDay:payDay},success:function success(data){self.contentTwo=eval("("+data+")"),console.log(self.contentTwo,"self.contentTwo"),window.location.href="changeSuccess?testUser=17839218825",0==self.contentTwo.success&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN")}})},firstWeb:function(){window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_HOME"},toPay:function(){window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_TOPAY"},toMove:function(){window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_MOVE"},maskHide:function(){$(".mask").hide()},maskShow:function(){var e=this;$(".dynamic-center").show();for(var t=e.contentOne.data.outDayList,n=[],a="",o=0;o<t.length;o++)a="每月"+t[o].outDay+"日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;每月"+t[o].payDay+"日",n.push(a);refundState(n)},ruleClick:function(){this.ruleShow="Y"},maskClick:function(){this.ruleShow=""}}});
//# sourceMappingURL=../../_srcmap/activity/11/changeBillDay.js.map
