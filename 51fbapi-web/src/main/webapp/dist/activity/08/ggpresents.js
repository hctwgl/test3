"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var s=e.substr(e.indexOf("?")+1,e.length),a=[];a=s.split("&");for(var n=0;n<a.length;n++)t[a[n].split("=")[0]]=unescape(a[n].split("=")[1])}return t}$(function(){var e=window.location.href;alert("currentUrl->"+e);var t=e.lastIndexOf("/"),s=e.indexOf("?"),a=e.slice(t+1,s),n=getUrlParam(e),l=n.activityId,r=n.userItemsId,i=n.userName;alert("activityId=>"+l),alert("userItemsId=>"+r),$.ajax({url:"/H5GGShare/ggSendItems",type:"GET",dataType:"JSON",data:{userItemsId:r},success:function(e){if(console.log(e),e.success){var t="",s="",a="",n="",l="",r="";n+="<span class='join'>"+e.data.fakeJoin+"</span>",$(".join").html(n),a+='<i class="friend">'+e.data.friend+"</i>",$(".friend").html(a),s+="<img src="+e.data.resourceDo.value+' alt="" class="banner-img">',$(".banner").html(s),t+='<span class="light">'+e.data.fakeFinal+"</span>",$(".light").html(t),l+='<span class="combo">'+e.data.itemsDo.name+"</span>",$(".combo").html(l),r+='<p class="ruleCont">'+e.data.description+"</p>",$(".ruleCont").html(r)}else requestMsg(e.msg)}}),function(){function e(){a.offsetWidth-n.scrollLeft<=0?n.scrollLeft-=s.offsetWidth:n.scrollLeft++}var t=null,s=document.getElementById("scroll_begin"),a=document.getElementById("scroll_end"),n=document.getElementById("scroll_div");a.innerHTML=s.innerHTML,t=setInterval(e,50),n.onmouseover=function(){clearInterval(t)},n.onmouseout=function(){t=setInterval(e,50)}}(),$(".rules").click(function(){$(".mask").show(),$(".alertRule").show()}),$(".mask").click(function(){$(".mask").hide(),$(".alertRule").hide()}),$(".presentCard").click(function(){$(this).html();$.ajax({url:"/H5GGShare/pickUpItems",type:"GET",dataType:"JSON",data:{userItemsId:r},success:function(e){console.log(e),e.success&&("没有登录"==e.msg?window.location.href="gglogin?word=Z&&urlName="+a+"&activityId="+l+"&userName="+i+"&userItemsId="+r:(requestMsg(e.msg),console.log(e.msg)))}})}),$(".demandCard").click(function(){$(this).html();$.ajax({url:"/H5GGShare/lightItems",type:"GET",dataType:"JSON",data:{activityId:l},success:function(e){if(console.log(e),e.success)if("没有登录"==e.msg)window.location.href="gglogin?urlName="+a;else{var t=e.data.userName;window.location.href="ggIndexShare?userName="+t}}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggpresents.js.map
