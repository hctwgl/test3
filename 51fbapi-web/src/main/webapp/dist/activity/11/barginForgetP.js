"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var r=e.substr(e.indexOf("?")+1,e.length),s=[];s=r.split("&");for(var o=0;o<s.length;o++)t[s[o].split("=")[0]]=unescape(s[o].split("=")[1])}return t}var spread=getUrl("spread");$(function(){var e=window.location.href,t=getUrlParam(e),r=(t.word,t.urlName,t.userName),s=(t.activityId,t.userItemsId,t.itemsId,localStorage.getItem("user")),o=localStorage.getItem("mesg");console.log(o),console.log(s),$(".btn").click(function(){var e=$(".blank-in").val(),t=String(CryptoJS.MD5(e)),a=/^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/.test(e);console.log(t),console.log(a),a?$.ajax({url:"/H5GGShare/boluomeActivityResetPwd",type:"POST",dataType:"JSON",data:{password:t,mobile:s,verifyCode:o},success:function(e){console.log(e),e.success?window.location.href="barginLogin?userName="+r+"&spread="+spread:requestMsg(e.msg)}}):requestMsg("请填写6-18位的数字、字母、字符组成的密码")})});
//# sourceMappingURL=../../_srcmap/activity/11/barginForgetP.js.map
