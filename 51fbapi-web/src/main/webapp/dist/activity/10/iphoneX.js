"use strict";function time(){function t(t){$(".time").eq(t).addClass("active01"),$(".time").eq(t).siblings().removeClass("active01"),$(".time").eq(t).find("span").addClass("active02"),$(".time").eq(t).siblings().find("span").removeClass("active02")}var e=(new Date).getTime(),o=Date.parse(new Date("2017/10/19 00:00:00")),a=Date.parse(new Date("2017/10/25 00:00:00")),s=(Date.parse(new Date("2017/10/26 00:00:00")),Date.parse(new Date("2017/10/27 00:00:00"))),n=Date.parse(new Date("2017/11/3 00:00:00")),i=Date.parse(new Date("2017/10/25 00:00:00"));console.log(i),console.log(e),e>=o&&t(0),e>=a&&e<s&&t(1),e>=s&&(t(2),console.log(document.getElementsByClassName("buyNow")),console.log($(".buyNow").show()),console.log(document.getElementsByClassName("coupon")),setTimeout(function(){$(".buyNow").show(),$(".btn").hide(),$(".orderDes").hide()},0),$(".buyNow").show(),$(".btn").hide(),$(".orderDes").hide(),$(".coupon").hide()),e>=n&&t(3),e>=o&&e<=i&&($(".rightNow").css("background-color","#d2d2d2"),this.flag=!1)}function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"iPhone X 预约立减200",shareAppContent:"iPhone X 预约活动震撼开启！1元预约立减200元！前50名下单用户享188元官方配件大礼包！速来抢购",shareAppImage:"http://f.51fanbei.com/h5/app/activity/09/iphone8_06.jpg",shareAppUrl:domainName+"/fanbei-web/activity/iphoneXShare?modelId="+modelId+"&title=",isSubmit:"Y",sharePage:"iphoneXShare"};return JSON.stringify(t)}var title=decodeURI(getUrl("title"));console.log(title),document.title=title;var modelId=getUrl("modelId"),protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:".bigBox",data:{content:{},prizeCont:{},orderStatus:"",loginStatus:"",ruleShow:"",couponCount:"",getStatus80:"",getStatus100:"",getStatus150:"",couponobj:{status80:80,status100:100,status150:150},flag:!0},created:function(){},mounted:function(){this.logData(),this.loginPrize()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo",data:{modelId:modelId},success:function success(data){self.content=eval("("+data+")").data.activityList.slice(0,3),self.content.firstList=self.content[0].activityGoodsList.slice(0,1),self.content.secondList=self.content[1].activityGoodsList,self.content.thirdList=self.content[2].activityGoodsList,console.log(self.content),self.$nextTick(function(){$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200}),time.call(self)})},error:function(){requestMsg("哎呀，出错了！")}})},loginPrize:function loginPrize(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activity/getActivityGood",success:function success(data){data=eval("("+data+")"),console.log(data),self.orderStatus=data.status,self.loginStatus=data.loginStatus,self.couponCount=data.couponCount,self.getStatus80=data.getStatus80,self.getStatus100=data.getStatus100,self.getStatus150=data.getStatus150,self.loginStatus=data.loginStatus,"Y"==self.getStatus80&&($(".rightNow").eq(0).addClass("gray"),$(".rightNow").eq(0).html("已经领取")),"Y"==self.getStatus100&&($(".rightNow").eq(1).addClass("gray"),$(".rightNow").eq(1).html("已经领取")),"Y"==self.getStatus150&&($(".rightNow").eq(2).addClass("gray"),$(".rightNow").eq(2).html("已经领取"))},error:function(){requestMsg("哎呀，出错了！")}})},clickCoupon:function(t){function e(t){return t.indexOf("status80")>-1?"status80":t.indexOf("status100")>-1?"status100":t.indexOf("status150")>-1?"status150":void 0}var o=t.target.className,a=this,s=document.getElementsByClassName("money");Array.prototype.slice.call(s);console.log(a.couponobj,e(o),o),$.ajax({type:"post",url:"/fanbei-web/activity/getReservationCoupons",data:{couponAmount:a.couponobj[e(o)]},success:function(t){console.log(t),console.log(o),t.success?(requestMsg(JSON.parse(t).msg),a.loginPrize()):(requestMsg(JSON.parse(t).msg),console.log(a.loginPrize),a.loginPrize()),"N"==a.loginStatus&&(window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN")},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(t){"SELFSUPPORT"==t.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t.goodsId+'"}'},orderClick:function(t){var e=this;"N"==e.loginStatus?window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN":"FAIL"==e.orderStatus&&(window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}')},buyClick:function(t){"N"==this.loginStatus?window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN":window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}'},ruleClick:function(){this.ruleShow="Y"},maskClick:function(){this.ruleShow=""}}});
//# sourceMappingURL=../../_srcmap/activity/10/iphoneX.js.map
