"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName);var vm=new Vue({el:"#vueCon",data:{tableUrl:"/fanbei-web/partActivityInfo?modelId=",className:["Monday","Tuesday","Wednesday","Thursday","Friday"],content:{currentTime:20170612,beginTime:20170609,list:[{time:20170610},{time:20170611}]},beginTime:20170609,current:4,option:{}},created:function(){this.logData()},methods:{signIn:function(e){console.log(e)},selected:function(e){var t=this.content.list;console.log(e),console.log(t.indexOf(e));for(var n in t)if(t[n]==e)return!0;return!1},logData:function logData(){Vue.http.options.emulateJSON=!0;var self=this,op={data:JSON.stringify(self.option)};self.$http.get(self.tableUrl,op).then(function(res){self.content=eval("("+res.data+")"),console.log(self.content),self.$nextTick(function(){})},function(e){console.log(e)})}}});
//# sourceMappingURL=../../_srcmap/activity/06/sign.js.map
