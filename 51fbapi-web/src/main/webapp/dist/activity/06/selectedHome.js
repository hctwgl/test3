"use strict";var modelId=getUrl("modelId");new Vue({el:"#vueCon",data:{tableUrl:"/fanbei-web/partActivityInfo?modelId="+modelId,content:[],ht:"#",moreHref:"getMore?modelId="+modelId+"&subjectId=",divTop:"",option:{}},created:function(){this.logData(),window.addEventListener("scroll",this.handleScroll)},methods:{handleScroll:function(){jQuery(window).scrollTop()>=this.divTop?jQuery("#listAlert").addClass("fixTop"):jQuery("#listAlert").removeClass("fixTop")},logData:function logData(){Vue.http.options.emulateJSON=!0;var self=this,op={data:JSON.stringify(self.option)};self.$http.get(self.tableUrl,op).then(function(res){self.content=eval("("+res.data+")"),console.log(self.content),self.$nextTick(function(){self.divTop=document.getElementById("listAlert").offsetTop})},function(e){console.log(e)})}}});
//# sourceMappingURL=../../_srcmap/activity/06/selectedHome.js.map
