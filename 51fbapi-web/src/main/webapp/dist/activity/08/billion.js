"use strict";function ScrollImgLeft(){function e(){a.offsetWidth-o.scrollLeft<=0?o.scrollLeft-=n.offsetWidth:o.scrollLeft++}var t=null,n=document.getElementById("scroll_begin"),a=document.getElementById("scroll_end"),o=document.getElementById("scroll_div");a.innerHTML=n.innerHTML,t=setInterval(e,50),o.onmouseover=function(){clearInterval(t)},o.onmouseout=function(){t=setInterval(e,50)}}function countDown(e,n){var a=e.currentDate,o=e.endTime,i=e.startrTime;console.log(new Date(a)),console.log(new Date(i)),console.log(new Date(o));var l,s,r,c;if(a<i)$("#count").html("活动暂未开始");else if(i>o)$("#count").html("活动结束");else{var u=new Date(i).getDate(),d=new Date(i).getHours();(d>10||new Date(a).getDate()==new Date(i).getDate())&&(u+=1);var m=new Date(i).getFullYear(),f=new Date(i).getMonth()+1;document.getElementById("hour"),document.getElementById("minute"),document.getElementById("second");clearInterval(timerId),timerId=setInterval(function(){i+=1e3,l=new Date(m+"/"+f+"/"+u+" 10:00:00"),u==new Date(o).getDate()&&(l=new Date(o)),t=l.getTime()-i,s=Math.floor(t/1e3/60/60%24),r=Math.floor(t/1e3/60%60),c=Math.floor(t/1e3%60),n.hourData=s,n.minuteData=r,n.secondData=c,t<1e3&&(i=l.getTime(),u+=1),i>o&&(n.hourData="00",n.minuteData="00",n.secondData="00",clearInterval(timerId))},1e3)}}var RMB=0,t=0,startTime=null,timerId,vm=new Vue({el:"#billion",data:{returnData:[],num:"",list600:[],list700:[],list800:[],list900:[],list1000:[],list:{},data:null,active:0,famen:!0,timeoutdata:[],str:{},hourData:"",minuteData:"",secondData:""},created:function(){var e=this;e.initial(),function(){setInterval(e.initial,3e5)}()},methods:{initial:function(){var e=this;$.ajax({url:"/fanbei-web/activity/borrowCashActivities",dataType:"json",type:"post",success:function(t){var n=t.data,a=JSON.parse(n);console.log(a),e.num=a,countDown(a,e);var o=a.amount;o=o.replace(/"/g,""),o=o.replace(/"/g,"");var i=o.split("");console.log(o),console.log(i),i.length>=10?($(".totalMoney").hide(),$(".num").hide(),$(".totalMoney1").show(),$(".num1").show()):i.length>=15e8&&$("#scroll_div").show(),ScrollImgLeft()}}),e.$nextTick(function(){RMB=document.getElementById("RMB").innerHTML,RMB>600?($(".winningUser").show(),$(".cash").css({width:"3rem",float:"left"})):($(".winningUser").hide(),$(".cash").css({width:"100%",float:"none"})),600==RMB?e.active=0:700==RMB?e.active=1:800==RMB?e.active=2:900==RMB?e.active=3:1e3==RMB&&(e.active=4)}),$.ajax({url:"/fanbei-web/activity/getBillionWinUser",dataType:"json",type:"post",success:function(e){console.log(e),""!==e&&$("#scroll_div").show()}})},goBorrowMoney:function(){$.ajax({url:"/fanbei-web/tearRiskPacketActivity",dataType:"json",type:"post",success:function(e){if(console.log(e),console.log(e.data.status),e.data.loginUrl)location.href=e.data.loginUrl;else{var t=e.data.status,n=e.data.idNumber,a=e.data.realName;console.log(t),"A1"==t?window.location.href="/fanbei-web/opennative?name=DO_SCAN_ID":"A2"==t?window.location.href='/fanbei-web/opennative?name=DO_BIND_CARD&params={"idNumber":"'+n+'","realName":"'+a+'"}':"A3"==t||"A4"==t?window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_BASIC":"B"==t||"D"==t?window.location.href="/fanbei-web/opennative?name=BORROW_MONEY":"C"==t&&(window.location.href="/fanbei-web/opennative?name=DO_PROMOTE_EXTRA")}},error:function(){requestMsg("请求失败")}})},lists:function(){var e=this;$(".mask").show(),$(".alertRule").show(),$.ajax({url:"/fanbei-web/activity/getWinUser",dataType:"json",type:"post",success:function(t){console.log(t);for(var n in t){e["list"+n]=JSON.parse(t[n])}}})},noShow:function(){$(".mask").hide(),$(".alertRule").hide()}}});
//# sourceMappingURL=../../_srcmap/activity/08/billion.js.map
