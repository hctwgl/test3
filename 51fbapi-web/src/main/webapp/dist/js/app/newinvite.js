"use strict";function getUrlParam(i){var e=new Object;if(-1!=i.indexOf("?")){var n=i.substr(i.indexOf("?")+1,i.length),t=[];t=n.split("&");for(var o=0;o<t.length;o++)e[t[o].split("=")[0]]=unescape(t[o].split("=")[1])}return e}function getinfo(i){$.ajax({url:"/fanbei-web/activityUserInfo",data:{},type:"POST",dataType:"JSON",success:function(e){shareinfo.listDesc=e[0].listDesc[0],shareinfo.listTitle=e[0].listTitle[0],shareinfo.listPic=e[0].listPic[0],i(e[0])},error:function(i){requestMsg(i)}})}function _init(){$(".rule").on("click",function(){$("._global_mask").show(),$(".rulewindow").show()}),$(".rulewindow .close").on("click",function(){$("._global_mask").hide(),$(".rulewindow").hide()}),$(".buttons div").on("click",function(){$(".buttons div").removeClass("active"),$(this).addClass("active")})}function wordMove(){var i=$(".personAmount").scrollLeft();i>=$(".cont1").width()?i=0:i++,$(".personAmount").scrollLeft(i),setTimeout("wordMove()",speed)}function maidian(i){$.ajax({url:"/fanbei-web/postMaidianInfo",data:{maidianInfo:"sharewith"+i},type:"POST",succuess:function(i){console.log(i)}})}window.shareinfo={},window.userId=getUrlParam("userId"),$(function(){var i=$(".cont1").html();$(".cont2").html(i),wordMove()});var speed=30;window.onload=function(){function i(i){var e=i.listRule,n=i.invitationCode,t=i.sumPrizeMoney,o=e.map(function(i,e){return"<p>"+(e+1)+"、"+i+"</p>"});$(".rulewindow .content").append(o.join("")),$(".invitecode").text(n),$(".invitecode")[0].dataset.clipboardText=n,$(".myreward span").text(t),$(".rightown").on("click",function(){var i={shareAppTitle:shareinfo.listTitle,shareAppContent:shareinfo.listDesc,shareAppImage:shareinfo.listPic,shareAppUrl:location.origin+"/fanbei-web/app/inviteregister?recommendCode="+n,isSubmit:"Y",sharePage:"inviteShare"};i=JSON.stringify(i);var e=BASE64.encoder(i);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+e})}function e(){$(".loadall").show(),setTimeout(function(){$(".loadall").hide()},1500)}function n(i){function e(i){var e=i.split("");return e.splice(3,4,"****"),e.join("")}return i.map(function(i,n){return'<div class="item"><div>'+e(i.userName)+"</div><div>"+i.createTime.slice(0,10)+"</div><div class="+("0"===i.color?"":"red")+">"+i.status+"</div><div class="+("0"===i.color?"":"red")+">"+i.prize_money+"</div></div>"})}function t(i,e,n){$.ajax({url:"/fanbei-web/rewardQuery",data:{type:e,pageSize:5,currentPage:i},type:"POST",dataType:"JSON",success:function(i){u=i[0].count,n(i[0],e)},error:function(i){requestMsg(i)}})}function o(i,e){var t=i.rewardQueryList,o=void 0;o=t.length>0?n(t):'<div class="nodata">您暂无邀请！</div>',$(".list").html(o),1==e?c=1:l=1,$(".list").html(o),setTimeout(function(){d=!0,$(".loading").hide()},0)}function s(i,e){var t=i.rewardQueryList,o=n(t);1==e?c=1:l=1,$(".list").append(o),setTimeout(function(){d=!0,$(".loading").hide()},0)}_init(),new Clipboard(".invitecode").on("success",function(i){console.log(i)}),$(".copycode").on("click",function(){alert("已复制到剪贴板，可粘贴")});var a=$(".cont1").html();$(".cont2").html(a),function(){var i=$(".personAmount").scrollLeft();i>=$(".cont1").width()?i=0:i++,$(".personAmount").scrollLeft(i),setTimeout("wordMove()",r)}();var r=30;getinfo(i);var c=1,l=1,d=!0,u=null;t(1,1,o),$(".buttons div").on("click",function(i){$(".loading").show(),i.target.className.indexOf("levelone")>-1?t(1,1,o):t(1,2,o)}),$(".con").scroll(function(){$(this)[0].scrollTop+$(this).height()+40>=$(this)[0].scrollHeight&&(d&&$(this).find(".item").length<u?(d=!1,$(".loading").show(),$(".levelone").hasClass("active")?(c++,t(c,1,s)):(l++,t(l,2,s))):$(this).find(".item").length==u&&e())})},window.postshareex=function(i){maidian(i)},window.postshareaf=function(i){};
//# sourceMappingURL=../../_srcmap/js/app/newinvite.js.map
