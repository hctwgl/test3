"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var o=e.getDate();return o=o<10?"0"+o:o,t+a+o+e.getHours()+e.getMinutes()+e.getSeconds()}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var a=e.substr(e.indexOf("?")+1,e.length),o=[];o=a.split("&");for(var s=0;s<o.length;s++)t[o[s].split("=")[0]]=unescape(o[s].split("=")[1])}return t}var token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}(),$(function(){function e(){n--,n<=0?($(".checkbtn").text("获取验证码"),clearInterval(t),n=60,$(".checkbtn").attr("isState",0)):$(".checkbtn").text(n+" s")}var t,a=window.location.href,o=getUrlParam(a),s=(o.urlName,o.activityId),n=60,i=o.typeFrom,r=o.typeFromNum;$(".clearValOne").click(function(){$("#mobile").val("")}),$(".clearValTwo").click(function(){$("#password").val("")}),$(".checkbtn").click(function(){var e=$(".mobile").val();!isNaN(e)&&/^1(3|4|5|6|7|8|9)\d{9}$/i.test(e)?$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".mask").show(),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".mask").hide(),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号"),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggNewAdjr?activityId=1&type=new_img&typeFrom="+i+"&typeFromNum="+r},success:function(e){console.log(e)}})}),$("#imgVftCodeRefresh").click(function(){var e=$(".mobile").val();$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".mask").show(),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".mask").hide(),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}})}),$("#imgVftCodeSbumit").click(function(){var a=$(".checkbtn").attr("isState"),o=$(".mobile").val(),s=($("#password").val(),$("#imgVftCode").val());if(0==a||!a){var c=/^1[3|4|5|6|7|8|9][0-9]\d{4,8}$/.test(o);c?$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"json",data:{mobile:o,token:token,verifyImgCode:s},success:function(a){a.success?($(".mask").hide(),$(".imgVftCodeWrap").addClass("hide"),$(".checkbtn").attr("isState",1),$(".checkbtn").text(n+" s"),t=setInterval(e,1e3)):requestMsg(a.msg)},error:function(){requestMsg("请求失败")}}):(console.log(c),requestMsg("请填写正确的手机号"))}$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggNewAdjr?activityId=1&type=new_sure&typeFrom="+i+"&typeFromNum="+r},success:function(e){console.log(e)}})}),$(".loginbtn").click(function(){var e=$(".check").val(),t=$(".mobile").val(),a=$("#password").val(),o=$("#yzcheck").val(),n=/^1[3|4|5|6|7|8|9][0-9]{9}$/.test(t),c=/^\d{6}$/.test(o),l=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(a),d=String(CryptoJS.MD5(a));console.log(t),n&&c&&""!=o&&l&&void 0!=a?$.ajax({url:"/H5GGShare/boluomeActivityRegisterLogin",type:"post",data:{registerMobile:t,smsCode:e,password:d,token:token,activityId:s,typeFrom:i,typeFromNum:r},success:function(e){console.log(0),console.log(e);var t=JSON.parse(e);t.success?(requestMsg("注册成功"),_taq.push({convert_id:"72607366923",event_type:"form"}),window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"):requestMsg(t.msg)},error:function(){requestMsg("绑定失败")}}):n?c?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的验证码"):requestMsg("请填写正确的手机号"),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggNewAdjr?activityId=1&type=new_get&typeFrom="+i+"&typeFromNum="+r},success:function(e){console.log(e)}})})});
//# sourceMappingURL=../../_srcmap/activity/10/ggNewAdRegisterjr.js.map
