"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var s=e.getDate();return s=s<10?"0"+s:s,t+a+s+e.getHours()+e.getMinutes()+e.getSeconds()}function maidianFun(e){maidianFnNew(e,spread)}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var a=e.substr(e.indexOf("?")+1,e.length),s=[];s=a.split("&");for(var n=0;n<s.length;n++)t[s[n].split("=")[0]]=unescape(s[n].split("=")[1])}return t}var token=formatDateTime()+Math.random().toString(36).substr(2),spread=getUrl("spread"),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(),$(function(){function e(){r--,r<=0?($(".checkbtn").removeAttr("disabled"),$(".checkbtn").text("获取验证码"),clearInterval(i),r=60,$(".checkbtn").attr("isState",0)):$(".checkbtn").text(r+" s")}function t(){var t=$(".checkbtn").attr("isState"),a=$(".mobile").val();$("#password").val(),$("#imgVftCode").val();if(0==t||!t){var s=/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(a);s?($(".checkbtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode4Geetest",type:"POST",dataType:"json",data:{mobile:a,token:token},success:function(t){t.success?($(".checkbtn").attr("isState",1),$(".checkbtn").text(r+" s"),i=setInterval(e,1e3),requestMsg("验证码已发送")):(requestMsg(t.msg),$(".checkbtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):(console.log(s),requestMsg("请填写正确的手机号"))}}var a=window.location.href,s=getUrlParam(a),n=(s.word,s.urlName),o=(s.userName,s.userName),c=s.activityId;s.userItemsId,s.itemsId,s.loginSource;console.log(c),console.log(a);var i,r=60;$(".yhicon").click(function(){$(".yhinp").val(""),$(".yhicon").css("display","none")}),$(".yhinp").keyup(function(){""==$(".yhinp").val()?$(".yhicon").css("display","none"):$(".yhicon").css("display","block")}),$(".big-one").click(function(){$("#mobile").val("")}),$(".big-two").click(function(){$("#password").val("")}),$.ajax({url:"/fanbei-web/getGeetestCode",type:"get",dataType:"json",success:function(e){initGeetest({gt:e.gt,challenge:e.challenge,new_captcha:e.new_captcha,offline:!e.success,product:"bind"},function(a){document.getElementById("checkbtn").addEventListener("click",function(){var e=$("#mobile").val();/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/checkMobileRegistered",type:"post",data:{mobile:e},success:function(e){"N"==e.data?(a.verify(),maidianFun("getCodeSuccess")):(maidianFun("getCodeRegistered"),"用户已存在"==e.msg?requestMsg("您已注册，请直接登录"):requestMsg(e.msg))}}):requestMsg("请输入手机号")}),a.onSuccess(function(){var s=a.getValidate();$.ajax({url:"/fanbei-web/verifyGeetestCode",type:"POST",dataType:"json",data:{userId:e.userId,geetestChallenge:s.geetest_challenge,geetestValidate:s.geetest_validate,geetestSeccode:s.geetest_seccode},success:function(e){"success"===e.data.status?(maidianFun("sendCodeSuccess"),t()):"fail"===e.data.status&&(maidianFun("sendCodeFail"),requestMsg(e.msg))}})})})}}),maidianFun("enter"),$(".loginbtn").click(function(){var e=$(".check").val(),t=$(".mobile").val(),a=$("#password").val();console.log(a),console.log(e),console.log(t);var i=$("#yzcheck").val(),r=/^1[3|4|5|7|8][0-9]{9}$/.test(t),l=/^\d{6}$/.test(i),u=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(a);if(r&&l&&""!=i&&u&&void 0!=a){var d=String(CryptoJS.MD5(a));$.ajax({url:"/H5GGShare/commitBouomeActivityRegister",type:"post",data:{registerMobile:t,smsCode:e,password:d,urlName:n,token:token,activityId:c,refUserName:o},success:function(e){console.log(e);var t=JSON.parse(e);if(console.log(t),t.success){maidianFun("registerSuccess");s.urlName;requestMsg("注册成功"),setTimeout(function(){window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"},1500)}else"Register"==t.url&&(maidianFnNew("registerFail",t.msg),requestMsg(t.msg))},error:function(){requestMsg("绑定失败")}})}else r?l?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/activity/12/doubleEggRegister.js.map
