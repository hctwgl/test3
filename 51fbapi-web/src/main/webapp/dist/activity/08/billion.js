"use strict";var vm=new Vue({el:"#billion",data:{returnData:[]},created:function(){this.initial()},methods:{initial:function(){$.ajax({url:"/app/activity/borrowCashActivities",dataType:"json",type:"post",success:function(t){console.log(t);var e=t.data;console.log(e);var n=e,o=n.split("");console.log(o);for(var l=0;l<o.length;l++){var i=o.index;o[l]=i,"."==o[l]&&o[l]}!function(){function t(){o.offsetWidth-l.scrollLeft<=0?l.scrollLeft-=n.offsetWidth:l.scrollLeft++}var e=null,n=document.getElementById("scroll_begin"),o=document.getElementById("scroll_end"),l=document.getElementById("scroll_div");o.innerHTML=n.innerHTML,e=setInterval(t,50),l.onmouseover=function(){clearInterval(e)},l.onmouseout=function(){e=setInterval(t,50)}}()}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/billion.js.map
