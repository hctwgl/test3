"use strict";function add0(t){return t<10?"0"+t:t}function format(t){var e=new Date(t),o=e.getFullYear(),n=e.getMonth()+1,r=e.getDate(),a=e.getHours(),d=e.getMinutes(),f=e.getSeconds();return o+"-"+add0(n)+"-"+add0(r)+" "+add0(a)+":"+add0(d)+":"+add0(f)}function showTimerS(t){var e=0,o=0,n=0;return t>0&&(e=Math.floor(t/3600),o=Math.floor(t/60)-60*e,n=Math.floor(t)-60*e*60-60*o),e<=9&&(e="0"+e),o<=9&&(o="0"+o),n<=9&&(n="0"+n),e+":"+o+":"+n}var orderNo=getUrl("orderNo"),plantform=getUrl("plantform"),vm=new Vue({el:"#gameOrderDetail",data:{content:{},diff:""},created:function(){this.logData()},methods:{logData:function(){var t=this;$.ajax({type:"get",url:"/game/pay/orderInfo",data:{orderNo:orderNo},success:function(e){console.log(e),e.success&&(t.content=e.data,console.log(t.content),t.content.orderStartTime=format(1e3*t.content.gmtCreate),t.content.payTime=format(1e3*t.content.gmtCreate),t.diff=t.content.gmtPayEnd-t.content.gmtPayStart,console.log(t.diff),showTimerS(t.diff),t.diff--,window.setInterval(function(){showTimerS(t.diff),t.diff--},1e3))},error:function(){requestMsg("哎呀，出错了！")}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/gameOrderDetail.js.map
