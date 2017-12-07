"use strict";var goodsId=getUrl("goodsId"),discout=getUrl("discout"),rebate=getUrl("rebate");$(function(){$("#recreationPay").height($(window).height()),$(".footer").height(.07*$(window).height()),$(".allTop").height(.93*$(window).height())});var vm=new Vue({el:"#recreationPay",data:{content:{},fixCont:{},dataType:"",allData:[],initChooseFirst:"",allDataLen:"",discout:discout,rebate:rebate,liIndex:"",maskShow:"",initPriceList:[]},created:function(){this.logData()},methods:{rewritedata:function(e){var a=e,t=Object.create(null),i=function(e,a,t){return t?$(e).find(a).attr(t):$(e).find(a)},n=function(e,a){return $(e).attr(a)};(i(a,"game","name")||n(a,"name"))&&(t.gameName=i(a,"game","name")||n(a,"name"));var o=$(a).find("acctType").text();o?t.accountType=o:$(".gameNum").hide();var s=[];i(a,"type","name")?($(a).find("type").each(function(){s.push($(this).attr("name"))}),t.typeNameList=s):i(a,"types","name")?($(a).find("types").each(function(){s.push($(this).attr("name"))}),t.typeNameList=s):$(".gameType").hide();for(var l=[],r=[],m=[],c=0;c<$(a).find("type").length;c++){r[c]=$(a).find("type").eq(c).children("quantity").text().split(","),m[c]=$(a).find("type").eq(c).children("chargeNum").text().split(",");for(var d=[],h=0;h<r[c].length;h++){var g={quantity:r[c][h],chargeNum:m[c][h]};d.push(g)}var p={chargeNumName:$(a).find("type").eq(c).children("chargeNum").attr("name")?$(a).find("type").eq(c).children("chargeNum").attr("name"):"",priceTimes:$(a).find("type").eq(c).children("quantity").attr("priceTimes"),list:d};l.push(p)}return t.priceTypeList=l,t},logData:function(){var e=this;$.ajax({type:"post",url:"/game/pay/goodsInfo",data:{goodsId:goodsId},success:function(a){e.content=a.data.content,e.dataType=a.data.xmlType,console.log(e.content),$(e.content).find("game").each(function(a,t){e.allData.push(e.rewritedata(t))}),console.log(e.allData,"全部数据"),console.log(e.allData.length,"全部数据长度"),e.fixCont=e.allData[0],console.log(e.allData[0],"默认显示第一个数据"),e.initPriceList=e.allData[0].priceTypeList[0],e.liIndex=0,e.allDataLen=e.allData.length,1==e.allDataLen&&(e.fixCont=e.allData[0]),e.$nextTick(function(){$(".gamePass input").attr("placeholder","请输入"+e.fixCont.accountType),$(".typeList li").eq(0).addClass("changeColor01"),$(".typeList li").eq(0).find("p").addClass("changeColor02"),$(".moneyList li").eq(0).addClass("changeColor01"),$(".moneyList li").eq(0).find("p").addClass("changeColor02"),$(".payMoney span").html($(".moneyList li").eq(0).find(".typePrice").html()),$(".fanMoney span").html(($(".moneyList li").eq(0).find(".pricePay").html()*rebate).toFixed(2)+"元")})},error:function(){requestMsg("哎呀，出错了！")}})},gameNameClick:function(){this.maskShow=!0,$(".nameCont").animate({bottom:0},400)},chooseName:function(e,a){var t=this;t.fixCont=t.allData[e],console.log(t.fixCont),$(".gameName:first-child").find("span").html(a.gameName),t.maskShow=!1,$(".nameCont").animate({bottom:"-8.7rem"},0)},gameTypeClick:function(e){var a=this;$(".typeList li").eq(e).addClass("changeColor01"),$(".typeList li").eq(e).find("p").addClass("changeColor02"),$(".typeList li").eq(e).siblings().removeClass("changeColor01"),$(".typeList li").eq(e).siblings().find("p").removeClass("changeColor02"),a.initPriceList=a.fixCont.priceTypeList[e],console.log(a.initPriceList,"000"),$(".payMoney span").html((a.initPriceList.list[0].quantity*discout).toFixed(2)+"元"),$(".fanMoney span").html((a.initPriceList.list[0].quantity*discout*rebate).toFixed(2)+"元")},gameMoneyClick:function(e){var a=this;$(".moneyList li").eq(e).addClass("changeColor01"),$(".moneyList li").eq(e).find("p").addClass("changeColor02"),$(".moneyList li").eq(e).siblings().removeClass("changeColor01"),$(".moneyList li").eq(e).siblings().find("p").removeClass("changeColor02"),$(".payMoney span").html($(".moneyList li").eq(e).find(".pricePay").html()+"元"),$(".fanMoney span").html(($(".moneyList li").eq(e).find(".pricePay").html()*rebate).toFixed(2)+"元"),a.liIndex=e},sureClick:function(){var e=this,a=void 0,t=void 0,i=void 0,n=void 0,o=void 0,s=void 0,l=void 0,r=void 0,m=void 0;$(".gamePass input").val()?(e.fixCont.priceTypeList&&(e.initPriceList.list[e.liIndex].quantity,e.initPriceList.priceTimes),s=$(".gameName").hasClass("needGameNum")?$(".needGameNum input").val():"",l=$(".gameName").hasClass("gameArea")?$(".gameArea span").html():"",m=$(".gameName").hasClass("gameService")?$(".gameService span").html():"",r=$(".payType").hasClass("gameType")?$(".typeList .changeColor02").html():"","A"==e.dataType&&(a=$(".gameName:first-child").find("span").html(),t=$(".gamePass p").html(),i=$(".gamePass input").val(),n=$(".changeColor01 .goodsNum").html(),o=$(".changeColor01 .pricePay").html()),"B"==e.dataType&&(a=$(".gameName:first-child").find("span").html(),t=$(".gamePass p").html(),i=$(".gamePass input").val(),n=$(".changeColor01 .goodsNum").html(),o=$(".changeColor01 .pricePay").html()),$.ajax({type:"post",url:"/game/pay/order",data:{goodsId:goodsId,gameName:a,acctType:t,userName:i,goodsNum:n,actualAmount:o,gameAcct:s,gameArea:l,gameSrv:m,gameType:r},success:function(e){console.log(e,"确认充值");var a=e.data.orderNo;window.location.href="gameOrderDetail?orderNo="+a},error:function(){requestMsg("哎呀，出错了！")}})):requestMsg("信息填写不完整！")},fixStrToNum:function(e){return parseInt(e)},fixNum:function(e){return e.toFixed(2)},maskClick:function(){this.maskShow=!1,$(".nameCont").animate({bottom:"-8.7rem"},0)}}});
//# sourceMappingURL=../../_srcmap/activity/11/recreationPay.js.map
