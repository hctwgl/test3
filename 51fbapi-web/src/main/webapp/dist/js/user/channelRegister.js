"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),n=e.getMonth()+1;n=n<10?"0"+n:n;var a=e.getDate();return a=a<10?"0"+a:a,t+n+a+e.getHours()+e.getMinutes()+e.getSeconds()}var token=formatDateTime()+Math.random().toString(36).substr(2),style=$("#style").val(),os=getBlatFrom();console.log(os);var _fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(t,n)}(),function(e){e._tt_config=!0;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=document.location.protocol+"//s3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js",t.onerror=function(){var e=new XMLHttpRequest,n=window.encodeURIComponent(window.location.href),a=t.src,s="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+n+"&js_url="+a;e.open("GET",s,!0),e.send(null)};var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(t,n)}(window),$(function(){function e(){a--,a<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("点击获取"),clearInterval(n),a=60):$("#register_codeBtn").text(a+" s")}function t(){var t=($(this).attr("isState"),$("#register_mobile").val()),s=$("#channelCode").val(),o=$("#pointCode").val();!isNaN(t)&&/^1(3|4|5|7|8)\d{9}$/i.test(t)?($("#register_codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode4Geetest",type:"POST",dataType:"JSON",data:{mobile:t,token:token,channelCode:s,pointCode:o},success:function(t){t.success?($(".registerMask").addClass("hide"),$("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(a+" s"),n=setInterval(e,1e3)):(requestMsg(t.msg),$("#register_codeBtn").removeAttr("disabled"))},error:function(){requestMsg("网络跑丢了，请稍候重试")}})):requestMsg("请填写正确的手机号")}var n,a=60;$.ajax({url:"/fanbei-web/getGeetestCode",type:"get",dataType:"json",success:function(e){initGeetest({gt:e.gt,challenge:e.challenge,new_captcha:e.new_captcha,offline:!e.success,product:"bind"},function(n){document.getElementById("register_codeBtn").addEventListener("click",function(){var e=$("#register_mobile").val();/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/checkMobileRegistered",type:"post",data:{mobile:e},success:function(e){e=JSON.parse(e),"N"==e.data?(n.verify(),maidianFn("getCodeSuccess")):(maidianFn("getCodeRegistered"),requestMsg(e.msg))}}):requestMsg("请输入手机号")}),n.onSuccess(function(){var a=n.getValidate();$.ajax({url:"/fanbei-web/verifyGeetestCode",type:"POST",dataType:"json",data:{userId:e.userId,geetestChallenge:a.geetest_challenge,geetestValidate:a.geetest_validate,geetestSeccode:a.geetest_seccode},success:function(e){"success"===e.data.status?(maidianFn("sendCodeSuccess"),t()):"fail"===e.data.status&&(maidianFn("sendCodeFail"),requestMsg(e.msg))}})})})}}),maidianFn("channelRegister"),$("#register_submitBtn").click(function(){maidianFn("registerBtn");var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),n=e.length,a=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,s=a.test(e),o=$("#register_mobile").val(),r=$("#register_verification").val(),i=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(o)&&""!=o?""!=r?s&&6<=n&&n<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(_taq.push({convert_id:"59212981134",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:o,smsCode:r,password:t,channelCode:i,pointCode:c,token:token},success:function(e){if(e.success){maidianFn("registerSuccess");var t=navigator.userAgent.toLowerCase();1==os&&"micromessenger"!=t.match(/MicroMessenger/i)&&"qq"!=t.match(/QQ/i)&&(window.location.href="http://sftp.51fanbei.com/51fanbei_app.apk"),window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"}else maidianFn("registerFail"),requestMsg(e.msg)},error:function(){requestMsg("注册失败")}})):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")})});var tag=!1,ox=0,left=0,bgleft=0,startEvent="touchstart",moveEvent="touchmove",upEvent="touchend",totalLength=300*document.documentElement.clientWidth/375;$(".progress_btn").on(startEvent,function(e){var t,n=e.originalEvent,a=n.touches;t=a?a[0]:e,console.log(t),ox=t.pageX-left,tag=!0}),$(document).on(upEvent,function(){tag=!1}),$(document).on(moveEvent,function(e){var t,n=e.originalEvent,a=n.touches;t=a?a[0]:e,tag&&(left=t.pageX-ox,left<=0?left=0:left>totalLength&&(left=totalLength),$(".progress_btn").css("left",left),$(".progress_bar").width(left/(document.documentElement.clientWidth/750*100)+"rem"),$(".text").html("￥"+parseInt(left/totalLength*19500+500)),$("#leftMoney").html("￥"+parseInt(left/totalLength*19500+500)),$("#rightMoney").html("￥"+(.001*parseInt(left/totalLength*19500+500)).toFixed(2)))});
//# sourceMappingURL=../../_srcmap/js/user/channelRegister.js.map
