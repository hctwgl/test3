"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName);var vm=new Vue({el:"#vueCon",data:{tableUrl:"/fanbei-web/partActivityInfo?modelId=",className:["Monday","Tuesday","Wednesday","Thursday","Friday"],content:{currentTime:20170615,beginTime:20170612,list:[{time:20170613},{time:20170614}]},beginTime:20170609,current:3,option:{}},created:function(){this.logData()},methods:{signIn:function(e){console.log(e),this.logData()},selected:function(e){var t=this.content.list;console.log(e),console.log(t.indexOf(e));for(var n in t)if(t[n]==e)return!0;return!1},logData:function logData(){var self=this;self.$http.get(self.tableUrl,self.option).then(function(res){self.content=eval("("+res.data+")"),console.log(self.content),self.$nextTick(function(){})},function(e){console.log(e)})}}});
//# sourceMappingURL=../../_srcmap/activity/06/sign.js.map
