"use strict";function step(){setTimeout(function(){$(".lineBox01").addClass("lineShow01"),$(".word01").addClass("wordShow01"),$(".lineBox02").addClass("lineShow02"),$(".word02").addClass("wordShow02"),$(".lineBox03").addClass("lineShow03"),$(".word03").addClass("wordShow03"),$(".lineBox04").addClass("lineShow04"),$(".word04").addClass("wordShow04")},500)}var protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#ggFix",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function(){var o=this;$.ajax({type:"post",url:"/activity/freshmanShare/homePage",success:function(t){console.log(t),o.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut()})},error:function(){requestMsg("哎呀，出错了！")}})},ruleClick:function(){this.ruleShow=!0},closeClick:function(){this.ruleShow=!1}}});step();
//# sourceMappingURL=../../_srcmap/activity/11/ggFix.js.map
