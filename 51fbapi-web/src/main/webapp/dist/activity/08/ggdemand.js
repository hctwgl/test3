"use strict";var currentUrl=window.location.href,index=currentUrl.lastIndexOf("/"),urlName=currentUrl.slice(index+1);console.log(urlName),$(function(){$.ajax({url:"/H5GGShare/ggAskForItems",type:"GET",dataType:"JSON",data:{itemsId:1},success:function(a){if(console.log(a),a.success){var e="",s="",t="",n="";n+="<span class='join'>"+a.data.fakeJoin+"</span>",$(".join").html(n),t+='<i class="friend">'+a.data.friend+"</i>",$(".friend").html(t),s+="<img src="+a.data.itemsDo.iconUrl+' alt="" class="banner-img">',$(".banner").html(s),e+='<span class="light">'+a.data.fakeFinal+"</span>",$(".light").html(e)}else requestMsg(a.msg)}}),$(".presentCard").click(function(){$.ajax({url:"/H5GGShare/sendToFriend",type:"GET",dataType:"JSON",data:{itemsId:1,friendId:68885},success:function(a){if(console.log(a),a.success){var e="";try{e=a.data.loginUrl}catch(a){}void 0!=e&&""!=e?window.location.href="gglogin?urlName="+urlName:requestMsg(a.msg)}requestMsg(a.msg)}})}),$(".demandCard").click(function(){$.ajax({url:"/H5GGShare/lightItems",type:"GET",dataType:"JSON",data:{activityId:1},success:function(a){if(console.log(a),a.success){var e="";try{e=a.data.loginUrl}catch(a){}void 0!=e&&""!=e&&(window.location.href="gglogin?urlName="+urlName),requestMsg(a.msg)}}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggdemand.js.map
