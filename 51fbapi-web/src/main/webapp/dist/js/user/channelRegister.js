"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),s=e.getMonth()+1;s=s<10?"0"+s:s;var a=e.getDate();return a=a<10?"0"+a:a,t+s+a+e.getHours()+e.getMinutes()+e.getSeconds()}var token=formatDateTime()+Math.random().toString(36).substr(2),style=$("#style").val(),os=getBlatFrom();console.log(os);var _fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var s=document.getElementsByTagName("script")[0];s.parentNode.insertBefore(t,s)}(),function(e){e._tt_config=!0;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=document.location.protocol+"//s3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js",t.onerror=function(){var e=new XMLHttpRequest,s=window.encodeURIComponent(window.location.href),a=t.src,r="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+s+"&js_url="+a;e.open("GET",r,!0),e.send(null)};var s=document.getElementsByTagName("script")[0];s.parentNode.insertBefore(t,s)}(window),$(function(){function e(){a--,a<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("获取验证码"),clearInterval(s),a=60):$("#register_codeBtn").text(a+" s")}function t(){var t=($(this).attr("isState"),$("#register_mobile").val()),r=$("#channelCode").val(),n=$("#pointCode").val();!isNaN(t)&&/^1(3|4|5|7|8)\d{9}$/i.test(t)?($("#register_codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode4Geetest",type:"POST",dataType:"JSON",data:{mobile:t,token:token,channelCode:r,pointCode:n},success:function(t){t.success?($(".registerMask").addClass("hide"),$("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(a+" s"),s=setInterval(e,1e3)):(requestMsg(t.msg),$("#register_codeBtn").removeAttr("disabled"))},error:function(){requestMsg("网络跑丢了，请稍候重试")}})):requestMsg("请填写正确的手机号")}var s,a=60;$.ajax({url:"/fanbei-web/getGeetestCode",type:"get",dataType:"json",success:function(e){initGeetest({gt:e.gt,challenge:e.challenge,new_captcha:e.new_captcha,offline:!e.success,product:"bind"},function(s){document.getElementById("register_codeBtn").addEventListener("click",function(){/^1(3|4|5|7|8)\d{9}$/i.test($("#register_mobile").val())?s.verify():requestMsg("请输入手机号")}),s.onSuccess(function(){var a=s.getValidate();$.ajax({url:"/fanbei-web/verifyGeetestCode",type:"POST",dataType:"json",data:{userId:e.userId,geetestChallenge:a.geetest_challenge,geetestValidate:a.geetest_validate,geetestSeccode:a.geetest_seccode},success:function(e){"success"===e.data.status?t():"fail"===e.data.status&&requestMsg(e.msg)}})})})}}),$("#register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),s=e.length,a=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,r=a.test(e),n=$("#register_mobile").val(),o=$("#register_verification").val(),i=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(n)&&""!=n?""!=o?r&&6<=s&&s<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(_taq.push({convert_id:"59212981134",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:n,smsCode:o,password:t,channelCode:i,pointCode:c,token:token},success:function(e){if(e.success){var t=navigator.userAgent.toLowerCase();1==os&&"micromessenger"!=t.match(/MicroMessenger/i)&&"qq"!=t.match(/QQ/i)&&(window.location.href="http://sftp.51fanbei.com/51fanbei_app.apk"),window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"}else requestMsg(e.msg)},error:function(){requestMsg("注册失败")}})):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/js/user/channelRegister.js.map
