"use strict";function timeFunction(){timerS--,timerS<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("获取验证码"),clearInterval(timerInterval),timerS=60):$("#register_codeBtn").text(timerS+" s")}function goApp(){var e=new Date;$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyRegister?type=go"},success:function(e){console.log(e)}}),navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)?(window.location.href="com.91ala.www://home://",window.setTimeout(function(){new Date-e<5e3&&-1==location.href.indexOf("com.91ala.www://home//")?window.location="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.close()},2e3)):navigator.userAgent.match(/android/i)&&(window.location.href="myapp://jp.app/openwith??isBrowser=1",setTimeout(function(){new Date-e<5e3&&(window.location="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www")},2e3))}var timerInterval,timerS=60;$(".submit").click(function(){var e=$("#password").val(),t=String(CryptoJS.MD5(e)),a=e.length,i=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,s=i.test(e),o=$("#mobile").val(),n=$("#verification").val();$(".codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(o)&&""!=o?""!=n?s&&6<=a&&a<=18?$("#input_check").is(":checked")?1==$(".codeBtn").attr("isState")?$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:o,smsCode:n,password:t},success:function(e){e.success?($(".success").show(),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyRegister?type=submit"},success:function(e){console.log(e)}})):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")}),$(".codeBtn").click(function(){var e=$("#mobile").val();!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号")}),$("#imgVftCodeRefresh").click(function(){var e=$("#mobile").val();$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}})}),$("#imgVftCodeSbumit").click(function(){var e=($(this).attr("isState"),$("#mobile").val()),t=$("#channelCode").val(),a=$("#pointCode").val(),i=$("#imgVftCode").val();!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?($("#codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:e,token:token,channelCode:t,pointCode:a,verifyImgCode:i},success:function(e){e.success?($(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide"),$(".codeBtn").attr("isState",1).text(timerS+" s"),timerInterval=setInterval(timeFunction,1e3),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyRegister?type=code"},success:function(e){console.log(e)}})):(requestMsg(e.msg),$(".codeBtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):requestMsg("请填写正确的手机号")});
//# sourceMappingURL=../../_srcmap/activity/wy/wy.js.map
