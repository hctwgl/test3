"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName),$(function(){var currentStamp=Date.parse(new Date),activityTime="2017-05-26 10:00:00",activityStamp=Date.parse("2017-05-26 10:00:00");$(".grabTenyuan").click(function(){currentStamp<activityStamp?($(".tips").fadeIn(),setTimeout('$(".tips").fadeOut()',2e3)):$.ajax({url:"/fanbei-web/pickBoluomeCoupon",data:{sceneId:"8139",userName:userName},type:"POST",success:function success(data){data=eval("("+data+")"),data.success?requestMsg("领劵成功"):data.url?2==getBlatFrom()?location.href=data.url:requestMsg("请退出当前活动页面,登录后再进行领劵"):requestMsg(data.msg)}})}),$(".clickRule").click(function(){$(".mask").css("display","block"),$(".rule").fadeIn()}),$(".mask").click(function(){$(".mask").css("display","none"),$(".rule").fadeOut()}),$(".go").click(function(){location.href="kouMovie"})});
//# sourceMappingURL=../../_srcmap/activity/05/pirateMovie.js.map
