"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var r=e.substr(e.indexOf("?")+1,e.length),s=[];s=r.split("&");for(var a=0;a<s.length;a++)t[s[a].split("=")[0]]=unescape(s[a].split("=")[1])}return t}$(function(){function e(){u--,u<=0?($(".btn").text("获取验证码"),clearInterval(t),u=60,$(".btn").attr("isState",0)):$(".btn").text(u+" s")}var t,r=window.location.href,s=getUrlParam(r),a=s.word,o=s.urlName,i=(s.userName,s.activityId),n=s.userItemsId,l=s.itemsId,u=60;$(".btn").click(function(){var r=$(".phoneNumber-right").val();/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(r)?$.ajax({url:"/H5GGShare/boluomeActivityForgetPwd",type:"POST",dataType:"JSON",data:{mobile:r},success:function(r){console.log(r),r.success?($(".btn").attr("isState",1),$(".btn").text(u+" s"),t=setInterval(e,1e3)):"ForgetPwd"==r.url&&requestMsg(r.msg)}}):requestMsg("请填写正确的手机号"),localStorage.setItem("user",r),console.log(r)}),$(".nextStep").click(function(){var e=$(".mesg-right").val(),t=$(".phoneNumber-right").val();return""==t?(requestMsg("请填写正确的手机号"),!1):""==e?(requestMsg("请填写正确的验证码"),!1):void $.ajax({url:"/H5GGShare/boluomeActivityCheckVerifyCode",type:"POST",dataType:"JSON",data:{mobile:t,verifyCode:e},success:function(r){if(console.log(r),!r.success)return requestMsg(r.msg),!1;localStorage.setItem("mesg",e),window.location.href="ggForgetP?urlName="+o+"&userName="+t+"&activityId="+i+"&userItemsId="+n+"&itemsId="+l+"&word="+a}})})});
//# sourceMappingURL=../../_srcmap/activity/08/ggVerify.js.map
