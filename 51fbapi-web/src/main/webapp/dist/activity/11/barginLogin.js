"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),n=e.getMonth()+1;n=n<10?"0"+n:n;var o=e.getDate();return o=o<10?"0"+o:o,t+n+o+e.getHours()+e.getMinutes()+e.getSeconds()}function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var n=e.substr(e.indexOf("?")+1,e.length),o=[];o=n.split("&");for(var a=0;a<o.length;a++)t[o[a].split("=")[0]]=unescape(o[a].split("=")[1])}return t}var token=formatDateTime()+Math.random().toString(36).substr(2),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(t,n)}(),$(function(){var e=window.location.href,t=getUrlParam(e),n=(t.word,t.urlName),o=t.userName,a=t.activityId;t.userItemsId,t.itemsId,t.loginSource;console.log(t),$(".yhicon").click(function(){$("#yhinp").val(""),$(".yhicon").css("display","none")}),$("#yhinp").keyup(function(){""==$("#yhinp").val()?$(".yhicon").css("display","none"):$(".yhicon").css("display","block")}),$(".mmicon").click(function(){$(".check").val(""),$(".mmicon").css("display","none")}),$(".check").keyup(function(){""==$(".check").val()?$(".mmicon").css("display","none"):$(".mmicon").css("display","block")}),$(".loginbtn").click(function(){var e=$("#yhinp").val(),t=$(".check").val(),i=/^1[3|4|5|7|8][0-9]{9}$/.test(e);if(i&&/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(t)){var r=String(CryptoJS.MD5(t));$.ajax({url:"/H5GGShare/boluomeActivityLogin",type:"POST",dataType:"JSON",data:{userName:e,password:r,activityId:a,refUserName:o,urlName:n,token:token},success:function(t){if(console.log(">>>>",t),alert(e),console.log(t),t.success)return window.location.href="./barginIndex?userName="+e,!1;"Login"==t.url?requestMsg(t.msg):"DownLoad"==t.url&&(requestMsg(t.msg),setTimeout(function(){window.location.href="barginRegister?userName="+o},1500))}})}else console.log(i+"000"),i?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的手机号")}),$("#gg_register").click(function(){window.location.href="barginRegister?userName="+o}),$("#gg_forget").click(function(){window.location.href="barginVerify?userName="+o})});
//# sourceMappingURL=../../_srcmap/activity/11/barginLogin.js.map
