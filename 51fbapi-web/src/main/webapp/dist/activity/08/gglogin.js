"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var n=e.substr(e.indexOf("?")+1,e.length),i=[];i=n.split("&");for(var a=0;a<i.length;a++)t[i[a].split("=")[0]]=unescape(i[a].split("=")[1])}return t}$(function(){var e=window.location.href,t=getUrlParam(e),n=t.word,i=t.urlName,a=t.userName,o=t.activityId,r=t.userItemsId,s=t.itemsId,c=t.loginSource;console.log(t);var l,u=function(){var e=new Date,t=e.getFullYear(),n=e.getMonth()+1;n=n<10?"0"+n:n;var i=e.getDate();return i=i<10?"0"+i:i,t+n+i+e.getHours()+e.getMinutes()+e.getSeconds()}()+Math.random().toString(36).substr(2);!function(){l={partner:"alading",appName:"alading_web",token:u};var e=new Image(1,1);e.onload=function(){l.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+l.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(t,n)}(),$(".yhicon").click(function(){$("#yhinp").val(""),$(".yhicon").css("display","none")}),$("#yhinp").keyup(function(){""==$("#yhinp").val()?$(".yhicon").css("display","none"):$(".yhicon").css("display","block")}),$(".mmicon").click(function(){$(".check").val(""),$(".mmicon").css("display","none")}),$(".check").keyup(function(){""==$(".check").val()?$(".mmicon").css("display","none"):$(".mmicon").css("display","block")}),$(".loginbtn").click(function(){var e=$("#yhinp").val(),t=$(".check").val(),l=/^1[3|4|5|7|8][0-9]{9}$/.test(e);if(l&&/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(t)){var d=String(CryptoJS.MD5(t));$.ajax({url:"/H5GGShare/boluomeActivityLogin",type:"POST",dataType:"JSON",data:{userName:e,password:d,activityId:o,refUserName:a,urlName:i,token:u},success:function(e){console.log(e),e.success?window.location.href="Z"==n?i+"?userName="+a+"&activityId="+o+"&userItemsId="+r:"S"==n?i+"?userName="+a+"&itemsId="+s+"&activityId="+o:i+"?activityId="+o+"&userName="+a+"&word="+c+"&urlName="+i:"Login"==e.url?requestMsg(e.msg):"DownLoad"==e.url&&(requestMsg(e.msg),setTimeout(function(){window.location.href="ggregister?word="+n+"&userName="+a+"&activityId="+o+"&userItemsId="+r+"&itemsId="+s+"&urlName="+i},1500))}})}else console.log(l+"000"),l?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的手机号")}),$("#gg_register").click(function(){window.location.href="ggregister?word="+n+"&userName="+a+"&activityId="+o+"&userItemsId="+r+"&itemsId="+s+"&urlName="+i+"&loginSource="+c}),$("#gg_forget").click(function(){window.location.href="ggVerify?word="+n+"&userName="+a+"&activityId="+o+"&userItemsId="+r+"&itemsId="+s+"&urlName="+i})});
//# sourceMappingURL=../../_srcmap/activity/08/gglogin.js.map
