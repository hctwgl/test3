"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var s=e.substr(1);strs=s.split("&");for(var a=0;a<strs.length;a++)t[strs[a].split("=")[0]]=unescape(strs[a].split("=")[1])}return t}var currentUrl=window.location.href,index=currentUrl.lastIndexOf("/"),urlName=currentUrl.slice(index+1),url=window.location.href,param=getUrlParam(url),userItemsId=param.userItemsId;$(function(){function e(){function e(){a.offsetWidth-n.scrollLeft<=0?n.scrollLeft-=s.offsetWidth:n.scrollLeft++}var t=null,s=document.getElementById("scroll_begin"),a=document.getElementById("scroll_end"),n=document.getElementById("scroll_div");a.innerHTML=s.innerHTML,t=setInterval(e,50),n.onmouseover=function(){clearInterval(t)},n.onmouseout=function(){t=setInterval(e,50)}}$.ajax({url:"/H5GGShare/ggAskForItems",type:"GET",dataType:"JSON",data:{itemsId:1,friendName:15839790051},success:function(t){if(console.log(t),t.success){var s="",a="",n="",r="",l="",o="";r+="<span class='join'>"+t.data.fakeJoin+"</span>",$(".join").html(r),n+='<i class="friend">'+t.data.friend+"</i>",$(".friend").html(n),a+="<img src="+t.data.resourceDo.value+' alt="" class="banner-img">',$(".banner").html(a),s+='<span class="light">'+t.data.fakeFinal+"</span>",$(".light").html(s),l+='<span class="combo">'+t.data.itemsDo.name+"</span>",$(".combo").html(l),o+='<p class="ruleCont">'+t.data.description+"</p>",$(".ruleCont").html(o),e()}else requestMsg(t.msg)}}),$(".presentCard").click(function(){$.ajax({url:"/H5GGShare/sendToFriend",type:"GET",dataType:"JSON",data:{itemsId:1,friendId:68885},success:function(e){console.log(e),e.success&&("没有登录"==e.msg?window.location.href="gglogin?word=Z&&urlName="+urlName:requestMsg(e.msg))}})}),$(".demandCard").click(function(){$.ajax({url:"/H5GGShare/lightItems",type:"GET",dataType:"JSON",data:{activityId:1},success:function(e){if(console.log(e),e.success)if("没有登录"==e.msg)alert(urlName),window.location.href="gglogin?urlName="+urlName;else{var t=e.data.userName;window.location.href="ggIndexShare?userName="+t}}})}),$(".rules").click(function(){$(".mask").show(),$(".alertRule").show()}),$(".mask").click(function(){$(".mask").hide(),$(".alertRule").hide()})});
//# sourceMappingURL=../../_srcmap/activity/08/ggdemand.js.map
