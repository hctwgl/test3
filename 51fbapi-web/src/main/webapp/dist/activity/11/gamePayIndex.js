"use strict";var type=getUrl("type"),vm=new Vue({el:"#gamePayIndex",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function(){var t=this;$.ajax({type:"post",url:"/game/pay/goods",data:{type:type},success:function(e){t.content=e.data,console.log(t.content),t.$nextTick(function(){var t=$(window).width(),e=.056*t,o=$(".hotGameList li").length,a=parseInt(4*t/15),i=parseInt(.072*t),n=a*o+(o-1)*i+e;$(".hotGameList li").width(a),$(".hotGameList li").css("margin-right",i),$(".hotGameList li:last-child").css("margin-right",e),$(".hotCont").css("margin-left",e),$(".hotGameList").width(n),console.log(a,i,e,n)})},error:function(){requestMsg("哎呀，出错了！")}})},hotGameClick:function(t){var e=t.id;window.location.href="gamePay?goodsId="+e+"&discout="+t.discout+"&rebate="+t.rebate},fixNum:function(t){return t.toFixed(1)}}});
//# sourceMappingURL=../../_srcmap/activity/11/gamePayIndex.js.map
