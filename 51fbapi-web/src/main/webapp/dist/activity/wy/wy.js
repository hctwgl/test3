"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var o=e.getDate();return o=o<10?"0"+o:o,t+a+o+e.getHours()+e.getMinutes()+e.getSeconds()}function timeFunction(){timerS--,timerS<=0?($(".codeBtn").removeAttr("disabled"),$(".codeBtn").text("点击获取"),clearInterval(timerInterval),timerS=60):$(".codeBtn").text(timerS+" s")}function goApp(){var e=new Date;$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyRegister?type=go"},success:function(e){console.log(e)}}),navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)?(window.location.href="com.91ala.www://home://",window.setTimeout(function(){new Date-e<5e3&&-1==location.href.indexOf("com.91ala.www://home//")?window.location="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.close()},2e3)):navigator.userAgent.match(/android/i)&&(window.location.href="myapp://jp.app/openwith??isBrowser=1",setTimeout(function(){new Date-e<5e3&&(window.location="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www")},2e3))}var timerInterval,timerS=60,channelCode=getUrl("channelCode"),pointCode=getUrl("pointCode"),token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(),$(".submit").click(function(){var e=$("#password").val(),t=String(CryptoJS.MD5(e)),a=e.length,o=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,i=o.test(e),s=$("#mobile").val(),n=$("#verification").val();$(".codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(s)&&""!=s?""!=n?i&&6<=a&&a<=18?$("#input_check").is(":checked")?1==$(".codeBtn").attr("isState")?$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:s,channelCode:channelCode,pointCode:pointCode,smsCode:n,password:t},success:function(e){e.success?($(".success").show(),$(".first").hide(),$("#more").hide(),$(".sPic").show(),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyRegister?type=submit"},success:function(e){console.log(e)}})):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}}):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")}),$("img.lazy").lazyload({placeholder:"https://img.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200}),$(".codeBtn").click(function(){var e=$("#mobile").val();!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".shadow").show(),$("#imgVftCode").val(""),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".shadow").hide(),$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("哎呀，出错了！")}}):requestMsg("请填写正确的手机号")}),$("#imgVftCodeRefresh").click(function(){var e=$("#mobile").val();$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("哎呀，出错了！")}})}),$("#imgVftCodeSbumit").click(function(){var e=($(this).attr("isState"),$("#mobile").val()),t=$("#imgVftCode").val();!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?($("#codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:e,token:token,channelCode:channelCode,pointCode:pointCode,verifyImgCode:t},success:function(e){$(".shadow").hide(),$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide"),e.success?($(".codeBtn").attr("isState",1).text(timerS+" s"),timerInterval=setInterval(timeFunction,1e3),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyRegister?type=code"},success:function(e){console.log(e)}})):(requestMsg(e.msg),$(".codeBtn").removeAttr("disabled"))},error:function(){requestMsg("哎呀，出错了！")}})):requestMsg("请填写正确的手机号")});
//# sourceMappingURL=../../_srcmap/activity/wy/wy.js.map
