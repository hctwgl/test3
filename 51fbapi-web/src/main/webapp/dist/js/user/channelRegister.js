"use strict";function formatDateTime(){var e=new Date,t=e.getFullYear(),o=e.getMonth()+1;o=o<10?"0"+o:o;var r=e.getDate();return r=r<10?"0"+r:r,t+o+r+e.getHours()+e.getMinutes()+e.getSeconds()}var token=formatDateTime()+Math.random().toString(36).substr(2),style=$("#style").val(),os=getBlatFrom();console.log(os),21==style?(document.title="借款超人注册",$("#borrowSuperman").click(function(){1==os?window.location.href="http://f.51fanbei.com/online/jiekuancaoren_v3.7.1.apk":2==os&&(window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%85%8D%E6%81%AF%E5%B0%8F%E9%A2%9D%E5%80%9F%E8%B4%B7%E6%89%8B%E6%9C%BA%E8%BD%AF%E4%BB%B6/id1263792729?mt=8")})):22==style&&(document.title="借钱平台注册",$("#BrwPlatform").click(function(){1==os?requestMsg("您好~借钱平台安卓端正在加急上线中，马上就能与您相见啦。请耐心等待"):2==os&&(window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E9%92%B1%E5%B9%B3%E5%8F%B0-%E5%B0%8F%E9%A2%9D%E6%9E%81%E9%80%9F%E7%8E%B0%E9%87%91%E5%80%9F%E8%B4%B7/id1259127316?mt=8")}));var _fmOpt;!function(){_fmOpt={partner:"alading",appName:"alading_web",token:token};var e=new Image(1,1);e.onload=function(){_fmOpt.imgLoaded=!0},e.src=("https:"==document.location.protocol?"https://":"http://")+"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId="+_fmOpt.token;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=("https:"==document.location.protocol?"https://":"http://")+"static.fraudmetrix.cn/fm.js?ver=0.1&t="+((new Date).getTime()/36e5).toFixed(0);var o=document.getElementsByTagName("script")[0];o.parentNode.insertBefore(t,o)}(),function(e){e._tt_config=!0;var t=document.createElement("script");t.type="text/javascript",t.async=!0,t.src=document.location.protocol+"//s3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js",t.onerror=function(){var e=new XMLHttpRequest,o=window.encodeURIComponent(window.location.href),r=t.src;if(16==style)var s="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+o+"&js_url="+r+"&convert_id=63736236689";else if(14==style)var s="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+o+"&js_url="+r+"&convert_id=62421367574";else var s="//ad.toutiao.com/link_monitor/cdn_failed?web_url="+o+"&js_url="+r;e.open("GET",s,!0),e.send(null)};var o=document.getElementsByTagName("script")[0];o.parentNode.insertBefore(t,o)}(window),$(function(){function e(){o--,o<=0?($("#register_codeBtn").removeAttr("disabled"),$("#register_codeBtn").text("获取验证码"),clearInterval(t),o=60):$("#register_codeBtn").text(o+" s")}var t,o=60;$("#register_codeBtn").click(function(){var r=($(this).attr("isState"),$("#register_mobile").val()),s=$("#channelCode").val(),n=$("#pointCode").val();!isNaN(r)&&/^1(3|4|5|7|8)\d{9}$/i.test(r)?($("#register_codeBtn").attr("disabled",!0),$.ajax({url:"/app/user/getRegisterSmsCode",type:"POST",dataType:"JSON",data:{mobile:r,token:token,channelCode:s,pointCode:n},success:function(r){console.log(r),r.success?($("#register_codeBtn").attr("isState",1),$("#register_codeBtn").text(o+" s"),t=setInterval(e,1e3)):(requestMsg(r.msg),$("#register_codeBtn").removeAttr("disabled"))},error:function(){requestMsg("请求失败")}})):requestMsg("请填写正确的手机号")}),$("#register_submitBtn").click(function(){var e=$("#register_password").val(),t=String(CryptoJS.MD5(e)),o=e.length,r=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/,s=r.test(e),n=$("#register_mobile").val(),a=$("#register_verification").val(),i=$("#channelCode").val(),c=$("#pointCode").val();$("#register_codeBtn").attr("isState");/^1(3|4|5|7|8)\d{9}$/i.test(n)&&""!=n?""!=a?s&&6<=o&&o<=18?$("#input_check").is(":checked")?1==$("#register_codeBtn").attr("isState")?(16==style?_taq.push({convert_id:"63736236689",event_type:"form"}):14==style?_taq.push({convert_id:"62421367574",event_type:"form"}):_taq.push({convert_id:"59212981134",event_type:"form"}),$.ajax({url:"/app/user/commitChannelRegister",type:"POST",dataType:"JSON",data:{registerMobile:n,smsCode:a,password:t,channelCode:i,pointCode:c,token:token},success:function(e){e.success?10==style||12==style||15==style||16==style?($("#register_submitBtn").attr("disabled",!0),$(".registerSuss8").removeClass("hide"),$(".registerSuss12").removeClass("hide"),$(".registerMask").removeClass("hide"),$("#downloadApp").click(function(){window.location.href=e.url})):20==style?($(".registerSuss8").removeClass("hide"),$(".registerMask").removeClass("hide"),$("#downloadApp").click(function(){1==os?window.location.href="http://fusion.qq.com/cgi-bin/qzapps/unified_jump?appid=42318693&from=mqq&actionFlag=0&params=pname%3Dcom.alfl.www%26versioncode%3D373%26channelid%3D%26actionflag%3D0":(window.location.href="https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?mt=8&submit=edit&term=%E5%88%86%E6%9C%9F%E8%B4%B7#software",window.location.href="https://itunes.apple.com/us/app/51%E8%BF%94%E5%91%97/id1136587444?mt=8")})):21==style?1==os?window.location.href="http://f.51fanbei.com/online/jiekuancaoren_v3.7.1.apk":2==os&&(window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%85%8D%E6%81%AF%E5%B0%8F%E9%A2%9D%E5%80%9F%E8%B4%B7%E6%89%8B%E6%9C%BA%E8%BD%AF%E4%BB%B6/id1263792729?mt=8"):22==style?1==os?requestMsg("您好~借钱平台安卓端正在加急上线中，马上就能与您相见啦。请耐心等待"):2==os&&(window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E9%92%B1%E5%B9%B3%E5%8F%B0-%E5%B0%8F%E9%A2%9D%E6%9E%81%E9%80%9F%E7%8E%B0%E9%87%91%E5%80%9F%E8%B4%B7/id1259127316?mt=8"):($("#register_submitBtn").attr("disabled",!0),window.location.href=e.url):requestMsg(e.msg)},error:function(){requestMsg("注册失败")}})):requestMsg("请获取验证码"):requestMsg("请阅读并同意《51返呗用户注册协议》"):requestMsg("请填写6-18位的数字、字母、字符组成的密码"):requestMsg("请输入正确的验证码"):requestMsg("请填写正确的手机号")})});
//# sourceMappingURL=../../_srcmap/js/user/channelRegister.js.map
