"use strict";function dataloadcb(e){switch(e){case 0:applysuccess();break;case 1:applyrefuse();break;case 2:initPage();break;default:initPage()}}function initPage(){void 0!=interimAmount&&void 0!=gmtFailuretime||($(".limitMoney").html("0.00"),$(".detailDate").html("----"),$(".detailDate").css({color:"#999","font-size":"0.42rem","line-height":"0.4rem"}))}function applyrefuse(){void 0!=interimAmount&&void 0!=gmtFailuretime||($(".limitMoney").html("0.00"),$(".detailDate").html("----"),$(".detailDate").css({color:"#999","font-size":"0.42rem","line-height":"0.4rem"}))}function applysuccess(){void 0!=interimAmount&&void 0!=gmtFailuretime||($(".limitMoney").html("0.00"),$(".detailDate").html("----"),$(".detailDate").css({color:"#999","font-size":"0.42rem","line-height":"0.4rem"}))}function getIntitalData(e){$.ajax({url:"/fanbei-web/applyInterimAuPage",type:"post",success:function(t){allDate=JSON.parse(t),console.log(allDate,"allDateff"),0==allDate.success&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN"),failureStatus=allDate.data.failureStatus,interimAmount=allDate.data.interimAmount,gmtFailuretime=allDate.data.gmtFailuretime,amount=allDate.data.amount,againApplyDesc=allDate.data.againApplyDesc,console.log(againApplyDesc,"againApplyDesc"),type=allDate.data.type,rule=allDate.data.rule,ruleTitle=allDate.data.ruleTitle,$(".Money").append('<span class="Money">'+amount+"</span>"),$(".limitMoney").append('<span class="limitMoney">'+interimAmount+"</span>"),$(".detailDate").append('<span class="detailDate">'+gmtFailuretime+"</span>"),$(".moneyApply-rule").append('<div class="ruleWord">'+rule+"</div>"),$(".wordTitle").append('<div class="wordTitle">'+ruleTitle+"</div>"),console.log(allDate.success,"allDate.success"),e(type)}})}var allDate=void 0,failureStatus=void 0,againApplyDesc=void 0,amount=void 0,type=void 0,gmtFailuretime=void 0,interimAmount=void 0,rule=void 0,ruleTitle=void 0,upMoney=document.getElementById("upMoney");$(function(){getIntitalData(dataloadcb),getUrl("applySuccess")&&($(".applyButton").css({"background-color":"#999","box-shadow":"none"}),$(".applyButton").html("暂时无法再次提额"),upMoney.addEventListener("click",function(e){e.preventDefault()}),1==failureStatus&&$(".useless").show()),getUrl("unapprove")&&($(".applyButton").css({"background-color":"#999","box-shadow":"none"}),$(".applyButton").html(""+againApplyDesc),upMoney.addEventListener("click",function(e){e.preventDefault()})),$(".applyButton").click(function(){$(".applyButton").hide(),$(".verirication").show(),$.ajax({url:"/fanbei-web/applyInterimAu",type:"post",success:function(e){var t=JSON.parse(e);console.log(t,"applyDate"),setTimeout(function(){3==t.data.type&&(window.location.href="/fanbei-web/activity/unapprove"),1==t.data.type&&(window.location.href="/fanbei-web/activity/unapprove?approve=show")},1e3),55==t.data.type&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN"),2==t.data.type&&($(".popUp").show(),$(".applyButton").show(),$(".verirication").hide())}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/moneyApplyFor?type=applyMoney"},success:function(e){}})}),$(".buttonBox").click(function(){$(".popUp").hide()})});
//# sourceMappingURL=../../_srcmap/activity/11/moneyApplyFor.js.map
