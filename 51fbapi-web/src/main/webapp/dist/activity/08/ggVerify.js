"use strict";$(function(){function t(){n--,n<=0?($(".btn").text("获取验证码"),clearInterval(e),n=60,$(".btn").attr("isState",0)):$(".btn").text(n+" s")}var e,n=60;$(".btn").click(function(){var a=$(".phoneNumber-right").val();$(".mesg-right").val();/^1(3|4|5|7|8)\d{9}$/i.test(a)?$.ajax({url:"/H5GGShare/boluomeActivityForgetPwd",type:"POST",dataType:"JSON",data:{mobile:a},success:function(a){console.log(a),a.success?($(".btn").attr("isState",1),$(".btn").text(n+" s"),e=setInterval(t,1e3)):requestMsg(a.msg)}}):requestMsg("请填写正确的手机号")}),$(".nextStep").click(function(){window.location.href="http://localhost/fanbei-web/activity/ggForgetP"})});
//# sourceMappingURL=../../_srcmap/activity/08/ggVerify.js.map
