"use strict";function toMaidian(e,t){maidianFnNew(e,channelCode,pointCode,t)}function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var n=e.getDate();return n=n<10?"0"+n:n,t+a+n+e.getHours()+e.getMinutes()+e.getSeconds()}var token=formatDateTime()+Math.random().toString(36).substr(2),style=$("#style").val(),os=getBlatFrom();console.log(style);var channelCode=getUrl("channelCode"),pointCode=getUrl("pointCode"),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(),function(e){e._tt_config=!0;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=document.location.protocol+"//s3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js",t.onerror=function(){var e=new XMLHttpRequest,a=window.encodeURIComponent(window.location.href),n=t.src,o="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+a+"&js_url="+n;e.open("GET",o,!0),e.send(null)};var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(window),$(function(){function e(){n--,n<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("点击获取"),clearInterval(a),n=60):$("#register_codeBtn").text(n+" s")}function t(){var t=($(this).attr("isState"),$("#register_mobile").val()),o=$("#channelCode").val(),s=$("#pointCode").val();!isNaN(t)&&/^1(3|4|5|7|8)\d{9}$/i.test(t)?($("#register_codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode4Geetest",type:"POST",dataType:"JSON",data:{mobile:t,token:token,channelCode:o,pointCode:s},success:function(t){t.success?($(".registerMask").addClass("hide"),$("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(n+" s"),a=setInterval(e,1e3)):(requestMsg(t.msg),$("#register_codeBtn").removeAttr("disabled"))},error:function(){requestMsg(returnData.msg)}})):requestMsg("请填写正确的手机号")}var a,n=60;$.ajax({url:"/fanbei-web/getGeetestCode",type:"get",dataType:"json",success:function(e){initGeetest({gt:e.gt,challenge:e.challenge,new_captcha:e.new_captcha,offline:!e.success,product:"bind"},function(a){document.getElementById("register_codeBtn").addEventListener("click",function(){var e=$("#register_mobile").val();/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/checkMobileRegistered",type:"post",data:{mobile:e},success:function(e){e=JSON.parse(e),"N"==e.data?(a.verify(),toMaidian("getCodeSuccess")):(toMaidian("getCodeRegistered"),requestMsg(e.msg))}}):requestMsg("请输入手机号")}),a.onSuccess(function(){var n=a.getValidate();$.ajax({url:"/fanbei-web/verifyGeetestCode",type:"POST",dataType:"json",data:{userId:e.userId,geetestChallenge:n.geetest_challenge,geetestValidate:n.geetest_validate,geetestSeccode:n.geetest_seccode},success:function(e){"success"===e.data.status?(toMaidian("sendCodeSuccess"),t()):"fail"===e.data.status&&(toMaidian("sendCodeFail"),requestMsg(e.msg))}})})})}}),toMaidian("channelRegister"),$("#register_submitBtn").click(function(){if(toMaidian("registerBtn"),"8"!=style)var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),a=e.length,n=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,o=n.test(e);else var t="";var s=$("#register_mobile").val(),r=$("#register_verification").val(),i=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(s)&&""!=s?""!=r?"8"==style||o&&6<=a&&a<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(_taq.push({convert_id:"59212981134",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:s,smsCode:r,password:t,channelCode:i,pointCode:c,token:token},success:function(e){if(e.success){toMaidian("registerSuccess",s);var t=navigator.userAgent.toLowerCase();1==os&&"micromessenger"!=t.match(/MicroMessenger/i)&&"qq"!=t.match(/QQ/i)&&(window.location.href="http://sftp.51fanbei.com/51fanbei_app.apk"),window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"}else maidianFnNew("registerFail",i,c,e.msg),requestMsg(e.msg)},error:function(){requestMsg("注册失败")}})):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")})});var tag=!1,ox=0,left=300*document.documentElement.clientWidth/375,bgleft=0,totalLength=300*document.documentElement.clientWidth/375;$(".progress_btn").on("touchstart",function(e){var t,a=e.originalEvent,n=a.touches;t=n?n[0]:e,console.log(t),ox=t.pageX-left,tag=!0}),$(document).on("touchend",function(){tag=!1}),$(document).on("touchmove",function(e){var t,a=e.originalEvent,n=a.touches;t=n?n[0]:e,tag&&(left=t.pageX-ox,left<=0?left=0:left>totalLength&&(left=totalLength),$(".progress_btn").css("left",left),$(".progress_bar").width(left/(document.documentElement.clientWidth/750*100)+"rem"),$(".text").html("￥"+parseInt(left/totalLength*19500+500)),$("#leftMoney").html("￥"+parseInt(left/totalLength*19500+500)),$("#rightMoney").html("￥"+(.001*parseInt(left/totalLength*19500+500)).toFixed(2)))});
//# sourceMappingURL=../../_srcmap/js/user/channelRegister.js.map
