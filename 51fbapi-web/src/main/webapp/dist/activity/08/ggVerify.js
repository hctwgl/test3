"use strict";function getUrlParam(t){var e=new Object;if(-1!=t.indexOf("?")){var r=t.substr(t.indexOf("?")+1,t.length),s=[];s=r.split("&");for(var a=0;a<s.length;a++)e[s[a].split("=")[0]]=unescape(s[a].split("=")[1])}return e}$(function(){function t(){c--,c<=0?($(".btn").text("获取验证码"),clearInterval(e),c=60,$(".btn").attr("isState",0)):$(".btn").text(c+" s")}var e,r=window.location.href,s=getUrlParam(r),a=s.word,o=s.urlName,i=(s.userName,s.activityId),n=s.userItemsId,l=s.itemsId,c=60;$(".btn").click(function(){var r=$(".phoneNumber-right").val(),s=/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(r),a=$(".btn").attr("isState");0!=a&&a||(s?$.ajax({url:"/H5GGShare/boluomeActivityForgetPwd",type:"POST",dataType:"JSON",data:{mobile:r},success:function(r){console.log(r),r.success?($(".btn").attr("isState",1),$(".btn").text(c+" s"),e=setInterval(t,1e3)):"ForgetPwd"==r.url&&requestMsg(r.msg)}}):requestMsg("请填写正确的手机号"),localStorage.setItem("user",r),console.log(r))}),$(".nextStep").click(function(){var t=$(".mesg-right").val(),e=$(".phoneNumber-right").val();$.ajax({url:"/H5GGShare/boluomeActivityCheckVerifyCode",type:"POST",dataType:"JSON",data:{mobile:e,verifyCode:t},success:function(r){if("ForgetPwd"==r.url&&requestMsg(r.msg),console.log(r),!r.success)return requestMsg(r.msg),!1;localStorage.setItem("mesg",t),window.location.href="ggForgetP?urlName="+o+"&userName="+e+"&activityId="+i+"&userItemsId="+n+"&itemsId="+l+"&word="+a}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggVerify.js.map
