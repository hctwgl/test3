"use strict";var goodsId=getUrl("goodsId"),productType=getUrl("productType"),goodsType=getUrl("productType"),userName=getUrl("userName"),code=getUrl("code"),protocol=window.location.protocol,host=window.location.host,urlHost=protocol+"//"+host,vm=new Vue({el:"#barginProduct",data:{goodsData:{},friendData:[],ruleFlag:!1,barginFlag:!1,downTime:{d:0,h:0,m:0,s:0},productType:productType,isWX:!1,cutData:"",progressWidth:0,tipLeft:0,loadFlag:!1,shareFlag:!1,userInfoFlag:!0,ajaxFlag:!0,getUserFlag:!0,listNum:1,url_1:"/activity/de/goodsInfo",url_2:"/activity/de/friend",url_3:"/activity/de/share",userInfo:{}},created:function(){this.judge()},methods:{judge:function(){if("micromessenger"==window.navigator.userAgent.toLowerCase().match(/MicroMessenger/i)){this.isWX=!0,this.url_1="/activityH5/de/goodsInfo",this.url_2="/activityH5/de/friend",this.url_3="/activityH5/de/share";var o=encodeURIComponent(window.location.href.split("#")[0]),e="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx583e90560d329683&redirect_uri="+o+"&response_type=code&scope=snsapi_userinfo&state=1&connect_redirect=1#wechat_redirect";code&&""!=code||(location.href=e)}else this.isWX=!1;this.logData(),this.listFn(),this.countDown()},logData:function(){var o=this;o.isWX&&""!=code&&(o.getUserFlag=!1,$.ajax({url:"/activity/de/wechat/userInfo",type:"POST",dataType:"json",data:{code:code},success:function(e){o.userInfo=e},error:function(){requestMsg("哎呀，获取用户信息出错了！")},complete:function(){o.getUserFlag=!0,$(".loadingMask").fadeOut()}})),$.ajax({url:o.url_1,type:"POST",dataType:"json",data:{goodsPriceId:goodsId,userName:userName},success:function(e){if(o.getUserFlag&&$(".loadingMask").fadeOut(),!e.success)return o.isWX?o.toLogin():location.href=e.data.loginUrl,!1;o.goodsData=e.data,o.progressWidth=6.3*o.goodsData.cutPrice/o.goodsData.originalPrice,o.tipLeft=6.3-o.progressWidth-.74},error:function(){requestMsg("哎呀，出错了！")}})},listFn:function(){var o=this;o.loadFlag=!1,$.ajax({url:o.url_2,type:"POST",dataType:"json",data:{goodsPriceId:goodsId,pageNo:o.listNum,userName:userName},success:function(e){e.success?e.data.friendList.length>0&&(o.friendData=o.friendData.concat(e.data.friendList),o.listNum++,o.loadFlag=!0):o.isWX?o.toLogin():location.href=e.data.loginUrl},error:function(){requestMsg("哎呀，出错了！")}})},showRule:function(){this.ruleFlag=!0},closeRule:function(){this.ruleFlag=!1},closeShare:function(){this.shareFlag=!1},countDown:function(){var o=this;setInterval(function(){var e=(o.goodsData.endTime-(new Date).getTime())/1e3,t=0,a=0,i=0,s=0;e>=0&&(t=Math.floor(e/60/60/24),a=Math.floor(e/60/60%24),i=Math.floor(e/60%60),s=Math.floor(e%60)),o.downTime.d=t,o.downTime.h=a,o.downTime.m=i,o.downTime.s=s},1e3)},toList:function(){location.href="./barginList?goodsId="+goodsId},toLogin:function(){location.href="./barginLogin?goodsId="+goodsId},cut:function(){var o=this,e=o.userInfo;if(o.ajaxFlag){if(!e.hasOwnProperty("openid"))return requestMsg("获取用户信息失败！"),!1;o.ajaxFlag=!1,$.ajax({url:"/activityH5/de/cutPrice",type:"POST",dataType:"json",data:{userId:userName,goodsPriceId:goodsId,openId:e.openid,nickName:e.nickname,headImgUrl:e.headimgurl},success:function(e){o.cutData=e.data,o.barginFlag=!0},error:function(){requestMsg("哎呀，出错了！")},complete:function(){o.ajaxFlag=!0}})}},closeBargin:function(){this.barginFlag=!1},scrollFn:function(){var o=this;$(window).on("scroll",function(e){if(o.loadFlag){var t=$(this).scrollTop(),a=$(document).height();t+$(this).height()>=a-20&&o.listFn()}})},buy:function(){this.isWX?location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+this.goodsData.goodsId+'"}'},share:function(){var o=this;o.ajaxFlag&&(o.ajaxFlag=!1,$.ajax({url:o.url_3,type:"POST",dataType:"json",data:{goodsPriceId:goodsId},success:function(e){if(console.log("share=",e),!e.success)return e.hasOwnProperty("data")?o.isWX?location.href="./barginLogin?goodsId="+goodsId:location.href=e.data.loginUrl:requestMsg("只能砍价两件商品，不要太贪心哦"),!1;if(o.isWX)o.shareFlag=!0;else{var t={shareAppTitle:"51返呗邀请有礼，快来参与~",shareAppContent:"我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",shareAppImage:"https://f.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+productType+"&userName="+getInfo().userName,isSubmit:"Y",sharePage:"barginIndex"};t=JSON.stringify(t);var a=BASE64.encoder(t);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+a}},error:function(){requestMsg("哎呀，出错了！")},complete:function(){o.ajaxFlag=!0}}))}}});
//# sourceMappingURL=../../_srcmap/activity/11/barginProduct.js.map
