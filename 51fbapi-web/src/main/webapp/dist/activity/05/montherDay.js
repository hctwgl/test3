"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName),$(function(){$(".buy").click(function(){$.ajax({url:"/fanbei-web/getBrandUrl",data:{shopId:"17",userName:userName},type:"POST",success:function success(data){data=eval("("+data+")"),data.success,location.href=data.url}})}),$(".saleOff").click(function(){$.ajax({url:"/fanbei-web/pickBoluomeCoupon",data:{sceneId:"387",userName:userName},type:"POST",success:function success(data){data=eval("("+data+")"),data.success?requestMsg("领劵成功"):data.url?2==getBlatFrom()?location.href=data.url:requestMsg("请退出当前活动页面,登录后再进行领劵"):requestMsg(data.msg)}})})});
//# sourceMappingURL=../../_srcmap/activity/05/montherDay.js.map
