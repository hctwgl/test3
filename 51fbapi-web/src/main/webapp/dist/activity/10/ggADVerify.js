"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var r=e.getDate();return r=r<10?"0"+r:r,t+a+r+e.getHours()+e.getMinutes()+e.getSeconds()}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var a=e.substr(e.indexOf("?")+1,e.length),r=[];r=a.split("&");for(var o=0;o<r.length;o++)t[r[o].split("=")[0]]=unescape(r[o].split("=")[1])}return t}var token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(),$(function(){function e(){c--,c<=0?($(".btn").text("获取验证码"),clearInterval(t),c=60,$(".btn").attr("isState",0)):$(".btn").text(c+" s")}var t,a=window.location.href,r=getUrlParam(a),o=r.urlName,s=r.activityId,i=r.typeFrom,n=r.typeFromNum,c=60;$(".btn").click(function(){var e=$(".phoneNumber-right").val();!isNaN(e)&&/^1(3|4|5|6|7|8|9)\d{9}$/i.test(e)?$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号")}),$("#imgVftCodeRefresh").click(function(){var e=$(".phoneNumber-right").val();$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}})}),$("#imgVftCodeSbumit").click(function(){var a=$(".phoneNumber-right").val(),r=/^1[3|4|5|6|7|8|9][0-9]\d{4,8}$/.test(a),o=$(".btn").attr("isState"),s=$("#imgVftCode").val();0!=o&&o||(r?$.ajax({url:"/H5GGShare/boluomeActivityForgetPwd",type:"POST",dataType:"JSON",data:{mobile:a,token:token,verifyImgCode:s},success:function(a){console.log(a),a.success?($(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide"),$(".btn").attr("isState",1),$(".btn").text(c+" s"),t=setInterval(e,1e3)):requestMsg(a.msg)},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号"),localStorage.setItem("user",a),console.log(a))}),$(".nextStep").click(function(){var e=$(".mesg-right").val(),t=$(".phoneNumber-right").val();$.ajax({url:"/H5GGShare/boluomeActivityCheckVerifyCode",type:"POST",dataType:"JSON",data:{mobile:t,verifyCode:e,token:token,typeFrom:i,typeFromNum:n},success:function(t){if("ForgetPwd"==t.url&&requestMsg(t.msg),console.log(t),!t.success)return requestMsg(t.msg),!1;localStorage.setItem("mesg",e),window.location.href="ggADForgetP?urlName="+o+"&activityId="+s+"&typeFrom="+i+"&typeFromNum="+n},error:function(){requestMsg("请求失败")}})})});
//# sourceMappingURL=../../_srcmap/activity/10/ggADVerify.js.map
