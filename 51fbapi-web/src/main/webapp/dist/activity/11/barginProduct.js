"use strict";function setShareType(){if(-1!==goodsType.indexOf("share")){var e=goodsType.split("share");goodsType=e[1]}return goodsType}var goodsId=getUrl("goodsId"),productType=getUrl("productType"),goodsType=getUrl("productType"),userName=getUrl("userName"),code=getUrl("code"),protocol=window.location.protocol,host=window.location.host,urlHost=protocol+"//"+host,spread=getUrl("spread"),vm=new Vue({el:"#barginProduct",data:{goodsData:{},friendData:[],ruleFlag:!1,barginFlag:!1,downTime:{d:0,h:0,m:0,s:0},productType:productType,isWX:!1,cutData:"",progressWidth:0,tipLeft:0,loadFlag:!1,shareFlag:!1,userInfoFlag:!0,ajaxFlag:!0,getUserFlag:!0,listNum:1,url_1:"/activity/de/goodsInfo",url_2:"/activity/de/friend",url_3:"/activity/de/share",userInfo:{}},created:function(){this.judge(),this.maidian()},methods:{judge:function(){if("micromessenger"==window.navigator.userAgent.toLowerCase().match(/MicroMessenger/i)){if(this.isWX=!0,this.url_1="/activityH5/de/goodsInfo",this.url_2="/activityH5/de/friend",this.url_3="/activityH5/de/share",!code||""==code){var e=encodeURIComponent(window.location.href.split("#")[0]),o="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx583e90560d329683&redirect_uri="+e+"&response_type=code&scope=snsapi_userinfo&state=1&connect_redirect=1#wechat_redirect";location.href=o}}else this.isWX=!1;var a=sessionStorage.getItem("userInfo");null==a?this.getUserInfo():this.userInfo=JSON.parse(a),this.logData(),this.listFn(),this.countDown(),this.scrollFn()},logData:function(){var e=this;$.ajax({url:e.url_1,type:"POST",dataType:"json",data:{goodsPriceId:goodsId,userName:userName},success:function(o){if(e.goodsData=o.data,e.progressWidth=6.3*e.goodsData.cutPrice/e.goodsData.originalPrice,e.tipLeft=6.3-e.progressWidth-1,e.getUserFlag&&$(".loadingMask").fadeOut(),!o.success)return e.isWX?e.toLogin():location.href=o.data.loginUrl,!1},error:function(){requestMsg("哎呀，出错了！")}})},getUserInfo:function(){var e=this;e.isWX&&""!=code&&(e.getUserFlag=!1,$.ajax({url:"/activity/de/wechat/userInfo",type:"POST",dataType:"json",data:{code:code},success:function(o){o.success?(e.userInfo=o,e.userInfo.data=JSON.parse(o.data),sessionStorage.setItem("userInfo",JSON.stringify(e.userInfo))):requestMsg("获取用户信息失败了！")},error:function(){requestMsg("哎呀，获取用户信息出错了！")},complete:function(){e.getUserFlag=!0,$(".loadingMask").fadeOut()}}))},listFn:function(){var e=this;e.loadFlag=!1,$.ajax({url:e.url_2,type:"POST",dataType:"json",data:{goodsPriceId:goodsId,pageNo:e.listNum,userName:userName},success:function(o){o.success?o.data.friendList.length>0&&(e.friendData=e.friendData.concat(o.data.friendList),e.listNum++,e.loadFlag=!0):e.isWX?e.toLogin():location.href=o.data.loginUrl},error:function(){requestMsg("哎呀，出错了！")}})},showRule:function(){this.ruleFlag=!0},closeRule:function(){this.ruleFlag=!1},closeShare:function(){this.shareFlag=!1},countDown:function(){var e=this;setInterval(function(){var o=(e.goodsData.endTime-(new Date).getTime())/1e3,a=0,t=0,s=0,i=0;o>=0&&(a=Math.floor(o/60/60/24),t=Math.floor(o/60/60%24),s=Math.floor(o/60%60),i=Math.floor(o%60)),e.downTime.d=a,e.downTime.h=t,e.downTime.m=s,e.downTime.s=i},1e3)},toList:function(){location.href="./barginList?goodsId="+goodsId+"&spread="+spread},toLogin:function(){location.href="./barginLogin?goodsId="+goodsId+"&spread="+spread},cut:function(){var e=this,o=e.userInfo;if(e.ajaxFlag){if(!o.success)return requestMsg("获取用户信息失败,无法参与砍价"),!1;e.ajaxFlag=!1,$.ajax({url:"/activityH5/de/cutPrice",type:"POST",dataType:"json",data:{userId:userName,goodsPriceId:goodsId,openId:o.data.openid,nickName:o.data.nickname,headImgUrl:o.data.headimgurl},success:function(o){e.cutData=o.data,e.barginFlag=!0},error:function(){requestMsg("哎呀，出错了！")},complete:function(){e.ajaxFlag=!0}})}},closeBargin:function(){this.barginFlag=!1},scrollFn:function(){var e=this;$(window).on("scroll",function(o){if(e.loadFlag){var a=$(this).scrollTop(),t=$(document).height();a+$(this).height()>=t-20&&e.listFn()}})},buy:function(){this.isWX?location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+this.goodsData.goodsId+'"}'},share:function(){var e=this;e.ajaxFlag&&(e.ajaxFlag=!1,$.ajax({url:e.url_3,type:"POST",dataType:"json",data:{goodsPriceId:goodsId},success:function(o){if(console.log("share=",o),!o.success)return o.hasOwnProperty("data")?e.isWX?location.href="./barginLogin?goodsId="+goodsId+"&spread="+spread:location.href=o.data.loginUrl:requestMsg("只能砍价两件商品，不要太贪心哦"),!1;if(e.isWX)e.shareFlag=!0;else{var a={shareAppTitle:"砍价赢免单丨iphone X0元免费领!",shareAppContent:"来不及解释啦，51返呗想送你iphone X，还差一点点，请帮我砍它~",shareAppImage:"http://f.51fanbei.com/online/17f31c95deb17587.png",shareAppUrl:urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+productType+"&userName="+getInfo().userName+"&spread="+spread,isSubmit:"Y",sharePage:"barginIndex"};a=JSON.stringify(a);var t=BASE64.encoder(a);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+t}},error:function(){requestMsg("哎呀，出错了！")},complete:function(){e.ajaxFlag=!0}}))},maidian:function(e){$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/barginProduct?userName="+userName+"&goodsId="+goodsId+"&type="+productType,spread:spread},success:function(e){console.log(e)}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/barginProduct.js.map
