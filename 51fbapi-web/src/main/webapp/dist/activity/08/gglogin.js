"use strict";function getUrlParam(e){var i=new Object;if(-1!=e.indexOf("?")){var s=e.substr(e.indexOf("?")+1,e.length),c=[];c=s.split("&");for(var t=0;t<c.length;t++)i[c[t].split("=")[0]]=unescape(c[t].split("=")[1])}return i}$(function(){var e=window.location.href,i=getUrlParam(e),s=i.word,c=i.urlName,t=i.userName,n=i.activityId,a=i.userItemsId,r=i.itemsId;$(".yhicon").click(function(){$("#yhinp").val(""),$(".yhicon").css("display","none")}),$("#yhinp").keyup(function(){""==$("#yhinp").val()?$(".yhicon").css("display","none"):$(".yhicon").css("display","block")}),$(".mmicon").click(function(){$(".check").val(""),$(".mmicon").css("display","none")}),$(".check").keyup(function(){""==$(".check").val()?$(".mmicon").css("display","none"):$(".mmicon").css("display","block")}),$(".loginbtn").click(function(){var e=$("#yhinp").val(),i=$(".check").val(),t=/^1[3|4|5|8][0-9]\d{4,8}$/.test(e);if(t&&/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(i)){var o=String(CryptoJS.MD5(i));$.ajax({url:"/H5GGShare/boluomeActivityLogin",type:"POST",dataType:"JSON",data:{userName:e,password:o,activityId:n,refUserName:e,urlName:s},success:function(i){console.log(i),i.success?window.location.href="Z"==s?c+"?userName="+e+"&activityId="+n+"&userItemsId="+a:"S"==s?c+"?userName="+e+"&itemsId="+r:"ggIndexShare?activityId="+n+"&userName="+e:requestMsg(i.msg)}})}else console.log(t),t?requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的手机号")}),$("#gg_register").click(function(){window.location.href="ggregister?word="+s+"&userName="+t+"&activityId="+n+"&userItemsId="+a+"&itemsId="+r+"&urlName="+c}),$("#gg_forget").click(function(){window.location.href="ggVerify?word="+s+"&userName="+t+"&activityId="+n+"&userItemsId="+a+"&itemsId="+r+"&urlName="+c})});
//# sourceMappingURL=../../_srcmap/activity/08/gglogin.js.map
