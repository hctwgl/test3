"use strict";var protocol=window.location.protocol,host=window.location.host,urlHost=protocol+"//"+host,goodsId="",goodsType="",userName=getUrl("userName"),vm=new Vue({el:"#barginIndex",data:{shareTime:0,totalData:{},firstGoods:{},ruleFlag:!1,downTime:{d:0,h:0,m:0,s:0},isWX:!1,pageNo:1,sureFlag:!1,shareFlag:!1,goodsId:0,cutData:"",goodsType:"",url_3:"/activity/de/share",url_1:"/activity/de/goods",url_2:"/fanbei-web/pickCoupon",couponData:[],ajaxFlag:!0},created:function(){this.judge()},computed:{couponNum:function(){return this.firstGoods.couponList?this.firstGoods.couponList.filter(function(e,t){return 0!=e.state}).length:0}},methods:{judge:function(){"micromessenger"==window.navigator.userAgent.toLowerCase().match(/MicroMessenger/i)?(this.isWX=!0,this.url_3="/activityH5/de/share",this.url_1="/activityH5/de/goods",this.url_2="/activityH5/de/pickCoupon"):this.isWX=!1,this.logData()},logData:function(){var e=this;$.ajax({url:e.url_1,type:"POST",dataType:"json",success:function(t){if(!t.success)return requestMsg("哎呀，出错了！"),!1;e.totalData=t.data,e.getFirstData(),e.countDown(),e.getShareTimes(),$(".loadingMask").fadeOut()},error:function(){requestMsg("哎呀，出错了！")}})},getFirstData:function(){for(var e=this,t=e.totalData.goodsList,o=t.length-1;o>=0;o--)if(1==t[o].type){e.firstGoods=t[o],e.couponData=t[o].couponList;break}},getShareTimes:function(){var e=this,t=e.totalData.goodsList;console.dir(e.totalData,e.shareTime);for(var o=0;o<t.length;o++)1==t[o].share&&1!=t[o].type&&e.shareTime++},showRule:function(){this.ruleFlag=!0},closeRule:function(){this.ruleFlag=!1},closeSure:function(){this.sureFlag=!1},closeShare:function(){this.shareFlag=!1},share:function(e,t,o){goodsId=e,goodsType=t,shareInfo.link=urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+goodsType+"&userName="+userName;var a=this;a.ajaxFlag&&(a.ajaxFlag=!1,$.ajax({url:a.url_3,type:"POST",dataType:"json",data:{goodsPriceId:e},success:function(e){if(!e.success)return e.hasOwnProperty("data")?a.isWX?location.href="./barginLogin":location.href=e.data.loginUrl:requestMsg(e.msg),!1;if("product"==t){if(a.shareTime>=2&&0==o)return requestMsg("只能砍价两件商品，不要太贪心哦"),!1;a.sureFlag=!0}else a.shareSure()},error:function(){requestMsg("哎呀，出错了！")},complete:function(){a.ajaxFlag=!0}}))},shareSure:function(){if(this.sureFlag=!1,this.isWX)this.shareFlag=!0;else{var e={shareAppTitle:"51返呗邀请有礼，快来参与~",shareAppContent:"我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",shareAppImage:"https://f.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+goodsType+"&userName="+getInfo().userName,isSubmit:"Y",sharePage:"barginIndex"};e=JSON.stringify(e);var t=BASE64.encoder(e);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+t}},toList:function(e){location.href="/fanbei-web/activity/barginList?goodsId="+e},toProduct:function(e,t){var o=userName||getInfo().userName;location.href="/fanbei-web/activity/barginProduct?goodsId="+e+"&productType="+t+"&userName="+o},countDown:function(){var e=this;setInterval(function(){var t=(e.totalData.endTime-(new Date).getTime())/1e3,o=0,a=0,s=0,r=0;t>=0&&(o=Math.floor(t/60/60/24),a=Math.floor(t/60/60%24),s=Math.floor(t/60%60),r=Math.floor(t%60)),e.downTime.d=o,e.downTime.h=a,e.downTime.m=s,e.downTime.s=r},1e3)},couponClick:function(e,t){var o=this,a=e.couponId;2==e.state?o.ajaxFlag&&(o.ajaxFlag=!1,$.ajax({url:o.url_2,type:"POST",dataType:"JSON",data:{couponId:a},success:function(e){if(e.success)requestMsg("优惠劵领取成功"),o.couponData[t].state;else{var a=e.data.status;"USER_NOT_EXIST"==a&&(o.isWx?location.href="./barginLogin":window.location.href=e.url),"OVER"==a&&requestMsg("您已经领取，快去使用吧"),"COUPON_NOT_EXIST"==a&&requestMsg(e.msg),"MORE_THAN"==a&&requestMsg(e.msg)}},error:function(){requestMsg("哎呀，出错了！")},complete:function(){o.ajaxFlag=!0}})):1==e.state&&requestMsg("您已经领取，快去使用吧")},buy:function(e){this.isWX?location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+e+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/11/barginIndex.js.map
