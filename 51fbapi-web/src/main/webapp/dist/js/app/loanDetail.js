"use strict";var lsmNo=getUrl("lsmNo"),vm=new Vue({el:"#loanDetail",data:{content:{moneyMin:500,moneyMax:3e3,timeMin:2,timeMax:20,lsmName:"51fanbei",timeUnit:2},flowCont:[]},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/borrowCash/getRegisterLoanSupermarket",data:{lsmNo:lsmNo},success:function success(data){self.content=eval("("+data+")").data,self.flow(),console.log(self.content)},error:function(){requestMsg("哎呀，出错了！")}})},fen:function(t){return t.split("；")},dayMonth:function(){return 2==this.content.timeUnit?"月":"日"},flow:function(){for(var t=[{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail01.png",name:"身份认证"},{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail02.png",name:"银行卡认证"},{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail03.png",name:"运营商认证"},{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail04.png",name:"个人信息认证"},{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail05.png",name:"通讯录认证"},{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail06.png",name:"芝麻信用认证"},{imgUrl:"http://f.51fanbei.com/h5/app/activity/09/loanDetail07.png",name:"等待认证"}],a=this.content,n=a.applyProcess.split(","),e=0;e<n.length;e++)for(var i=0;i<t.length;i++)n[e]==t[i].name&&a.flowCont.push(t[i])}}});
//# sourceMappingURL=../../_srcmap/js/app/loanDetail.js.map
