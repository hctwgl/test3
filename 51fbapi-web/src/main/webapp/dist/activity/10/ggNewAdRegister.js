"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var s=e.getDate();return s=s<10?"0"+s:s,t+a+s+e.getHours()+e.getMinutes()+e.getSeconds()}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var a=e.substr(e.indexOf("?")+1,e.length),s=[];s=a.split("&");for(var o=0;o<s.length;o++)t[s[o].split("=")[0]]=unescape(s[o].split("=")[1])}return t}var token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(),$(function(){function e(){r--,r<=0?($(".checkbtn").text("获取验证码"),clearInterval(t),r=60,$(".checkbtn").attr("isState",0)):$(".checkbtn").text(r+" s")}var t,a=window.location.href,s=getUrlParam(a),o=(s.urlName,s.activityId),r=60,i=s.typeFrom,n=s.typeFromNum;$(".clearValOne").click(function(){$("#mobile").val("")}),$(".clearValTwo").click(function(){$("#password").val("")}),$(".checkbtn").click(function(){var e=$(".mobile").val();!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号")}),$("#imgVftCodeRefresh").click(function(){var e=$(".mobile").val();$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}})}),$("#imgVftCodeSbumit").click(function(){var a=$(".checkbtn").attr("isState"),s=$(".mobile").val(),o=($("#password").val(),$("#imgVftCode").val());if(0==a||!a){var i=/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(s);i?$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"json",data:{mobile:s,token:token,verifyImgCode:o},success:function(a){a.success?($(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide"),$(".checkbtn").attr("isState",1),$(".checkbtn").text(r+" s"),t=setInterval(e,1e3)):requestMsg(a.msg)},error:function(){requestMsg("请求失败")}}):(console.log(i),requestMsg("请填写正确的手机号"))}}),$(".loginbtn").click(function(){var e=$(".check").val(),t=$(".mobile").val(),a=$("#password").val(),s=$("#yzcheck").val(),r=/^1[3|4|5|7|8][0-9]{9}$/.test(t),c=/^\d{6}$/.test(s),l=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(a),d=String(CryptoJS.MD5(a));console.log(t),r&&c&&""!=s&&l&&void 0!=a?$.ajax({url:"/H5GGShare/boluomeActivityRegisterLogin",type:"post",data:{registerMobile:t,smsCode:e,password:d,token:token,activityId:o,typeFrom:i,typeFromNum:n},success:function(e){console.log(0),console.log(e);var t=JSON.parse(e);t.success?(requestMsg("注册成功"),window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"):requestMsg(t.msg)},error:function(){requestMsg("绑定失败")}}):r?c?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/activity/10/ggNewAdRegister.js.map
