"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),a=e.getMonth()+1;a=a<10?"0"+a:a;var o=e.getDate();return o=o<10?"0"+o:o,t+a+o+e.getHours()+e.getMinutes()+e.getSeconds()}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var a=e.substr(e.indexOf("?")+1,e.length),o=[];o=a.split("&");for(var n=0;n<o.length;n++)t[o[n].split("=")[0]]=unescape(o[n].split("=")[1])}return t}function step(){setTimeout(function(){$(".lineBox01").addClass("lineShow01"),$(".word01").addClass("wordShow01"),$(".lineBox02").addClass("lineShow02"),$(".word02").addClass("wordShow02"),$(".lineBox03").addClass("lineShow03"),$(".word03").addClass("wordShow03"),$(".lineBox04").addClass("lineShow04"),$(".word04").addClass("wordShow04")},500)}function bombBox(){var e=new Date;navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)?(window.location.href="com.91ala.www://home://",window.setTimeout(function(){new Date-e<5e3&&-1==location.href.indexOf("com.91ala.www://home//")?window.location="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.close()},2e3)):navigator.userAgent.match(/android/i)&&(window.location.href="myapp://jp.app/openwith??isBrowser=1",setTimeout(function(){new Date-e<5e3&&(window.location="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www")},2e3))}var token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var a=document.getElementsByTagName("script")[0];a.parentNode.insertBefore(t,a)}();var currentUrl=window.location.href,param=getUrlParam(currentUrl),timerInterval,timerS=60,userName=param.userName,typeFrom=param.typeFrom,typeFromNum=param.typeFromNum;$(function(){"Jrtt"==typeFrom||"Jmtt"==typeFrom?$(".companyWord02").show():$(".companyWord01").show()}),$(function(){function e(){timerS--,timerS<=0?($(".checkbtn").text("获取验证码"),clearInterval(timerInterval),timerS=60,$(".checkbtn").attr("isState",0)):$(".checkbtn").text(timerS+" s")}$(".clearValOne").click(function(){$("#mobile").val("")}),$(".clearValTwo").click(function(){$("#password").val("")}),$(".checkbtn").click(function(){var e=$(".mobile").val();!isNaN(e)&&/^1(3|4|5|7|8)\d{9}$/i.test(e)?$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".mask").show(),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".mask").hide(),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}}):requestMsg("请填写正确的手机号"),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFixSharejm?type=new_img&typeFrom="+typeFrom+"&typeFromNum="+typeFromNum},success:function(e){console.log(e)}})}),$("#imgVftCodeRefresh").click(function(){var e=$(".mobile").val();$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:e},success:function(e){console.log(e),$(".mask").show(),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$("#imgVftCodeClose").click(function(){$(".mask").hide(),$(".imgVftCodeWrap").addClass("hide")})},error:function(){requestMsg("请求失败")}})}),$("#imgVftCodeSbumit").click(function(){var t=$(".checkbtn").attr("isState"),a=$(".mobile").val(),o=($("#password").val(),$("#imgVftCode").val());if(0==t||!t){var n=/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(a);n?$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"json",data:{mobile:a,token:token,verifyImgCode:o},success:function(t){t.success?($(".mask").hide(),$(".imgVftCodeWrap").addClass("hide"),$(".checkbtn").attr("isState",1),$(".checkbtn").text(timerS+" s"),timerInterval=setInterval(e,1e3)):requestMsg(t.msg)},error:function(){requestMsg("请求失败")}}):(console.log(n),requestMsg("请填写正确的手机号"))}$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFixSharejm?type=new_sure&typeFrom="+typeFrom+"&typeFromNum="+typeFromNum},success:function(e){console.log(e)}})}),$(".loginbtn").click(function(){var e=$(".check").val(),t=$(".mobile").val(),a=$("#password").val(),o=$("#yzcheck").val(),n=/^1[3|4|5|7|8][0-9]{9}$/.test(t),i=/^\d{6}$/.test(o),r=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(a),s=String(CryptoJS.MD5(a));console.log(t),n&&i&&""!=o&&r&&void 0!=a?$.ajax({url:"/H5GGShare/boluomeActivityRegisterLogin",type:"post",data:{registerMobile:t,smsCode:e,password:s,token:token,inviteer:userName,activityId:"1000",typeFrom:typeFrom,typeFromNum:typeFromNum},success:function(e){console.log(0),console.log(e);var a=JSON.parse(e);a.success?(requestMsg("注册成功"),_taq.push({convert_id:"73486724679",event_type:"form"}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFixSharejm?type=new_success&typeFrom="+typeFrom+"&typeFromNum="+typeFromNum,maidianInfo1:t},success:function(e){console.log(e)}}),window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"):requestMsg(a.msg)},error:function(){requestMsg("绑定失败")}}):n?i?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的验证码"):requestMsg("请填写正确的手机号"),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFixSharejm?type=new_get&typeFrom="+typeFrom+"&typeFromNum="+typeFromNum},success:function(e){console.log(e)}})})});var vm=new Vue({el:"#ggFixSharejm",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/h5GgActivity/homePage",success:function success(data){self.content=eval("("+data+")").data,console.log(self.content),self.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut()})},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFixSharejm?type=new_ini&typeFrom="+typeFrom+"&typeFromNum="+typeFromNum},success:function(e){console.log(e)}})},ruleClick:function(){this.ruleShow=!0},closeClick:function(){this.ruleShow=!1},hasUserNameClick:function(){window.location.href="ggFixShareLogin?typeFrom="+typeFrom+"&typeFromNum="+typeFromNum}}});step();
//# sourceMappingURL=../../_srcmap/activity/11/ggFixSharejm.js.map
