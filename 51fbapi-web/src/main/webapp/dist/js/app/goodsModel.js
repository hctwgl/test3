"use strict";var activityId=getUrl("activityId"),windowW=$(window).outerWidth();new Vue({el:"#vueCon",data:{tableUrl:"/fanbei-web/newEncoreActivityInfo",content:[]},created:function(){this.logData()},methods:{buttonTxt:function(t){return"Y"==t.doubleRebate?"双倍返:￥"+t.rebateAmount/2+"×2":t.nperMap?1==t.nperMap.isFree?"月供:￥"+t.nperMap.freeAmount+"起":"月供:￥"+t.nperMap.amount+"起":"立即购买"},priceTxt:function(t){return t.remark?t.remark:"原价"},logData:function(){var t=this;$.ajax({url:t.tableUrl,type:"post",data:{activityId:activityId},success:function(n){t.content=JSON.parse(n),console.log(t.content),t.$nextTick(function(){$("body").css("background",t.content.data.bgColor),$(".monthPrice").css("background",t.content.data.btnColor),lazy.init()})}})}}});
//# sourceMappingURL=../../_srcmap/js/app/goodsModel.js.map
