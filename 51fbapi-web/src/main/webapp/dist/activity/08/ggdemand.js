"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var a=e.substr(e.indexOf("?")+1,e.length),n=[];n=a.split("&");for(var s=0;s<n.length;s++)t[n[s].split("=")[0]]=unescape(n[s].split("=")[1])}return t}$(function(){function e(){function e(){n.offsetWidth-s.scrollLeft<=0?s.scrollLeft-=a.offsetWidth:s.scrollLeft++}var t=null,a=document.getElementById("scroll_begin"),n=document.getElementById("scroll_end"),s=document.getElementById("scroll_div");n.innerHTML=a.innerHTML,t=setInterval(e,50),s.onmouseover=function(){clearInterval(t)},s.onmouseout=function(){t=setInterval(e,50)}}var t=window.location.href,a=t.lastIndexOf("/"),n=t.indexOf("?"),s=t.slice(a+1,n),i=getUrlParam(t),o=i.itemsId,l=i.userName,c=i.activityId;i.loginSource;$.ajax({url:"/H5GGShare/ggAskForItems",type:"GET",dataType:"JSON",data:{itemsId:o,friendName:l},success:function(t){if(console.log(t),t.success){var a="",n="",s="",i="",o="",l="";i+="<span class='join'>"+t.data.fakeJoin+"</span>",$(".join").html(i),s+='<i class="friend">'+t.data.friend+"</i>",$(".friend").html(s),n+="<img src="+t.data.resourceDo.value+' alt="" class="banner-img">',$(".banner").html(n),a+='<span class="light">'+t.data.fakeFinal+"</span>",$(".light").html(a),o+='<span class="combo">'+t.data.itemsDo.name+"</span>",$(".combo").html(t.data.itemsDo.name),l+='<p class="ruleCont">'+t.data.description+"</p>",$(".ruleCont").html(l),e()}else requestMsg(t.msg)}}),$(".presentCard").click(function(){$.ajax({url:"/H5GGShare/sendToFriend",type:"GET",dataType:"JSON",data:{itemsId:o,friendName:l},success:function(e){console.log(e),e.success&&("没有登录"==e.msg?window.location.href="gglogin?word=S&urlName="+s+"&itemsId="+o+"&userName="+l+"&activityId="+c:requestMsg(e.msg))}})}),$(".demandCard").click(function(){$.ajax({url:"/H5GGShare/lightItems",type:"GET",dataType:"JSON",data:{activityId:c},success:function(e){console.log(e),e.success&&("没有登录"==e.msg?window.location.href="gglogin?word=S&urlName="+s+"&itemsId="+o+"&userName="+l+"&activityId="+c:(l=e.data.userName,window.location.href="ggIndexShare?userName="+l+"&activityId="+c))}})}),$(".rules").click(function(){$(".mask").show(),$(".alertRule").show()}),$(".mask").click(function(){$(".mask").hide(),$(".alertRule").hide()}),$(".joinAmount").click(function(){window.location.href="ggrankingList?activityId="+c})});
//# sourceMappingURL=../../_srcmap/activity/08/ggdemand.js.map
