"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName);var domainName=domainName(),vm=new Vue({el:"#oppoR11",data:{show:[!0,!1,!1,!1],url:domainName+'/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"121129"}',goodsMobileListMap:[]},created:function(){var e=this;$.ajax({url:"/app/activity/getSelfSupportGoodsInfo",dataType:"json",data:{userName:userName},type:"post",success:function(a){e.goodsMobileListMap=a.data,console.log(e.goodsMobileListMap)},error:function(){requestMsg("请求失败")}})},methods:{oppoList:function(e){for(var a=0;a<this.show.length;a++)Vue.set(vm.show,a,!1);Vue.set(vm.show,e-1,!0);var o=[121129,121130];this.url='https://app.51fanbei.com/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o[e-1]+'"}'},goGoodsDetail:function(e){window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+e+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/07/oppoR11.js.map
