"use strict";function formatDateTime(){var t=new Date,e=t.getFullYear(),n=t.getMonth()+1;n=n<10?"0"+n:n;var o=t.getDate();return o=o<10?"0"+o:o,e+n+o+t.getHours()+t.getMinutes()+t.getSeconds()}function getUrlParam(t){var e=new Object;if(-1!=t.indexOf("?")){var n=t.substr(t.indexOf("?")+1,t.length),n=t.substr(t.indexOf("?")+1,t.length),o=[];o=n.split("&");for(var a=0;a<o.length;a++)e[o[a].split("=")[0]]=unescape(o[a].split("=")[1])}return e}var token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var t=new Image(1,1);t.onload=function(){_fmOpt.imgLoaded=!0},t.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var e=document.createElement("script");e.type="text/javascript",e.async=!0,e.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(e,n)}(),$(function(){var t=window.location.href,e=getUrlParam(t),n=e.urlName,o=e.activityId,a=e.typeFrom,i=e.typeFromNum;console.log(n),$(".yhicon").click(function(){$("#yhinp").val(""),$(".yhicon").css("display","none")}),$("#yhinp").keyup(function(){""==$("#yhinp").val()?$(".yhicon").css("display","none"):$(".yhicon").css("display","block")}),$(".mmicon").click(function(){$(".check").val(""),$(".mmicon").css("display","none")}),$(".check").keyup(function(){""==$(".check").val()?$(".mmicon").css("display","none"):$(".mmicon").css("display","block")}),$(".loginbtn").click(function(){var t=$("#yhinp").val(),e=$(".check").val(),c=/^1[3|4|5|7|8][0-9]{9}$/.test(t);if(c&&/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(e)){var r=String(CryptoJS.MD5(e));$.ajax({url:"/H5GGShare/boluomeActivityLogin",type:"POST",dataType:"JSON",data:{userName:t,password:r,activityId:o,urlName:n,token:token},success:function(t){console.log(t),t.success?window.location.href=n+"?activityId="+o+"&typeFrom="+a+"&typeFromNum="+i:"DownLoad"==t.url&&(requestMsg(t.msg),setTimeout(function(){window.location.href="ggadregister?activityId="+o+"&urlName="+n+"&typeFrom="+a+"&typeFromNum="+i},1500))}})}else console.log(c+"000"),c?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的手机号")}),$("#gg_forget").click(function(){window.location.href="ggADVerify?activityId="+o+"&urlName="+n+"&typeFrom="+a+"&typeFromNum="+i})});
//# sourceMappingURL=../../_srcmap/activity/10/ggADLogin.js.map
