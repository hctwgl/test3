"use strict";function getUrlParam(e){var t=new Object;if(-1!=e.indexOf("?")){var o=e.substr(e.indexOf("?")+1,e.length),n=[];n=o.split("&");for(var a=0;a<n.length;a++)t[n[a].split("=")[0]]=unescape(n[a].split("=")[1])}return t}function alaShareData(){var e={appLogin:"Y",type:"share",shareAppTitle:"老铁~快来吃霸王餐啦~",shareAppContent:"节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>",shareAppImage:"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",shareAppUrl:domainName+"/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName="+userName,isSubmit:"Y",sharePage:"ggFixShare"};return JSON.stringify(e)}var protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,currentUrl=window.location.href,param=getUrlParam(currentUrl),shopId=param.shopId03,userName="";getInfo().userName&&(userName=getInfo().userName);var vm=new Vue({el:"#ggOverlord",data:{content:{},couponLength:"",inviteSumMoney:"",baseData:""},created:function(){this.logData()},methods:{logData:function(){var e=this;$.ajax({type:"post",url:"/h5GgActivity/inviteFriend",success:function(t){console.log(t),e.baseData=t.data,setTimeout(function(){new Clipboard(".invitecode").on("success",function(e){console.log(e)}),$(".copycode").on("click",function(){alert("已复制到剪贴板，可粘贴")})},0)},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({type:"post",url:"/h5GgActivity/returnCoupon",success:function(t){console.log(t),e.content=t.data,e.couponLength=e.content.returnCouponList.length,e.content.couponAmount=e.content.couponAmount.toString(),e.inviteSumMoney=e.content.couponAmount.split(""),e.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut()})},error:function(){requestMsg("哎呀，出错了！")}})},inviteButtonClick:function(){var e='{"shareAppTitle":"老铁~快来吃霸王餐啦~","shareAppContent":"节日剁手不吃土，来51返呗点餐立减15元，有福同享，你也快来>>>","shareAppImage":"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg","shareAppUrl":"'+domainName+'/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName="'+userName+',"isSubmit":"Y","sharePage":"ggFixShare"}',t=BASE64.encoder(e);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+t,$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=callPeople"},success:function(e){console.log(e)}})},toCashClick:function(){$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:shopId},dataType:"JSON",success:function(e){console.log(e),e.success,location.href=e.url},error:function(){requestMsg("请求失败")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=toUseCoupon"},success:function(e){console.log(e)}})},fixDate:function(e){return e.replace(/-/g,".")}}});
//# sourceMappingURL=../../_srcmap/activity/11/ggOverlord.js.map
