"use strict";function getUrlParam(e){var i=new Object;if(-1!=e.indexOf("?")){var n=e.substr(e.indexOf("?")+1,e.length),t=[];t=n.split("&");for(var a=0;a<t.length;a++)i[t[a].split("=")[0]]=unescape(t[a].split("=")[1])}return i}function getinfo(e){$.ajax({url:"/fanbei-web/activityUserInfo",data:{},type:"POST",dataType:"JSON",success:function(i){shareinfo.listDesc=i[0].listDesc[0],shareinfo.listTitle=i[0].listTitle[0],shareinfo.listPic=i[0].listPic[0],e(i[0])},error:function(e){requestMsg(e)}})}function _init(){$(".rule").on("click",function(){$("._global_mask").show(),$(".rulewindow").show()}),$(".rulewindow .close").on("click",function(){$("._global_mask").hide(),$(".rulewindow").hide()}),$(".buttons div").on("click",function(){$(".buttons div").removeClass("active"),$(this).addClass("active")})}function maidian(e){$.ajax({url:"/fanbei-web/shareActivity",data:{shareWith:"sharewith"+e},type:"POST",succuess:function(e){console.log(e)}})}window.shareinfo={},window.userId=getUrlParam("userId"),window.onload=function(){function e(e){var i=e.listRule,n=e.invitationCode,t=e.sumPrizeMoney,a=i.map(function(e,i){return"<p>"+(i+1)+"、"+e+"</p>"});$(".rulewindow .content").append(a.join("")),$(".invitecode").text(n),$(".invitecode")[0].dataset.clipboardText=n,$(".myreward").find(".firstSpan").text(t);var s=window.location.protocol,o=window.location.host,r=s+"//"+o;$(".rightown").on("click",function(){var e={shareAppTitle:shareinfo.listTitle,shareAppContent:shareinfo.listDesc,shareAppImage:shareinfo.listPic,shareAppUrl:r+"/fanbei-web/app/inviteregister?recommendCode="+n,isSubmit:"Y",sharePage:"inviteShare"};e=JSON.stringify(e);var i=BASE64.encoder(e);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+i})}function i(){$(".loadall").show(),setTimeout(function(){$(".loadall").hide()},1500)}function n(e){function i(e){var i=e.split("");return i.splice(3,4,"****"),i.join("")}return e.map(function(e,n){return'<div class="item"><div>'+i(e.userName)+"</div><div>"+e.createTime.slice(0,10)+"</div><div class="+("0"===e.color?"":"red")+">"+e.status+"</div><div class="+("0"===e.color?"":"red")+">"+e.prize_money+"</div></div>"})}function t(e,i,n){$.ajax({url:"/fanbei-web/rewardQuery",data:{type:i,pageSize:5,currentPage:e},type:"POST",dataType:"JSON",success:function(e){c=e[0].count,n(e[0],i)},error:function(e){requestMsg(e)}})}function a(e,i){var t=e.rewardQueryList,a=void 0;a=t.length>0?n(t):'<div class="nodata">您暂无邀请！</div>',$(".list").html(a),1==i?o=1:r=1,$(".list").html(a),setTimeout(function(){l=!0,$(".loading").hide()},0)}function s(e,i){var t=e.rewardQueryList,a=n(t);1==i?o=1:r=1,$(".list").append(a),setTimeout(function(){l=!0,$(".loading").hide()},0)}_init(),new Clipboard(".invitecode").on("success",function(e){console.log(e)}),$(".copycode").on("click",function(){alert("已复制到剪贴板，可粘贴")}),getinfo(e);var o=1,r=1,l=!0,c=null;t(1,1,a),$(".buttons .sameLevel").on("click",function(e){$(".loading").show(),e.target.className.indexOf("levelone")>-1?t(1,1,a):t(1,2,a)}),$(".con").scroll(function(){$(this)[0].scrollTop+$(this).height()+40>=$(this)[0].scrollHeight&&(l&&$(this).find(".item").length<c?(l=!1,$(".loading").show(),$(".levelone").hasClass("active")?(o++,t(o,1,s)):(r++,t(r,2,s))):$(this).find(".item").length==c&&i())})},window.postshareex=function(e){maidian(e)},window.postshareaf=function(e){},$(function(){$.ajax({type:"post",url:"/h5GgActivity/inviteCeremony",success:function(e){console.log(e),$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut();var i=e.data;$(".specialPrizeTitle span").html(i.spePreference),$(".specialCoupon span").html(i.couponAmount),$(".limitRule").html(i.activityRule),$(".exampleRule").html(i.example),$(".top").append('<img src="'+i.image+'">'),$(".secondSpan").html("+"+i.spePreference)},error:function(){requestMsg("哎呀，出错了！")}}),$(".levelthree").click(function(){$(".loading").show(),$(".list").empty(),$.ajax({type:"post",url:"/h5GgActivity/returnCoupon",success:function(e){$(".loading").hide();var i=e.data.returnCouponList,n="";if(i&&i.length>0){for(var t=0;t<i.length;t++)"已完成"==i[t].status?n+='<div class="takeOutMoney"><span>'+i[t].inviteeMobile+"</span>\n                    <span>"+i[t].registerTime+'</span>\n                    <span style="color:#f0110e">'+i[t].status+"</span>\n                    <span>"+i[t].reward+"</span></div>":n+='<div class="takeOutMoney"><span>'+i[t].inviteeMobile+"</span>\n                    <span>"+i[t].registerTime+"</span>\n                    <span>"+i[t].status+"</span>\n                    <span>"+i[t].reward+"</span></div>";$(".list").append(n)}else $(".list").append('<div class="nodata">您暂无邀请！</div>')},error:function(){requestMsg("哎呀，出错了！")}})})});
//# sourceMappingURL=../../_srcmap/js/app/newinvite.js.map
