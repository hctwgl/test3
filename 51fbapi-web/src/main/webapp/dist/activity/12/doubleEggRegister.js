"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),s=e.getMonth()+1;s=s<10?"0"+s:s;var a=e.getDate();return a=a<10?"0"+a:a,t+s+a+e.getHours()+e.getMinutes()+e.getSeconds()}function maidianFun(e){maidianFnNew(e,spread)}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var s=e.substr(e.indexOf("?")+1,e.length),a=[];a=s.split("&");for(var n=0;n<a.length;n++)t[a[n].split("=")[0]]=unescape(a[n].split("=")[1])}return t}function toLogin(){maidianFun("toLogin"),location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"}var token=formatDateTime()+Math.random().toString(36).substr(2),spread=getUrl("spread");$(function(){function e(){r--,r<=0?($(".checkbtn").removeAttr("disabled"),$(".checkbtn").text("获取验证码"),clearInterval(c),r=60,$(".checkbtn").attr("isState",0)):$(".checkbtn").text(r+" s")}function t(){var t=$(".checkbtn").attr("isState"),s=$(".mobile").val();$("#password").val(),$("#imgVftCode").val();if(0==t||!t){var a=/^1[3|4|5|6|7|8|9][0-9]\d{4,8}$/.test(s);a?($(".checkbtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode4Geetest",type:"POST",dataType:"json",data:{mobile:s,token:token,bsqToken:token},success:function(t){t.success?($(".checkbtn").attr("isState",1),$(".checkbtn").text(r+" s"),c=setInterval(e,1e3),requestMsg("验证码已发送")):(requestMsg(t.msg),$(".checkbtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):(console.log(a),requestMsg("请填写正确的手机号"))}}var s=window.location.href,a=getUrlParam(s),n=(a.word,a.urlName),i=(a.userName,a.userName),o=a.activityId;a.userItemsId,a.itemsId,a.loginSource;console.log(o),console.log(s);var c,r=60;$(".yhicon").click(function(){$(".yhinp").val(""),$(".yhicon").css("display","none")}),$(".yhinp").keyup(function(){""==$(".yhinp").val()?$(".yhicon").css("display","none"):$(".yhicon").css("display","block")}),$(".big-one").click(function(){$("#mobile").val("")}),$(".big-two").click(function(){$("#password").val("")}),$.ajax({url:"/fanbei-web/getGeetestCode",type:"get",dataType:"json",success:function(e){initGeetest({gt:e.gt,challenge:e.challenge,new_captcha:e.new_captcha,offline:!e.success,product:"bind"},function(s){document.getElementById("checkbtn").addEventListener("click",function(){var e=$("#mobile").val();/^1(3|4|5|6|7|8|9)\d{9}$/i.test(e)?$.ajax({url:"/app/user/checkMobileRegistered",type:"post",data:{mobile:e},success:function(e){"N"==e.data?(s.verify(),maidianFun("getCodeSuccess")):(maidianFun("getCodeRegistered"),"用户已存在"==e.msg?requestMsg("您已注册，请直接登录"):requestMsg(e.msg))}}):requestMsg("请输入手机号")}),s.onSuccess(function(){var a=s.getValidate();$.ajax({url:"/fanbei-web/verifyGeetestCode",type:"POST",dataType:"json",data:{userId:e.userId,geetestChallenge:a.geetest_challenge,geetestValidate:a.geetest_validate,geetestSeccode:a.geetest_seccode},success:function(e){"success"===e.data.status?(maidianFun("sendCodeSuccess"),t()):"fail"===e.data.status&&(maidianFun("sendCodeFail"),requestMsg(e.msg))}})})})}}),maidianFun("enter"),$(".loginbtn").click(function(){maidianFun("registerBtn");var e=$(".check").val(),t=$(".mobile").val(),s=$("#password").val();console.log(s),console.log(e),console.log(t);var c=$("#yzcheck").val(),r=/^1[3|4|5|6|7|8|9][0-9]{9}$/.test(t),l=/^\d{6}$/.test(c),u=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(s);if(r&&l&&""!=c&&u&&void 0!=s){var d=String(CryptoJS.MD5(s));$.ajax({url:"/H5GGShare/commitBouomeActivityRegister",type:"post",data:{registerMobile:t,smsCode:e,password:d,urlName:n,token:token,bsqToken:token,activityId:o,refUserName:i},success:function(e){console.log(e);var t=e;if(console.log(t),t.success){maidianFun("registerSuccess");a.urlName;requestMsg("注册成功"),setTimeout(function(){window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"},1500)}else"Register"==t.url&&(maidianFnNew("registerFail",t.msg),requestMsg(t.msg))},error:function(){requestMsg("绑定失败")}})}else r?l?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/activity/12/doubleEggRegister.js.map
