"use strict";function hello(){var t=$(".myscroll").scrollTop();t>=$(".roll").height()?t=0:t++,$(".myscroll").scrollTop(t),setTimeout("hello()",speed)}function addStyle(t){$(".time").eq(t).addClass("active01"),$(".time").eq(t).siblings().removeClass("active01"),$(".time").eq(t).find("span").addClass("active02"),$(".time").eq(t).siblings().find("span").removeClass("active02")}function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"iPhone 8 预约立减100",shareAppContent:"十周年 翘首以待！1元预约立减100，每日限抽取5名成功分享用户获iPhone 8大奖，限时秒杀iPhone 6 仅1999元！",shareAppImage:"http://f.51fanbei.com/h5/app/activity/09/iphone8_06.jpg",shareAppUrl:domainName+"/fanbei-web/activity/iphone8Share?modelId="+modelId,isSubmit:"Y",sharePage:"iphone8Share"};return JSON.stringify(t)}var speed=40,cont=$(".roll").html();$(".copyRoll").html(cont),hello();var currentStarmp=(new Date).getTime(),firstStarmp=Date.parse(new Date("2017/09/20 00:00:00")),secondStarmp=Date.parse(new Date("2017/09/21 00:00:00")),thirdStarmp=Date.parse(new Date("2017/09/23 00:00:00")),fourthStarmp=Date.parse(new Date("2017/09/25 00:00:00"));currentStarmp>=firstStarmp&&currentStarmp<secondStarmp&&addStyle(1),currentStarmp>=secondStarmp&&currentStarmp<thirdStarmp&&(addStyle(2),$(".limitTime").show()),currentStarmp>=fourthStarmp&&addStyle(3);var modelId=getUrl("modelId"),protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#iphone8",data:{content:{},prizeCont:{},orderStatus:"",loginStatus:"",ruleShow:""},created:function(){this.logData(),this.loginPrize()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo",data:{modelId:modelId},success:function success(data){self.content=eval("("+data+")").data.activityList.slice(0,3),console.log(eval("("+data+")").data),self.content.firstList=self.content[0].activityGoodsList,self.content.secondList=self.content[1].activityGoodsList,self.content.thirdList=self.content[2].activityGoodsList,console.log(self.content),self.$nextTick(function(){$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}})},loginPrize:function loginPrize(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activity/getActivityGoods",success:function success(data){data=eval("("+data+")"),console.log(data),self.prizeCont=data.winUsers,self.orderStatus=data.status,self.loginStatus=data.loginStatus},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(t){"SELFSUPPORT"==t.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t.goodsId+'"}'},orderClick:function(t){var e=this;"N"==e.loginStatus?window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN":"FAIL"==e.orderStatus&&(window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}')},ruleClick:function(){this.ruleShow="Y"},maskClick:function(){this.ruleShow=""}}});
//# sourceMappingURL=../../_srcmap/activity/08/iphone8.js.map
