"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),n=e.getMonth()+1;n=n<10?"0"+n:n;var o=e.getDate();return o=o<10?"0"+o:o,t+n+o+e.getHours()+e.getMinutes()+e.getSeconds()}var users=[{phone:"13120808880",money:"8000"},{phone:"13777739767",money:"6000"},{phone:"18657172099",money:"7000"},{phone:"15869125132",money:"6000"},{phone:"17826833863",money:"4000"},{phone:"17623000389",money:"5000"},{phone:"15769163696",money:"4000"},{phone:"18367127197",money:"3000"},{phone:"13958114326",money:"5000"},{phone:"13758140925",money:"4000"},{phone:"13588865276",money:"3000"},{phone:"15757161549",money:"7000"},{phone:"18768110576",money:"6000"},{phone:"13216178996",money:"4000"},{phone:"18768110576",money:"5000"}],recommendCode=getUrl("recommendCode"),_fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(t,n)}();var token=formatDateTime()+Math.random().toString(36).substr(2),checkphone=function(){return!!/^1(3|4|5|7|8)\d{9}$/i.test($("#user").val())},checkpwd=function(){return!!/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/.test($("#pwd").val())},checkverify=function(){return 6===$("#verify").val().length},checkcheckbox=function(){return!!$(".checkbox").hasClass("checked")},sixty=function(){var e=60,t=setInterval(function(){$(".getcode").html(e+"S"),e--},1e3);setTimeout(function(){clearInterval(t),$(".getcode").removeClass("disabled").text("获取验证码")},61e3)},insertroll=function(e){var t="";return e.forEach(function(e){var n=e.phone.split("");n.splice(3,4,"****"),t+="<li>用户 "+n.join("")+" &nbsp;成功借款 "+e.money+"元</li>"}),t},timescroll=function(e){var t=e.length;setInterval(function(){$(".roll ul").animate({top:"-=20px"})},2e3),setInterval(function(){$(".roll ul").css({top:"20px"})},2e3*t-1)};window.onload=function(){$(".roll ul").append(insertroll(users)),timescroll(users),$(".getcode").on("click",function(){!checkphone()||$(".getcode").hasClass("disabled")||$(".getcode").hasClass("pending")?checkphone()||requestMsg("请填写正确的手机号"):($(".getcode").addClass("pending"),$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:$("#user").val()},success:function(e){$(".getcode").removeClass("pending"),$(".registerMask").removeClass("hide"),$(".imgVftCodeWrap").removeClass("hide"),$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data),$(".imgVftCodeRefresh").on("click",function(){$.ajax({url:"/app/user/getImgCode",type:"POST",dataType:"JSON",data:{mobile:$("#user").val()},success:function(e){$("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+e.data)},error:function(){requestMsg("请求失败")}})}),$(".imgVftCodeSbumit").on("click",function(){$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:$("#user").val(),token:token,verifyImgCode:$("#imgVftCode").val()},success:function(e){e.success?($(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide"),$(".getcode").addClass("disabled"),sixty()):(requestMsg(e.msg),$(".getcode").removeClass("disabled"))},error:function(){requestMsg("请求失败")}})}),$("#imgVftCodeClose").click(function(){$(".registerMask").addClass("hide"),$(".imgVftCodeWrap").addClass("hide")})},error:function(){$(".getcode").removeClass("pending"),requestMsg("请求失败")}}))}),$(".reg").on("click",function(){var e=checkphone(),t=checkpwd(),n=checkverify(),o=checkcheckbox();e?t?n?o||requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请输入正确的验证码"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请填写正确的手机号"),e&&t&&n&&o&&($(".reg").hasClass("pending")||($(".reg").addClass("pending"),$.ajax({url:"/app/user/commitRegister",type:"POST",dataType:"JSON",data:{registerMobile:$("#user").val(),smsCode:$("#verify").val(),password:String(CryptoJS.MD5($("#pwd").val())),recommendCode:recommendCode,token:token},success:function(e){$(".reg").removeClass("pending"),e.success?window.location.href=e.url:requestMsg(e.msg)},error:function(){$(".reg").removeClass("pending"),requestMsg("注册失败")}})))}),$(".checkbox").on("click",function(){$(".checkbox").toggleClass("checked")})};
//# sourceMappingURL=../../_srcmap/js/app/inviteregister.js.map
