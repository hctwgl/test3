"use strict";var goodsId=getUrl("goodsId"),productType=getUrl("productType"),goodsType=getUrl("productType"),userName=getUrl("testUser"),code=getUrl("code"),protocol=window.location.protocol,host=window.location.host,urlHost=protocol+"//"+host,vm=new Vue({el:"#barginProduct",data:{goodsData:{},friendData:[],ruleFlag:!1,barginFlag:!1,downTime:{d:0,h:0,m:0,s:0},productType:productType,isWX:!1,cutData:"",progressWidth:0,tipLeft:0,loadFlag:!1,listNum:1,url_1:"/activity/de/goodsInfo",url_2:"/activity/de/friend",userInfo:{}},created:function(){this.judge(),this.logData(),this.listFn(),this.countDown()},methods:{judge:function(){if("micromessenger"==window.navigator.userAgent.toLowerCase().match(/MicroMessenger/i)){this.isWX=!0,this.url_1="/activityH5/de/goodsInfo",this.url_2="/activityH5/de/friend";var o=encodeURIComponent(window.location.href.split("#")[0]);code||(location.href="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx583e90560d329683&redirect_uri="+o+"&response_type=code&scope=snsapi_userinfo&state=123&connect_redirect=1#wechat_redirect")}else this.isWX=!1},logData:function(){var o=this;$.ajax({url:o.url_1,type:"POST",dataType:"json",data:{goodsPriceId:goodsId,userName:userName},success:function(e){if($(".loadingMask").fadeOut(),!e.success)return o.isWX?requestMsg("哎呀，出错了！"):location.href=e.data.loginUrl,!1;o.goodsData=e.data,o.progressWidth=6.2*o.goodsData.cutPrice/o.goodsData.originalPrice,o.tipLeft=o.progressWidth-.74},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/activity/de/wechat/userInfo",type:"POST",dataType:"json",data:{code:code},success:function(e){e.success&&(o.userInfo=e.data)},error:function(){requestMsg("哎呀，出错了！")}})},listFn:function(){var o=this;o.loadFlag=!1,$.ajax({url:o.url_2,type:"POST",dataType:"json",data:{goodsPriceId:goodsId,pageNo:o.listNum,userName:userName},success:function(e){e.success?(console.log("frend=",e),e.data.friendList.length>0&&(o.friendData=o.friendData.concat(e.data.friendList),o.listNum++,o.loadFlag=!0)):o.isWX||(location.href=e.data.loginUrl)},error:function(){requestMsg("哎呀，出错了！")}})},showRule:function(){this.ruleFlag=!0},closeRule:function(){this.ruleFlag=!1},countDown:function(){var o=this;setInterval(function(){var e=(o.goodsData.endTime-(new Date).getTime())/1e3,t=0,a=0,i=0,s=0;e>=0&&(t=Math.floor(e/60/60/24),a=Math.floor(e/60/60%24),i=Math.floor(e/60%60),s=Math.floor(e%60)),o.downTime.d=t,o.downTime.h=a,o.downTime.m=i,o.downTime.s=s},1e3)},toList:function(){location.href="./barginList?goodsId="+goodsId},toLogin:function(){location.href="./barginLogin?goodsId="+goodsId},cut:function(){var o=this,e=o.userInfo;if(!e.hasOwnProperty("openid"))return requestMsg("哎呀，出错了！"),!1;$.ajax({url:"/activityH5/de/cutPrice",type:"POST",dataType:"json",data:{userId:userName,goodsPriceId:goodsId,openId:e.openid,nickName:e.nickname,headImgUrl:e.headimgurl},success:function(e){o.barginFlag=!0,o.cutData=e},error:function(){requestMsg("哎呀，出错了！")}})},closeBargin:function(){this.barginFlag=!1},scrollFn:function(){var o=this;$(window).on("scroll",function(e){if(o.loadFlag){var t=$(this).scrollTop(),a=$(document).height();t+$(this).height()>=a-20&&o.listFn()}})},buy:function(){this.isWX?location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+goodsId+'"}'},share:function(){if(this.isWX)requestMsg("请点击右上角进行分享"),this.toProduct(goodsId,"product");else{var o={shareAppTitle:"51返呗邀请有礼，快来参与~",shareAppContent:"我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",shareAppImage:"https://f.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+productType+"&userName="+getInfo().userName+"&testUser="+getInfo().userName,isSubmit:"Y",sharePage:"barginIndex"};o=JSON.stringify(o);var e=BASE64.encoder(o);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+e}}}});
//# sourceMappingURL=../../_srcmap/activity/11/barginProduct.js.map
