"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName);var vm=new Vue({el:"#vueCon",dataUrl:"/fanbei-web/initActivitySign",msgUrl:"/fanbei-web/activitySignIn",data:{className:["Monday","Tuesday","Wednesday","Thursday","Friday"],content:{},fixContent:{},current:4},created:function(){this.logData()},methods:{signIn:function(e){this.logData()},selected:function(e){var t=this.fixContent.timeList;for(var n in t)if(t[n]==e)return!0;return!1},logData:function logData(){var self=this;self.$http.post("/fanbei-web/initActivitySign").then(function(res){self.content=eval("("+res.data+")");var contentData=self.content.data,currentTime=contentData.currentDate;currentTime=currentTime.replace(/\-/g,""),currentTime=parseInt(currentTime);var beginTime=contentData.startDate;beginTime=beginTime.replace(/\-/g,""),beginTime=parseInt(beginTime);for(var timeList=contentData.resultList,i=0;i<timeList.length;i++)timeList[i]=timeList[i].replace(/\-/g,""),timeList[i]=parseInt(timeList[i]);self.fixContent=contentData,self.current=new Date(contentData.currentDate).getDay(),console.log(self.current)},function(e){console.log(e)})}}});
//# sourceMappingURL=../../_srcmap/activity/06/sign.js.map
