"use strict";function addStartDate(){var e=new mui.DtPicker({type:"date",labels:["年","月","日"]});e.show(function(t){startTime=t.y.value+"-"+t.m.value+"-"+t.d.value;var a=t.y.value+"-"+t.m.value+"-"+t.d.value+" ";$(".startTime").text(a),e.dispose()})}function addEndDate(){var e=new mui.DtPicker({type:"Int",picker:2});e.show(function(t){endTime=t.y.value+"-"+t.m.value+"-"+t.d.value;var a=t.y.value+"-"+t.m.value+"-"+t.d.value+" ";$(".endTime").text(a),e.dispose()})}var startTime="",endTime="";$(".dateOne").click(function(){addStartDate()}),$(".dateTwo").click(function(){addEndDate()}),$(".return").click(function(){window.location.href=paraArr+".html"}),$(".finish").click(function(){Number(endTime)>0&&Number(endTime)<Number(startTime)?requestMsg("结束时间不能小于开始时间"):window.location.href=paraArr+".html?startTime="+startTime+"&endTime="+endTime+"&stateStatus="+stateStatus});
//# sourceMappingURL=../../_srcmap/activity/11/changeBillDay.js.map
