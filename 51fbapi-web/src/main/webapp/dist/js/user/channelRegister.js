"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),s=e.getMonth()+1;s=s<10?"0"+s:s;var r=e.getDate();return r=r<10?"0"+r:r,t+s+r+e.getHours()+e.getMinutes()+e.getSeconds()}var token=formatDateTime()+Math.random().toString(36).substr(2),style=$("#style").val(),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var s=document.getElementsByTagName("script")[0];s.parentNode.insertBefore(t,s)}(),$(function(){function e(){s--,s<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("获取验证码"),clearInterval(t),s=60,$("#register_codeBtn").attr("isState",0)):$("#register_codeBtn").text(s+" s")}var t,s=60;$("#register_codeBtn").click(function(){var r=$(this).attr("isState"),i=$("#register_mobile").val();0!=r&&r||isNaN(i)||!/^1(3|4|5|7|8)\d{9}$/i.test(i)?requestMsg("请填写正确的手机号"):($("#register_codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:i},success:function(r){r.success?($("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(s+" s"),t=setInterval(e,1e3)):(requestMsg(r.msg),$("#register_codeBtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}}))}),10==style?$("#register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),s=e.length,r=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/,i=r.test(e),a=$("#register_mobile").val(),n=$("#register_verification").val(),o=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(a)&&""!=a?""!=n?i&&6<=s<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(_taq.push({convert_id:"61747053456",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:a,smsCode:n,password:t,channelCode:o,pointCode:c,token:token},success:function(e){e.success?($("#register_submitBtn").attr("disabled",!0),$(".registerSuss").removeClass("hide"),$(".registerMask").removeClass("hide"),$("#downloadApp").click(function(){window.location.href=e.url})):requestMsg(e.msg)},error:function(){$(".registerFail").removeClass("hide"),$("#repeatRegister").click(function(){$(".registerMask").addClass("hide"),$(".registerFail").addClass("hide")})}})):requestMsg("请输入正确的验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入验证码"):requestMsg("请填写正确的手机号")}):12==style?$("#register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),s=e.length,r=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/,i=r.test(e),a=$("#register_mobile").val(),n=$("#register_verification").val(),o=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(a)&&""!=a?""!=n?i&&6<=s<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(_taq.push({convert_id:"61747053456",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:a,smsCode:n,password:t,channelCode:o,pointCode:c,token:token},success:function(e){e.success?($("#register_submitBtn").attr("disabled",!0),$(".registerSuss").removeClass("hide"),$(".registerMask").removeClass("hide"),$("#downloadApp12").click(function(){window.location.href=e.url})):requestMsg(e.msg)},error:function(){$(".registerFail").removeClass("hide"),$("#repeatRegister").click(function(){$(".registerMask").addClass("hide"),$(".registerFail").addClass("hide")})}})):requestMsg("请输入正确的验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入验证码"):requestMsg("请填写正确的手机号")}):$("#register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),s=e.length,r=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/,i=r.test(e),a=$("#register_mobile").val(),n=$("#register_verification").val(),o=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(a)&&""!=a?""!=n?i&&6<=s<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(_taq.push({convert_id:"59212981134",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:a,smsCode:n,password:t,channelCode:o,pointCode:c,token:token},success:function(e){e.success?($("#register_submitBtn").attr("disabled",!0),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}})):requestMsg("请输入正确的验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/js/user/channelRegister.js.map
