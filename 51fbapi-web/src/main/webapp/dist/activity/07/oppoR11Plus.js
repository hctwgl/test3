"use strict";var activityId=getUrl("activityId"),domainName=domainName(),vm=new Vue({el:"#oppoR11",data:{show:[!0,!1,!1,!1],url:domainName+'/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"121129"}',goodsMobileListMap:[]},created:function(){var o=this;$.ajax({url:"/fanbei-web/encoreActivityInfo",dataType:"json",data:{activityId:activityId},type:"post",success:function(e){o.goodsMobileListMap=e.data,console.log(o.goodsMobileListMap)},error:function(){requestMsg("请求失败")}})},methods:{oppoList:function(o){for(var e=0;e<this.show.length;e++)Vue.set(vm.show,e,!1);Vue.set(vm.show,o-1,!0);var a=[121129,121130];this.url='https://app.51fanbei.com/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+a[o-1]+'"}'},goGoodsDetail:function(o){"SELFSUPPORT"==o.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+o.goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/07/oppoR11Plus.js.map
