"use strict";var RMB=0,t=0,vm=new Vue({el:"#billion",data:{returnData:[],num:"",list600:[],list700:[],list800:[],list900:[],list1000:[],list:{},data:null,active:0,famen:!0,timeoutdata:[],str:{}},created:function(){var e=this;e.initial(),function(){setInterval(e.initial,3e5)}()},methods:{initial:function(){var e=this;$.ajax({url:"/fanbei-web/activity/borrowCashActivities",dataType:"json",type:"post",success:function(n){function o(e){return e<10?"0"+e:e}function a(e){var t=(parseInt(e),new Date(e)),n=t.getFullYear(),a=t.getMonth()+1,i=t.getDate(),l=t.getHours(),s=t.getMinutes(),r=t.getSeconds();return n+"-"+o(a)+"-"+o(i)+" "+o(l)+":"+o(s)+":"+o(r)}function i(e){var t=e.getFullYear(),n=e.getMonth()+1,o=e.getDate(),a=t+"-"+n+"-"+o+" 10:00:00",i=new Date(Date.parse(a.replace(/-/g,"/")));return i=+i+864e5,i=new Date(i)}function l(n){var o=n,l=i(o);t=l.getTime()-o.getTime(),t>=0?(d=Math.floor(t/1e3/60/60/24),m=Math.floor(t/1e3/60/60%24),f=Math.floor(t/1e3/60%60),g=Math.floor(t/1e3%60)):(t+=864e5,d=Math.floor(t/1e3/60/60/24),m=Math.floor(t/1e3/60/60%24),f=Math.floor(t/1e3/60%60),g=Math.floor(t/1e3%60)),document.getElementById("hour").innerHTML=m+"时",document.getElementById("minute").innerHTML=f+"分",document.getElementById("second").innerHTML=g+"秒";document.getElementById("hour").innerHTML,document.getElementById("minute").innerHTML,document.getElementById("second").innerHTML;u=a(u),0==m&0==f&0==g&e.famen&&(e.famen=!1,clearTimeout(w),setTimeout(function(){e.famen=!0},1e3),RMB=document.getElementById("RMB").innerHTML,console.log(RMB),$.ajax({url:"/fanbei-web/activity/randomUser",dataType:"json",type:"post",data:{winAmount:RMB},success:function(t){console.log(t),e.timeoutdata=t}}),e.initial())}console.log(n);var s=n.data;e.num=JSON.parse(s);var r=e.num,c=r.startrTime;(new Date).getTime(),setInterval(function(){l(new Date(new Date((new Date).getTime()-c)))},0);console.log(111111111);var u=r.endTime;console.log(r),console.log(c),console.log(u);var d=0,m=0,f=0,g=0,w=(document.getElementById("start"),setInterval(l(new Date),0));r.length>=10?($(".totalMoney").hide(),$(".num").hide(),$(".totalMoney1").show(),$(".num1").show()):15e8==r&&$("#scroll_div").show(),function(){function e(){o.offsetWidth-a.scrollLeft<=0?a.scrollLeft-=n.offsetWidth:a.scrollLeft++}var t=null,n=document.getElementById("scroll_begin"),o=document.getElementById("scroll_end"),a=document.getElementById("scroll_div");o.innerHTML=n.innerHTML,t=setInterval(e,50),a.onmouseover=function(){clearInterval(t)},a.onmouseout=function(){t=setInterval(e,50)}}()}}),e.$nextTick(function(){RMB=document.getElementById("RMB").innerHTML,RMB>600?($(".winningUser").show(),$(".cash").css({width:"3rem",float:"left"})):($(".winningUser").hide(),$(".cash").css({width:"100%",float:"none"})),600==RMB?e.active=0:700==RMB?e.active=1:800==RMB?e.active=2:900==RMB?e.active=3:1e3==RMB&&(e.active=4)}),$.ajax({url:"/fanbei-web/activity/getBillionWinUser",dataType:"json",type:"post",success:function(e){console.log(e),""!==e&&$("#scroll_div").show()}})},goBorrowMoney:function(){$.ajax({url:"/fanbei-web/tearRiskPacketActivity",dataType:"json",type:"post",success:function(e){if(console.log(e),console.log(e.data.status),e.data.loginUrl)location.href=e.data.loginUrl;else{var t=e.data.status,n=e.data.idNumber,o=e.data.realName;console.log(t),"A1"==t?window.location.href="/fanbei-web/opennative?name=DO_SCAN_ID":"A2"==t?window.location.href='/fanbei-web/opennative?name=DO_BIND_CARD&params={"idNumber":"'+n+'","realName":"'+o+'"}':"A3"==t||"A4"==t?window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_BASIC":"B"==t||"D"==t?window.location.href="/fanbei-web/opennative?name=BORROW_MONEY":"C"==t&&(window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_EXTRA")}},error:function(){requestMsg("请求失败")}})},lists:function(){var e=this;$(".mask").show(),$(".alertRule").show(),$.ajax({url:"/fanbei-web/activity/getWinUser",dataType:"json",type:"post",success:function(t){console.log(t);for(var n in t){e["list"+n]=JSON.parse(t[n])}}})},noShow:function(){$(".mask").hide(),$(".alertRule").hide()}}});
//# sourceMappingURL=../../_srcmap/activity/08/billion.js.map
