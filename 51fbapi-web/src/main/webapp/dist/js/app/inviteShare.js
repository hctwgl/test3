"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),r=e.getMonth()+1;r=r<10?"0"+r:r;var a=e.getDate();return a=a<10?"0"+a:a,t+r+a+e.getHours()+e.getMinutes()+e.getSeconds()}function timeFunction(){timerS--,timerS<=0?($("#codeBtn").removeAttr("disabled"),$("#codeBtn").text("获取验证码"),clearInterval(timerInterval),timerS=60):$("#codeBtn").text(timerS+" s")}var recommendCode=getUrl("recommendCode"),token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var r=document.getElementsByTagName("script")[0];r.parentNode.insertBefore(t,r)}();var timerInterval,timerS=60,vm=new Vue({el:"#inviteShare",methods:{getCode:function(){var e=($(this).attr("isState"),$("#tel").val());!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?($("#codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:e,token:token},success:function(e){e.success?(console.log(e),$("#codeBtn").attr("isState",1),$("#codeBtn").text(timerS+" s"),timerInterval=setInterval(timeFunction,1e3)):(requestMsg(e.msg),$("#codeBtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):requestMsg("请填写正确的手机号")},goRegister:function(){var e=$("#password").val().length,t=String(CryptoJS.MD5($("#password").val())),r=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/.test($("#password").val()),a=$("#tel").val(),s=$("#VerifiCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(a)&&""!=a?""!=s?r&&6<=e&&e<=18?$("#inputCheck").is(":checked")?1==$("#codeBtn").attr("isState")?$.ajax({url:"/app/user/commitRegister",type:"POST",dataType:"JSON",data:{registerMobile:a,smsCode:s,password:t,recommendCode:recommendCode,token:token},success:function(e){e.success?($("#register_submitBtn").attr("disabled",!0),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")}}});
//# sourceMappingURL=../../_srcmap/js/app/inviteShare.js.map
