"use strict";function GetPageName(e){var o=[];return o=e.split("/"),o=o[o.length-1].split("?"),o[0]}function step(){setTimeout(function(){$(".lineBox01").addClass("lineShow01"),$(".word01").addClass("wordShow01"),$(".lineBox02").addClass("lineShow02"),$(".word02").addClass("wordShow02"),$(".lineBox03").addClass("lineShow03"),$(".word03").addClass("wordShow03"),$(".lineBox04").addClass("lineShow04"),$(".word04").addClass("wordShow04")},500)}function alaShareData(){var e={appLogin:"Y",type:"share",shareAppTitle:"有人@你~你有最高188元惊喜金待领取！",shareAppContent:"16元外卖1元购，下单即返30元~",shareAppImage:"http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",shareAppUrl:domainName+"/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName="+userName+"&pageName="+pageName,isSubmit:"Y",sharePage:"ggFixShare"};return JSON.stringify(e)}function wordMove(){var e=$(".personAmount").scrollLeft();e>=$(".cont1").width()?e=0:e++,$(".personAmount").scrollLeft(e),setTimeout("wordMove()",speed)}function showTimerS(e){var o=0,t=0,a=0;return e>0&&(o=Math.floor(e/3600),t=Math.floor(e/60)-60*o,a=Math.floor(e)-60*o*60-60*t),o<=9&&(o="0"+o),t<=9&&(t="0"+t),a<=9&&(a="0"+a),o+":"+t+":"+a}var protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,userName="";getInfo().userName&&(userName=getInfo().userName);var returnNum=getBlatFrom(),typeFrom=getUrl("typeFrom"),url=window.location.href,pageName=GetPageName(url),vm=new Vue({el:"#ggFix",data:{content:{},ruleShow:"",couponCont:{},couponList:"",firstTitle:"",firstValue:"",secondTitle:"",secondValue:"",myRebateMoney:"",alertData:"",alertShow:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut();var currentStamp=Date.parse(new Date),endStamp=Date.parse(new Date("2017-12-22 18:00:00")),diff=(endStamp-currentStamp)/1e3;showTimerS(diff),diff--,window.setInterval(function(){showTimerS(diff),$(".timeOut").html(showTimerS(diff)),diff--},1e3);var cont=$(".cont1").html();$(".cont2").html(cont),wordMove(),$.ajax({type:"post",url:"/h5GgActivity/homePage",success:function success(data){if(console.log(data,"初始化数据"),self.content=eval("("+data+")").data,console.log(self.content,"初始化数据"),self.firstTitle=self.content.resultList[0].name,self.firstValue=self.content.resultList[0].value,self.secondTitle=self.content.resultList[1].name,self.secondValue=self.content.resultList[1].value,self.content.totalRebate&&0!=self.content.totalRebate){if(self.content.totalRebate=self.content.totalRebate.toString(),self.myRebateMoney=self.content.totalRebate.split(""),-1==self.myRebateMoney.indexOf("."))return;self.$nextTick(function(){var e=self.myRebateMoney.indexOf(".");$(".fanMoneyStyle i").eq(e).addClass("pointSpecialStyle"),$(".fanMoneyStyle i:gt("+e+")").addClass("decimalSpecialStyle")})}},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({type:"post",url:"/h5GgActivity/boluomeCoupon",success:function success(data){console.log(data,"优惠券初始化"),data=eval("("+data+")"),console.log(data),data.success&&(self.couponCont=data.data,console.log(self.couponCont),self.couponCont.boluomeCouponList&&(self.couponList=self.couponCont.boluomeCouponList),console.log(self.couponList,"优惠券列表"))},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({type:"post",url:"/h5GgActivity/popUp",success:function(e){self.alertData=e.data,console.log(self.alertData,"弹窗初始化"),(self.alertData.couponToPop&&"Y"==self.alertData.couponToPop||self.alertData.rebateToPop&&"Y"==self.alertData.rebateToPop)&&(self.alertShow=!0)},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom="+typeFrom},success:function(e){console.log(e)}})},couponClick:function(e){var o=this,t=o.couponCont.boluomeCouponList[e].sceneId;$.ajax({url:"/h5GgActivity/pickBoluomeCoupon",type:"POST",dataType:"JSON",data:{sceneId:t},success:function(o){console.log(o),o.success?(requestMsg(o.msg),$(".coupon").eq(e).find(".getCoupon").html("已领取")):window.location.href=o.url},error:function(){requestMsg("请求失败")}})},cardClick:function(e){var o=e.shopId,t=e.name;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:o},dataType:"JSON",success:function(e){console.log(e),e.success,location.href=e.url},error:function(){requestMsg("请求失败")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=cardClick&cardName="+t},success:function(e){console.log(e)}})},contNewUserClick:function(){var e=this,o=e.content.waiMaiShopId;$.ajax({type:"post",url:"/fanbei-web/getBrandUrlV1",data:{shopId:o},dataType:"JSON",success:function(e){console.log(e),e.success,location.href=e.url},error:function(){requestMsg("请求失败")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=newUserBtn"},success:function(e){console.log(e)}})},contOldUserClick:function(){var e=this,o=e.content.waiMaiShopId;window.location.href="ggOverlord?addUiName=SHOWSHARE&shopId03="+o,$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=oldUserBtn"},success:function(e){console.log(e)}})},toCashClick:function(){window.location.href=1==returnNum?"/fanbei-web/opennative?name=GG_com.alfl.www.business.ui.CashLoanActivity":"/fanbei-web/opennative?name=GG_ALACashBorrowingViewController",$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=toCash"},success:function(e){console.log(e)}})},toRebateMoneyClick:function(){window.location.href=1==returnNum?"/fanbei-web/opennative?name=GG_com.alfl.www.main.ui.MainActivity_1":"/fanbei-web/opennative?name=GG_ALABrandViewController_1",$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/ggFix?typeFrom=toGuangGuangBtn"},success:function(e){console.log(e)}})},toGetClick:function(){var e=this;$(".toast").hide(),e.alertShow=!1,$("html,body").animate({scrollTop:$(".fourthCont").offset().top},800)},ruleClick:function(){this.ruleShow=!0},closeClick:function(){var e=this;e.ruleShow=!1,$(".toast").hide(),e.alertShow=!1}}});step();var speed=30;
//# sourceMappingURL=../../_srcmap/activity/11/ggFix.js.map
