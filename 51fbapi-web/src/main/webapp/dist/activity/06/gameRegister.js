"use strict";function gou(){$("#input_check").is(":checked")?$(".gou").show():$(".gou").hide()}var token=Math.random().toString(36).substr(2);!function(){var e={partner:"alading",appName:"register_professional_web",token:token},t=new Image(1,1);t.onload=function(){e.imgLoaded=!0},t.src="https://fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=register_professional_web&tokenId="+e.token;var s=document.createElement("script");s.type="text/javascript",s.async=!0,s.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(s,n)}(),$(".input").on("keydown",function(){$(this).next().hide()}),$(".input").on("keyup",function(){var e=$(this);""==e.val()&&e.next().show()}),$(function(){function e(){s--,s<=0?($(".time").hide(),clearInterval(t),s=60):($(".time").show(),$(".time1").text(s+" s"))}var t,s=60;$("#codeBtn").click(function(){var n=$("#phone").val();/^1(3|4|5|7|8)\d{9}$/i.test(n)?$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:n},success:function(n){n.success?($(".time").show(),$(".time1").text(s+"s"),t=setInterval(e,1e3)):requestMsg(n.msg)},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号")}),$("#submit").click(function(){var e=getUrl("recommendCode"),t=$("#phone").val(),s=$("#code").val(),n=$("#password").val(),o=String(CryptoJS.MD5(n)),r=n.length,a=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/,i=a.test(n);/^1(3|4|5|7|8)\d{9}$/i.test(t)?""!=s?i&&6<=r<=18?$("#input_check").is(":checked")?$.ajax({url:"/app/user/commitRegister",type:"POST",dataType:"JSON",data:{registerMobile:t,smsCode:s,password:o,recommendCode:e,token:token},success:function(e){e.success?($("#submit").off("click"),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/activity/06/gameRegister.js.map
