"use strict";function changeBtn(){var e=$("#register_mobile").val(),t=$("#register_verification").val(),r=$("#register_password").val();""==e?$(".register_mobileIcon").addClass("registerIcon_hide"):$(".register_mobileIcon").removeClass("registerIcon_hide"),""==t?$(".register_verificationIcon").addClass("registerIcon_hide"):$(".register_verificationIcon").removeClass("registerIcon_hide"),""==r?$(".register_passwordIcon").addClass("registerIcon_hide"):$(".register_passwordIcon").removeClass("registerIcon_hide"),""!=e&&""!=t&&""!=r?($(".register_submitBtn").removeClass("btnc_cf"),$(".register_submitBtn").attr("disabled",!1)):($(".register_submitBtn").attr("disabled",!0),$(".register_submitBtn").addClass("btnc_cf"))}$(function(){$(".register_mobileIcon").click(function(){$(".register_mobile").val(""),$(".register_mobileIcon").addClass("registerIcon_hide")}),$(".register_verificationIcon").click(function(){$(".register_verification").val(""),$(".register_verificationIcon").addClass("registerIcon_hide")})}),$(function(){function e(){r--,r<=0?($("#register_codeBtn").text("获取验证码"),clearInterval(t),r=60,$("#register_codeBtn").attr("isState",0)):$("#register_codeBtn").text(r+" s")}var t,r=60;$("#register_codeBtn").click(function(){var s=$(this).attr("isState"),i=$("#register_mobile").val();0!=s&&s||isNaN(i)?requestMsg("请填写正确的手机号"):$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:i},success:function(s){s.success?($("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(r+" s"),t=setInterval(e,1e3)):requestMsg(s.msg)},error:function(){requestMsg("请求失败")}})}),$(".register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),r=e.length,s=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/,i=s.test(e),a=getUrl("recommendCode"),o=$("#register_mobile").val(),c=$("#register_verification").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|6|7|8|9)\d{9}$/i.test(o)&&""!=o?""!=c?i&&6<=r<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?$.ajax({url:"/app/user/commitRegister",type:"POST",dataType:"JSON",data:{registerMobile:o,smsCode:c,password:t,recommendCode:a},success:function(e){e.success?($("#register_submitBtn").attr("disabled",!0),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请输入验证码"):requestMsg("请阅读并同意《爱上街用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入验证码"):requestMsg("请填写正确的手机号")})}),$(function(){$(".register_passwordEyeClosed").click(function(){"password"==$(".register_password").attr("type")?($(".register_password").attr("type","text"),$(this).addClass("register_passwordEye")):($(".register_password").attr("type","password"),$(this).removeClass("register_passwordEye"))})});
//# sourceMappingURL=../../_srcmap/js/user/register.js.map
