"use strict";var protocol=window.location.protocol,host=window.location.host,urlHost=protocol+"//"+host,goodsId="",goodsType="",vm=new Vue({el:"#barginIndex",data:{shareTime:0,totalData:{},firstGoods:{},ruleFlag:!1,downTime:{d:0,h:0,m:0,s:0},couponNum:0,isWX:!1,pageNo:1,sureFlag:!1,goodsId:0,cutData:"",goodsType:""},created:function(){this.logData(),this.judge()},computed:{couponNum:function(){return this.firstGoods.couponList?this.firstGoods.couponList.filter(function(o,t){return 0===o.state}).length:0}},methods:{judge:function(){"micromessenger"==window.navigator.userAgent.toLowerCase().match(/MicroMessenger/i)?this.isWX=!0:this.isWX=!1},logData:function(){var o=this;$.ajax({url:"/activity/de/goods",type:"POST",dataType:"json",success:function(t){if(!t.success)return requestMsg("哎呀，出错了！"),!1;o.totalData=t.data,o.getFirstData(),o.countDown(),o.getShareTimes(),$(".loadingMask").fadeOut()},error:function(){requestMsg("哎呀，出错了！")}})},getFirstData:function(){for(var o=this,t=o.totalData.goodsList,e=t.length-1;e>=0;e--)if(1==t[e].type){o.firstGoods=t[e];break}},getShareTimes:function(){var o=this,t=o.totalData.goodsList;console.dir(o.totalData,o.shareTime);for(var e=0;e<t.length;e++)1==t[e].share&&1!=t[e].type&&o.shareTime++},showRule:function(){this.ruleFlag=!0},closeRule:function(){this.ruleFlag=!1},closeSure:function(){this.sureFlag=!1},share:function(o,t){if(goodsId=o,goodsType=t,"product"==t){if(this.shareTime>=2)return requestMsg("只能砍价两件商品，不要太贪心哦"),!1;this.sureFlag=!0}else this.shareSure()},shareSure:function(){if(this.sureFlag=!1,this.isWX)requestMsg("请点击右上角进行分享");else{var o={shareAppTitle:"51返呗邀请有礼，快来参与~",shareAppContent:"我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",shareAppImage:"https://f.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+goodsType+"&userName="+getInfo().userName+"&testUser="+getInfo().userName,isSubmit:"Y",sharePage:"barginIndex"};o=JSON.stringify(o);var t=BASE64.encoder(o);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+t}},toList:function(o){location.href="/fanbei-web/activity/barginList?goodsId="+o},toProduct:function(o,t){location.href="/fanbei-web/activity/barginProduct?goodsId="+o+"&productType="+t+"&testUser="+getInfo().userName},countDown:function(){var o=this;setInterval(function(){var t=(o.totalData.endTime-(new Date).getTime())/1e3,e=0,s=0,a=0,i=0;t>=0&&(e=Math.floor(t/60/60/24),s=Math.floor(t/60/60%24),a=Math.floor(t/60%60),i=Math.floor(t%60)),o.downTime.d=e,o.downTime.h=s,o.downTime.m=a,o.downTime.s=i},1e3)},couponClick:function(o){var t=o.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:t},success:function(o){return console.log("假装领取了优惠券。couponClickReturn=",o),!1},error:function(){requestMsg("哎呀，出错了！")}})},buy:function(){this.isWX?location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+goodsId+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/11/barginIndex.js.map
