"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName);var vm=new Vue({el:"#vueCon",dataUrl:"/fanbei-web/initActivitySign",msgUrl:"/fanbei-web/activitySignIn",data:{className:["Monday","Tuesday","Wednesday","Thursday","Friday"],content:{},fixContent:{},okContent:[!1,!1,!1,!1,!1],current:"",msg:{}},created:function(){this.logData()},methods:{signIn:function signIn(time){var self=this;self.$http.post("/fanbei-web/activitySignIn").then(function(res){self.msg=eval("("+res.data+")"),requestMsg(self.msg.msg),self.logData()},function(e){})},logData:function logData(){var self=this;self.$http.post("/fanbei-web/initActivitySign").then(function(res){self.content=eval("("+res.data+")");var contentData=self.content.data,currentTime=contentData.currentDate;currentTime=currentTime.replace(/\-/g,""),currentTime=parseInt(currentTime);var beginTime=contentData.startDate;beginTime=beginTime.replace(/\-/g,""),beginTime=parseInt(beginTime);for(var timeList=contentData.resultList,i=0;i<timeList.length;i++)timeList[i]=timeList[i].replace(/\-/g,""),timeList[i]=parseInt(timeList[i]),timeList.push[timeList[i]];self.fixContent.timeList=timeList,self.fixContent.currentTime=currentTime,self.fixContent.beginTime=beginTime,self.current=currentTime-beginTime+1;for(var _i in self.fixContent.timeList)self.okContent.splice(self.fixContent.timeList[_i]-self.fixContent.beginTime,1,!0)},function(e){})}}});
//# sourceMappingURL=../../_srcmap/activity/06/sign.js.map
