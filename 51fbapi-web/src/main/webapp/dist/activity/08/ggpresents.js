"use strict";$(function(){$.ajax({url:"/H5GGShare/ggSendItems",type:"GET",dataType:"JSON",data:{userItemsId:30,userName:15839790051},success:function(s){if(console.log(s),s.success){var a="",e="",t="",n="";n+="<span class='join'>"+s.data.fakeJoin+"</span>",$(".join").html(n),t+='<i class="friend">'+s.data.friend+"</i>",$(".friend").html(t),e+="<img src="+s.data.itemsDo.iconUrl+' alt="" class="banner-img">',$(".banner").html(e),a+='<span class="light">'+s.data.fakeFinal+"</span>",$(".light").html(a)}else requestMsg(s.msg)}}),$(".presentCard").click(function(){$.ajax({url:"/H5GGShare/pickUpItems",type:"GET",dataType:"JSON",data:{userItemsId:30},success:function(s){var a=s.data;if(s.success){var e=a.loginUrl;void 0!=e&&""!=e&&(window.location.href=e)}else requestMsg(a.msg)}})}),$(".demandCard").click(function(){$.ajax({url:"/H5GGShare/lightItems",type:"GET",dataType:"JSON",data:{activityId:1},success:function(s){if(console.log(s),s.success){var a=s.loginUrl;alert(a)}else requestMsg(s.msg)}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggpresents.js.map
