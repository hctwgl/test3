"use strict";function loginSuccess(obj){userName=obj,$.ajax({url:"/fanbei-web/pickBoluomeCoupon",data:{sceneId:"8139",userName:userName},type:"POST",success:function success(data){if(data=eval("("+data+")"),data.success)requestMsg("领劵成功");else if(data.url)if(2==getBlatFrom())location.href=data.url;else{var appVersion=getInfo().appVersion.replace(/\./g,"");appVersion<"360"?requestMsg("请退出当前活动页面,登录后再进行领劵"):location.href=data.url}else requestMsg(data.msg)}})}var userName="";getInfo().userName&&(userName=getInfo().userName),$(function(){var e=new Date("May 26,2017 10:00:00"),a=e.valueOf(),s=new Date,u=s.valueOf();$(".grabTenyuan").click(function(){u<a?($(".tips").fadeIn(),setTimeout('$(".tips").fadeOut()',2e3)):loginSuccess(userName)}),$(".clickRule").click(function(){$(".mask").css("display","block"),$(".rule").fadeIn()}),$(".mask").click(function(){$(".mask").css("display","none"),$(".rule").fadeOut()}),$(".go").click(function(){$.ajax({url:"/fanbei-web/getBrandUrl",data:{shopId:"1",userName:userName},type:"POST",success:function(e){e=JSON.parse(e),location.href=e.url}})})});
//# sourceMappingURL=../../_srcmap/activity/05/pirateMovie.js.map
