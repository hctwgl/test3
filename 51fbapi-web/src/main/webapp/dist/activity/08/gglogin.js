"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var r=e.substr(e.indexOf("?")+1,e.length),i=[];i=r.split("&");for(var a=0;a<i.length;a++)t[i[a].split("=")[0]]=unescape(i[a].split("=")[1])}return t}$(function(){var e=window.location.href,t=getUrlParam(e),r=t.word,i=t.urlName,a=t.userName,s=t.activityId,n=t.userItemsId,c=t.itemsId;$(".loginbtn").click(function(){var e=$(".pinp").val(),t=$(".check").val();if(/^1(3|4|5|7|8)\d{9}$/i.test(e)){var a=String(CryptoJS.MD5(t));$.ajax({url:"/H5GGShare/boluomeActivityLogin",type:"POST",dataType:"JSON",data:{userName:e,password:a,activityId:s,refUserName:e,urlName:r},success:function(t){console.log(t),t.success?window.location.href="Z"==r?i+"?userName="+e+"&activityId="+s+"&userItemsId="+n:"S"==r?i+"?userName="+e+"&itemsId="+c:"ggIndexShare?activityId="+s+"&userName="+e:requestMsg(t.msg)}})}else requestMsg("请填写正确的手机号")}),$("#gg_register").click(function(){alert(r),window.location.href="ggregister?word="+r+"&userName="+a+"&activityId="+s+"&userItemsId="+n+"&itemsId="+c+"&urlName="+i}),$("#gg_forget").click(function(){alert(r),window.location.href="ggVerify?word="+r+"&userName="+a+"&activityId="+s+"&userItemsId="+n+"&itemsId="+c+"&urlName="+i})});
//# sourceMappingURL=../../_srcmap/activity/08/gglogin.js.map
