"use strict";function timeFunction(){timerS--,timerS<=0?($("#codeBtn").removeAttr("disabled"),$("#codeBtn").text("获取验证码"),clearInterval(timerInterval),timerS=60):$("#codeBtn").text(timerS+" s")}var recommendCode=getUrl("recommendCode"),timerInterval,timerS=60,vm=new Vue({el:"#inviteShare",methods:{getCode:function(){console.log(222);var e=($(this).attr("isState"),$("#tel").val());!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?($("#codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:e},beforeSend:function(){$("#codeBtn").attr("isState",1),$("#codeBtn").text(timerS+" s"),timerInterval=setInterval(timeFunction,1e3)},success:function(e){console.log(e),e.success?console.log(e):(requestMsg(e.msg),$("#codeBtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):requestMsg("请填写正确的手机号")},goRegister:function(){var e=$("#password").val().length,t=String(CryptoJS.MD5($("#password").val())),s=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/.test($("#password").val()),r=$("#tel").val(),o=$("#VerifiCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(r)&&""!=r?""!=o?s&&6<=e&&e<=18?$("#inputCheck").is(":checked")?1==$("#codeBtn").attr("isState")?$.ajax({url:"/app/user/commitRegister",type:"POST",dataType:"JSON",data:{registerMobile:r,smsCode:o,password:t,recommendCode:recommendCode},success:function(e){e.success?($("#register_submitBtn").attr("disabled",!0),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")}}});
//# sourceMappingURL=../../_srcmap/js/app/inviteShare.js.map
