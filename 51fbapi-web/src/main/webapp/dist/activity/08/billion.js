"use strict";function ScrollImgLeft(){function e(){a.offsetWidth-o.scrollLeft<=0?o.scrollLeft-=n.offsetWidth:o.scrollLeft++}var t=null,n=document.getElementById("scroll_begin"),a=document.getElementById("scroll_end"),o=document.getElementById("scroll_div");a.innerHTML=n.innerHTML,t=setInterval(e,50),o.onmouseover=function(){clearInterval(t)},o.onmouseout=function(){t=setInterval(e,50)}}var RMB=0,t=0,startTime=null,vm=new Vue({el:"#billion",data:{returnData:[],num:"",list600:[],list700:[],list800:[],list900:[],list1000:[],list:{},data:null,active:0,famen:!0,timeoutdata:[],str:{}},created:function(){var e=this;e.initial(),function(){setInterval(e.initial,3e5)}()},methods:{initial:function(){var e=this;$.ajax({url:"/fanbei-web/activity/borrowCashActivities",dataType:"json",type:"post",success:function(e){var n={amount:1166942,currentDate:1504885534518,startrTime:1505016e6,endTime:15053616e5},a=n.endTime,o=n.startrTime,i=n.currentDate;if(i>o)$("#count").html("活动暂未开始");else if(o>a)$("#count").html("活动结束");else{var l=new Date(o).getDate(),r=new Date(o).getHours();(r>10||new Date(i).getDate()==new Date(o).getDate())&&(l+=1);var s=new Date(o).getFullYear(),c=new Date(o).getMonth()+1,u=setInterval(function(){o+=1e3;var e=new Date(s+"/"+c+"/"+l+" 10:00:00");l==new Date(a).getDate()&&(e=new Date(a)),t=e.getTime()-o;var n=Math.floor(t/1e3/60/60%24),i=Math.floor(t/1e3/60%60),r=Math.floor(t/1e3%60);document.getElementById("hour").innerHTML=n+"时",document.getElementById("minute").innerHTML=i+"分",document.getElementById("second").innerHTML=r+"秒",t<1e3&&(o=e.getTime(),l+=1),o>a&&(document.getElementById("hour").innerHTML="0时",document.getElementById("minute").innerHTML="0分",document.getElementById("second").innerHTML="0秒",clearInterval(u))},1e3)}n.length>=10?($(".totalMoney").hide(),$(".num").hide(),$(".totalMoney1").show(),$(".num1").show()):15e8==n&&$("#scroll_div").show(),ScrollImgLeft()}}),e.$nextTick(function(){RMB=document.getElementById("RMB").innerHTML,RMB>600?($(".winningUser").show(),$(".cash").css({width:"3rem",float:"left"})):($(".winningUser").hide(),$(".cash").css({width:"100%",float:"none"})),600==RMB?e.active=0:700==RMB?e.active=1:800==RMB?e.active=2:900==RMB?e.active=3:1e3==RMB&&(e.active=4)}),$.ajax({url:"/fanbei-web/activity/getBillionWinUser",dataType:"json",type:"post",success:function(e){console.log(e),""!==e&&$("#scroll_div").show()}})},goBorrowMoney:function(){$.ajax({url:"/fanbei-web/tearRiskPacketActivity",dataType:"json",type:"post",success:function(e){if(console.log(e),console.log(e.data.status),e.data.loginUrl)location.href=e.data.loginUrl;else{var t=e.data.status,n=e.data.idNumber,a=e.data.realName;console.log(t),"A1"==t?window.location.href="/fanbei-web/opennative?name=DO_SCAN_ID":"A2"==t?window.location.href='/fanbei-web/opennative?name=DO_BIND_CARD&params={"idNumber":"'+n+'","realName":"'+a+'"}':"A3"==t||"A4"==t?window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_BASIC":"B"==t||"D"==t?window.location.href="/fanbei-web/opennative?name=BORROW_MONEY":"C"==t&&(window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_EXTRA")}},error:function(){requestMsg("请求失败")}})},lists:function(){var e=this;$(".mask").show(),$(".alertRule").show(),$.ajax({url:"/fanbei-web/activity/getWinUser",dataType:"json",type:"post",success:function(t){console.log(t);for(var n in t){e["list"+n]=JSON.parse(t[n])}}})},noShow:function(){$(".mask").hide(),$(".alertRule").hide()}}});
//# sourceMappingURL=../../_srcmap/activity/08/billion.js.map
