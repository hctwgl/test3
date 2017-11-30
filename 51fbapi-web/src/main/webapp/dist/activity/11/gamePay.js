"use strict";function gameAreaTwo(e){var a=new mui.PopPicker({layer:2});a.setData(e),a.pickers[0].setSelectedIndex(0),a.pickers[1].setSelectedIndex(0),a.show(function(e){var t=e[0].text+"&nbsp"+e[1];$(".gameArea span").html(t),a.dispose()})}function gameAreaOne(e){var a=new mui.PopPicker({layer:1});a.setData(e),a.pickers[0].setSelectedIndex(0),a.show(function(e){var t=e[0];$(".gameArea span").html(t),a.dispose()})}var goodsId=getUrl("goodsId"),discout=getUrl("discout"),rebate=getUrl("rebate");$(function(){$(".footer").height("7%"),$(".gamePay").css("padding-top","3%");var e=$(window).height()-$(".footer").height()-.015*$(window).height();$(".allTop").height(e)});var vm=new Vue({el:"#gamePay",data:{content:{},fixCont:{},dataType:"",allData:[],initChooseFirst:"",allDataLen:"",discout:discout,rebate:rebate,needGameNumShow:""},created:function(){this.logData()},methods:{rewritedata:function(e){var a=e,t=Object.create(null),n=function(e,a,t){return t?$(e).find(a).attr(t):$(e).find(a)},i=function(e,a){return $(e).attr(a)};(n(a,"game","name")||i(a,"name"))&&(t.gameName=n(a,"game","name")||i(a,"name")),(n(a,"game","needGameAcct")||i(a,"needGameAcct"))&&(t.needGameAcct=n(a,"game","needGameAcct")||i(a,"needGameAcct"));var o=$(a).find("acctType").text();if(o?t.accountType=o:$(".gameNum").hide(),$(a).find("area")&&$(a).find("area").attr("name")){$(".gameArea").show();var s=n(a,"areas"),r=s.length;if(r&&r>=2&&(t.areasList=s.map(function(e,a){var t=n(a,"area"),o=[];return t.length&&t.each(function(e,a){o.push(i(a,"name"))}),{text:i(a,"name"),children:o}}),t.areasList=Array.prototype.slice.call(t.areasList),t.areaslen=r),r&&1==r){var l=$(a).find("area").length,c=[];l&&$(a).find("area").each(function(){c.push($(this).attr("name"))}),t.areasList=c,t.areaslen=r}}var m=[];n(a,"type","name")?($(a).find("type").each(function(){m.push($(this).attr("name"))}),t.typeNameList=m):n(a,"types","name")?($(a).find("types").each(function(){m.push($(this).attr("name"))}),t.typeNameList=m):$(".gameType").hide();var d=[],h=[];h=$(a).find("type").children("quantity").text().split(",");var p=[];p=$(a).find("type").children("chargeNum").text().split(",");for(var f=0;f<h.length;f++){var g={quantity:h[f],chargeNum:p[f]};d.push(g)}return t.priceTypeList=d,t.priceTimes=$(a).find("type").children("quantity").attr("priceTimes"),$(a).find("type").children("chargeNum").attr("name")?t.chargeNumName=$(a).find("type").children("chargeNum").attr("name"):t.chargeNumName="",t},logData:function(){var e=this;$.ajax({type:"post",url:"/game/pay/goodsInfo",data:{goodsId:goodsId},success:function(a){if(e.content=a.data.content,e.dataType=a.data.xmlType,console.log(e.content),e.$nextTick(function(){$(e.content).find("deposititem").attr("nameDesc")&&($(".nameDesc").html($(e.content).find("deposititem").attr("nameDesc")),$(".nameDesc01").attr("placeholder","请输入"+$(e.content).find("deposititem").attr("nameDesc"))),$(e.content).find("deposititem").attr("nameconfirmDesc")&&($(".nameconfirmDesc").html($(e.content).find("deposititem").attr("nameconfirmDesc")),$(".nameconfirmDesc01").attr("placeholder","请"+$(e.content).find("deposititem").attr("nameconfirmDesc")))}),$(e.content).find("game").each(function(a,t){e.allData.push(e.rewritedata(t))}),console.log(e.allData,"全部数据"),console.log(e.allData.length,"全部数据长度"),e.initChooseFirst=e.allData[0],e.initChooseFirst.areasList)for(var t=0;t<e.initChooseFirst.areasList.length;t++)e.initChooseFirst.areasList[0].text?e.initChooseFirst.initArea=e.initChooseFirst.areasList[0].text+" "+e.initChooseFirst.areasList[0].children[0]:e.initChooseFirst.initArea=e.initChooseFirst.areasList[0];console.log(e.allData[0],"默认显示第一个数据"),e.allDataLen=e.allData.length,1==e.allDataLen&&(e.fixCont=e.allData[0]),e.$nextTick(function(){$(".typeList li").eq(0).addClass("changeColor01"),$(".typeList li").eq(0).find("p").addClass("changeColor02"),$(".moneyList li").eq(0).addClass("changeColor01"),$(".moneyList li").eq(0).find("p").addClass("changeColor02"),$(".payMoney span").html($(".moneyList li").eq(0).find(".typePrice").html()),$(".fanMoney span").html(($(".moneyList li").eq(0).find(".pricePay").html()*rebate).toFixed(2)+"元"),$(".gameNum input").attr("placeholder","请输入"+e.initChooseFirst.accountType)}),"1"==e.initChooseFirst.needGameAcct&&(e.needGameNumShow=!0)},error:function(){requestMsg("哎呀，出错了！")}})},gameNameClick:function(){for(var e=this,a=[],t=0;t<e.allData.length;t++)e.allData[t].gameName&&a.push({value:t,text:e.allData[t].gameName});var n=new mui.PopPicker({layer:1});n.setData(a),n.pickers[0].setSelectedIndex(0),n.show(function(a){var t=a[0].text;$(".gameName:first-child span").html(t),$(".gameName:first-child span").css("color","#232323"),e.fixCont=e.allData[a[0].value],console.log(e.fixCont),"1"==e.fixCont.needGameAcct?e.needGameNumShow=!0:e.needGameNumShow=!1,n.dispose()})},gameAreaClick:function(){var e=this;console.log(e.fixCont,222),e.fixCont.areaslen&&(1==e.fixCont.areaslen&&gameAreaOne(e.fixCont.areasList),e.fixCont.areaslen>=2&&gameAreaTwo(e.fixCont.areasList))},gameTypeClick:function(e){$(".typeList li").eq(e).addClass("changeColor01"),$(".typeList li").eq(e).find("p").addClass("changeColor02"),$(".typeList li").eq(e).siblings().removeClass("changeColor01"),$(".typeList li").eq(e).siblings().find("p").removeClass("changeColor02")},gameMoneyClick:function(e){$(".moneyList li").eq(e).addClass("changeColor01"),$(".moneyList li").eq(e).find("p").addClass("changeColor02"),$(".moneyList li").eq(e).siblings().removeClass("changeColor01"),$(".moneyList li").eq(e).siblings().find("p").removeClass("changeColor02"),$(".payMoney span").html($(".moneyList li").eq(e).find(".pricePay").html()+"元"),$(".fanMoney span").html(($(".moneyList li").eq(e).find(".pricePay").html()*rebate).toFixed(2)+"元")},sureClick:function(){var e=this,a=void 0,t=void 0,n=void 0,i=void 0,o=void 0;"A"==e.dataType&&(a=$(".gameName:first-child").find("span").html(),t=$(".gameNum p").html(),n=$(".gameNum input").val(),i=$(".moneyList .changeColor02").find("i").html(),o=$(".changeColor01 .pricePay").html()),"B"==e.dataType&&(a=$(".gameName:first-child").find("span").html(),t=$(".nameDesc").html(),n=$(".nameDesc01").val(),i=$(".moneyList .changeColor02").find("i").html(),o=$(".changeColor01 .pricePay").html()),$.ajax({type:"post",url:"/game/pay/order",data:{goodsId:goodsId,gameName:a,acctType:t,userName:n,goodsNum:i,actualAmount:o},success:function(e){console.log(e,"确认充值")},error:function(){requestMsg("哎呀，出错了！")}})},fixStrToNum:function(e){return parseInt(e)},fixNum:function(e){return e.toFixed(2)}}});
//# sourceMappingURL=../../_srcmap/activity/11/gamePay.js.map
