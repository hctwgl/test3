"use strict";function getUrlParam(t){var e=new Object;if(-1!=t.indexOf("?")){var n=t.substr(t.indexOf("?")+1,t.length),o=[];o=n.split("&");for(var a=0;a<o.length;a++)e[o[a].split("=")[0]]=unescape(o[a].split("=")[1])}return e}function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"老铁~快来吃霸王餐啦~",shareAppContent:"节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>",shareAppImage:"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",shareAppUrl:domainName+"/fanbei-web/activity/ggFixShare",isSubmit:"Y",sharePage:"ggFixShare"};return JSON.stringify(t)}var protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,currentUrl=window.location.href,param=getUrlParam(currentUrl),shopId=param.shopId03,vm=new Vue({el:"#ggOverlord",data:{content:{},couponLength:"",inviteSumMoney:"",baseData:""},created:function(){this.logData()},methods:{logData:function(){var t=this;$.ajax({type:"post",url:"/h5GgActivity/inviteFriend",success:function(e){console.log(e),t.baseData=e.data},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({type:"post",url:"/h5GgActivity/returnCoupon",success:function(e){console.log(e),t.content=e.data,t.couponLength=t.content.returnCouponList.length,t.content.inviteAmount=t.content.inviteAmount.toString(),t.inviteSumMoney=t.content.inviteAmount.split(""),t.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut()})},error:function(){requestMsg("哎呀，出错了！")}})},inviteButtonClick:function(){var t='{"shareAppTitle":"老铁~快来吃霸王餐啦~","shareAppContent":"节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>","shareAppImage":"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg","shareAppUrl":"'+domainName+'/fanbei-web/activity/ggFixShare","isSubmit":"Y","sharePage":"ggFixShare"}',e=BASE64.encoder(t);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+e},toCashClick:function(){$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:shopId},dataType:"JSON",success:function(t){console.log(t),t.success,location.href=t.url},error:function(){requestMsg("请求失败")}})},fixDate:function(t){return t.replace(/-/g,".")}}});$(function(){new Clipboard(".invitecode").on("success",function(t){console.log(t)}),$(".copycode").on("click",function(){alert("已复制到剪贴板，可粘贴")})});
//# sourceMappingURL=../../_srcmap/activity/11/ggOverlord.js.map
