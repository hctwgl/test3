"use strict";function ScrollImgLeft(){function e(){n.offsetWidth-o.scrollLeft<=0?o.scrollLeft-=a.offsetWidth:o.scrollLeft++}var t=null,a=document.getElementById("scroll_begin"),n=document.getElementById("scroll_end"),o=document.getElementById("scroll_div");n.innerHTML=a.innerHTML,t=setInterval(e,50),o.onmouseover=function(){clearInterval(t)},o.onmouseout=function(){t=setInterval(e,50)}}function countDown(e,a){var n=e.currentDate,o=e.endTime,i=e.startrTime;console.log(new Date(i),"活动开始时间"),console.log(new Date(n),"111后天服务器当前时间"),console.log(new Date(o),"活动结束时间");var l,r,s,c;if(i>n)$("#count").html("活动暂未开始");else{var u=new Date(i).getDate(),d=new Date(o).getDate(),m=new Date(n).getHours(),f=new Date(n).getFullYear(),w=new Date(n).getMonth()+1,v=new Date(n).getDate();(m>10||u==v)&&(v+=1),clearInterval(timerId),timerId=setInterval(function(){n+=1e3;var e=f+"/"+w+"/"+v+" 10:00:00";l=new Date(e),v==d&&(l=new Date(o)),t=l.getTime()-n,r=Math.floor(t/1e3/60/60%24),s=Math.floor(t/1e3/60%60),c=Math.floor(t/1e3%60),a.hourData=r,a.minuteData=s,a.secondData=c;var i=document.getElementById("RMB").innerHTML;0==a.hourData&&0==a.minuteData&&0==a.secondData&&$.ajax({url:"/fanbei-web/activity/randomUser",dataType:"json",type:"post",data:{winAmount:i},success:function(e){console.log(e),a.timeoutdata=e}}),t<1e3&&(n=l.getTime(),v+=1),n>o&&(a.hourData="00",a.minuteData="00",a.secondData="00",clearInterval(timerId))},1e3)}}var RMB=0,t=0,startTime=null,timerId,diff=0,vm=new Vue({el:"#billion",data:{returnData:[],num:"",list600:[],list700:[],list800:[],list900:[],list1000:[],list:{},data:null,active:0,famen:!0,timeoutdata:[],str:{},hourData:"",minuteData:"",secondData:"",aNum:""},created:function(){var e=this;e.initial(),function(){setInterval(e.initial,3e5)}()},methods:{initial:function(){var e=this;$.ajax({url:"/fanbei-web/activity/borrowCashActivities",dataType:"json",type:"post",success:function(t){var a=t.data,n=JSON.parse(a);console.log(n),e.num=n,countDown({currentDate:n.currentDate,startrTime:n.startrTime,endTime:n.endTime},e);var o=n.amount;o=o.replace(/"/g,""),o=o.replace(/"/g,"");var i=o.split("");i.length>=10?($(".totalMoney").hide(),$(".num").hide(),$(".totalMoney1").show(),$(".num1").show()):i.length>=15e8&&$("#scroll_div").show(),ScrollImgLeft()}}),e.$nextTick(function(){RMB=document.getElementById("RMB").innerHTML,RMB>600?($(".winningUser").show(),$(".cash").css({width:"3rem",float:"left"})):($(".winningUser").hide(),$(".cash").css({width:"100%",float:"none"})),600==RMB?e.active=0:700==RMB?e.active=1:800==RMB?e.active=2:900==RMB?e.active=3:1e3==RMB&&(e.active=4)}),$.ajax({url:"/fanbei-web/activity/getBillionWinUser",dataType:"json",type:"post",success:function(t){console.log(t),e.aNum=t,""!==t&&$("#scroll_div").show()}})},goBorrowMoney:function(){$.ajax({url:"/fanbei-web/tearRiskPacketActivity",dataType:"json",type:"post",success:function(e){if(console.log(e),console.log(e.data.status),e.data.loginUrl)location.href=e.data.loginUrl;else{var t=e.data.status,a=e.data.idNumber,n=e.data.realName;console.log(t),"A1"==t?window.location.href="/fanbei-web/opennative?name=DO_SCAN_ID":"A2"==t?window.location.href='/fanbei-web/opennative?name=DO_BIND_CARD&params={"idNumber":"'+a+'","realName":"'+n+'"}':"A3"==t||"A4"==t?window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_BASIC":"B"==t||"D"==t?window.location.href="/fanbei-web/opennative?name=BORROW_MONEY":"C"==t&&(window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_EXTRA")}},error:function(){requestMsg("请求失败")}})},lists:function(){var e=this;$(".mask").show(),$(".alertRule").show(),$("body").css("overflow","hidden"),$.ajax({url:"/fanbei-web/activity/getWinUser",dataType:"json",type:"post",success:function(t){console.log(t);for(var a in t){e["list"+a]=JSON.parse(t[a])}}})},noShow:function(){$(".mask").hide(),$(".alertRule").hide()}}});
//# sourceMappingURL=../../_srcmap/activity/08/billion.js.map
