"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),n=e.getMonth()+1;n=n<10?"0"+n:n;var o=e.getDate();return o=o<10?"0"+o:o,t+n+o+e.getHours()+e.getMinutes()+e.getSeconds()}function gou(){$("#input_check").is(":checked")?$(".gou").show():$(".gou").hide()}var token=formatDateTime()+Math.random().toString(36).substr(2);!function(){var e={partner:"alading",appName:"alading_web",token:token},t=new Image(1,1);t.onload=function(){e.imgLoaded=!0},t.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+e.token;var n=document.createElement("script");n.type="text/javascript",n.async=!0,n.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var o=document.getElementsByTagName("script")[0];o.parentNode.insertBefore(n,o)}(),$(".input").on("keydown",function(){$(this).next().hide()}),$(".input").on("keyup",function(){var e=$(this);""==e.val()&&e.next().show()}),$(function(){function e(){n--,n<=0?($(".time").hide(),clearInterval(t),n=60):($(".time").show(),$(".time1").text(n+" s"))}var t,n=60;$("#codeBtn").click(function(){var o=$("#phone").val();/^1(3|4|5|7|8)\d{9}$/i.test(o)?$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:o},success:function(o){o.success?($(".time").show(),$(".time1").text(n+"s"),t=setInterval(e,1e3)):requestMsg(o.msg)},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号")}),$("#submit").click(function(){var e=getUrl("recommendCode"),t=$("#phone").val(),n=$("#code").val(),o=$("#password").val(),s=String(CryptoJS.MD5(o)),a=o.length,r=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/,i=r.test(o);/^1(3|4|5|7|8)\d{9}$/i.test(t)?""!=n?i&&6<=a<=18?$("#input_check").is(":checked")?$.ajax({url:"/app/user/commitRegister",type:"POST",dataType:"JSON",data:{registerMobile:t,smsCode:n,password:s,recommendCode:e,token:token},success:function(e){e.success?($("#submit").off("click"),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/activity/06/gameRegister.js.map
