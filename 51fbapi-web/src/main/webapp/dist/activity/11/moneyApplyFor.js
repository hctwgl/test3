"use strict";function jundge(){unapprove&&($(".applyButton").css({"background-color":"#999","box-shadow":"none"}),$(".applyButton").html(""+againApplyDesc))}function getIntitalData(a){$.ajax({url:"/fanbei-web/applyInterimAuPage",type:"post",success:function(a){if(allDate=JSON.parse(a),console.log(allDate,"allDate"),0==allDate.success&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN"),failureStatus=allDate.data.failureStatus,interimAmount=allDate.data.interimAmount,gmtFailuretime=allDate.data.gmtFailuretime,amount=allDate.data.amount,againApplyDesc=allDate.data.againApplyDesc,againApplyTime=allDate.data.againApplyTime,interimUsed=allDate.data.interimUsed,jundge(),type=allDate.data.type,rule=allDate.data.rule,ruleTitle=allDate.data.ruleTitle,$(".Money").append('<span class="Money">'+amount+"</span>"),$(".limitMoney").append('<span class="limitMoney">'+interimAmount+"</span>"),$(".detailDate").append('<span class="detailDate">'+gmtFailuretime+"</span>"),$(".moneyApply-rule").append('<div class="ruleWord">'+rule+"</div>"),$(".wordTitle").append('<div class="wordTitle">'+ruleTitle+"</div>"),void 0!=interimAmount&&void 0!=gmtFailuretime||($(".limitMoney").html("0.00"),$(".detailDate").html("----"),$(".detailDate").css({color:"#999","font-size":"0.42rem","line-height":"0.4rem"})),1==type&&againApplyTime>0)return $(".applyButton").css({"background-color":"#999","box-shadow":"none"}),$(".applyButton").html(""+againApplyDesc),$(".applyButton").unbind("click"),!1;2===type||0===interimUsed&&0!=failureStatus||($(".applyButton").css({"background-color":"#999","box-shadow":"none"}),$(".applyButton").html("暂时无法再次提额"),$(".applyButton").unbind("click"),1==failureStatus&&$(".useless").show())}})}var allDate=void 0,failureStatus=void 0,againApplyDesc=void 0,amount=void 0,type=void 0,gmtFailuretime=void 0,interimAmount=void 0,rule=void 0,ruleTitle=void 0,interimUsed=void 0,againApplyTime=void 0,upMoney=document.getElementById("upMoney"),unapprove=getUrl("unapprove");$(function(){getIntitalData();var a=getUrl("applySuccess");a&&($(".applyButton").css({"background-color":"#999","box-shadow":"none"}),$(".applyButton").html("暂时无法再次提额"),1==failureStatus&&$(".useless").show()),$(".applyButton").click(function(){unapprove||a||($(".applyButton").hide(),$(".verirication").show(),$.ajax({url:"/fanbei-web/applyInterimAu",type:"post",success:function(a){console.log(a);var t=JSON.parse(a);setTimeout(function(){3==t.data.type&&(window.location.href="/fanbei-web/activity/unapprove"),1==t.data.type&&(window.location.href="/fanbei-web/activity/unapprove?approve=show")},1e3),55==t.data.type&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN"),2==t.data.type&&($(".popUp").show(),$(".applyButton").show(),$(".verirication").hide())}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/moneyApplyFor?type=applyMoney"},success:function(a){}}))}),$(".buttonBox").click(function(){$(".popUp").hide()})});
//# sourceMappingURL=../../_srcmap/activity/11/moneyApplyFor.js.map
