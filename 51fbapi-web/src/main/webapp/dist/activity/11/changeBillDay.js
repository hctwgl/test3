"use strict";function refundState(t){var e=new mui.PopPicker;e.setData(t),e.pickers[0].setSelectedIndex(0,2e3),e.show(function(t){var n=t[0],a=n.split("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"),o=a[0].slice(2,-1),s=a[1].slice(2,-1);console.log(o,s);var i="",c="";i+="<i class='monthOne startTime'>每月<i id='outDay'>"+o+"</i></i>号</i>",$(".monthOne").html(i),c+="<i class='monthTwo startTime'>每月<i id='payDay'>"+s+"</i></i>号</i>",$(".monthTwo").html(c),e.dispose()})}var vm=new Vue({el:"#changbillDay",data:{contentOne:"",contentTwo:"",calendar:"",ruleShow:"",otherDate:"",over:!1,msg:""},created:function(){this.logData()},mounted:function(){},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getOutDayList",success:function success(data){self.contentOne=eval("("+data+")"),self.otherDate=self.contentOne.data.outDayList.slice(0,1)[0],console.log(self.otherDate," self.otherDate"),0==self.contentOne.success&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN")}})},commit:function commit(){var self=this,outDay=document.getElementById("outDay").innerHTML,payDay=document.getElementById("payDay").innerHTML;localStorage.setItem("outDay",outDay),localStorage.setItem("payDay",payDay),console.log(outDay,"peng2222"),$.ajax({type:"post",url:"/fanbei-web/changeOutDay/updateOutDay",data:{outDay:outDay,payDay:payDay},success:function success(data){self.contentTwo=eval("("+data+")"),console.log(self.contentTwo,"self.contentTwo"),self.contentTwo.success}})},firstWeb:function(){window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_HOME"},toPay:function(){window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_TOPAY"},maskHide:function(){$(".mask").hide()},maskShow:function(){var t=this;if(t.over)return void requestMsg(t.msg);$(".dynamic-center").show();for(var e=t.contentOne.data.outDayList,n=[],a="",o=0;o<e.length;o++)a="每月"+e[o].outDay+"日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;每月"+e[o].payDay+"日",n.push(a);refundState(n)},ruleClick:function(){this.ruleShow="Y"},maskClick:function(){this.ruleShow=""}}});
//# sourceMappingURL=../../_srcmap/activity/11/changeBillDay.js.map
