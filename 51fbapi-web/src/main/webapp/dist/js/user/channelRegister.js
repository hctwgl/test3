"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),r=e.getMonth()+1;r=r<10?"0"+r:r;var s=e.getDate();return s=s<10?"0"+s:s,t+r+s+e.getHours()+e.getMinutes()+e.getSeconds()}var token=formatDateTime()+Math.random().toString(36).substr(2),style=$("#style").val(),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var r=document.getElementsByTagName("script")[0];r.parentNode.insertBefore(t,r)}(),function(e){e._tt_config=!0;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=document.location.protocol+"//s3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js",t.onerror=function(){var e=new XMLHttpRequest,r=window.encodeURIComponent(window.location.href),s=t.src;if(14==style)var o="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+r+"&js_url="+s+"&convert_id=62421367574";else var o="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+r+"&js_url="+s;e.open("GET",o,!0),e.send(null)};var r=document.getElementsByTagName("script")[0];r.parentNode.insertBefore(t,r)}(window),$(function(){function e(){r--,r<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("获取验证码"),clearInterval(t),r=60):$("#register_codeBtn").text(r+" s")}var t,r=60;$("#register_codeBtn").click(function(){var s=($(this).attr("isState"),$("#register_mobile").val());!isNaN(s)&&/^1(3|4|5|7|8)\d{9}$/i.test(s)?($("#register_codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:s,token:token},success:function(s){s.success?($("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(r+" s"),t=setInterval(e,1e3)):(requestMsg(s.msg),$("#register_codeBtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):requestMsg("请填写正确的手机号")}),$("#register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),r=e.length,s=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,o=s.test(e),n=$("#register_mobile").val(),a=$("#register_verification").val(),i=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(n)&&""!=n?""!=a?o&&6<=r&&r<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(8==style?_taq.push({convert_id:"63553308919",event_type:"form"}):14==style?_taq.push({convert_id:"62421367574",event_type:"form"}):_taq.push({convert_id:"59212981134",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:n,smsCode:a,password:t,channelCode:i,pointCode:c,token:token},success:function(e){e.success?8==style||10==style||12==style?($("#register_submitBtn").attr("disabled",!0),$(".registerSuss8").removeClass("hide"),$(".registerSuss12").removeClass("hide"),$(".registerMask").removeClass("hide"),$("#downloadApp").click(function(){window.location.href=e.url})):($("#register_submitBtn").attr("disabled",!0),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}})):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/js/user/channelRegister.js.map
