"use strict";var protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,vm=new Vue({el:"#newUser",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function(){var e=this;$.ajax({type:"post",url:"/activity/freshmanShare/homePage",success:function(a){console.log(a),e.content=a.data.goodsList,e.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"https://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecial?type=newSpecial_ini"},success:function(e){console.log(e)}})},activityDetailClick:function(){this.ruleShow=!0,$("body").addClass("overFlowClick"),$("html").addClass("overFlowClick"),$(".alertRule").animate({left:"15.4%"},600)},closeClick:function(){this.ruleShow=!1,$("body").removeClass("overFlowClick"),$("html").removeClass("overFlowClick"),$(".alertRule").animate({left:"140%"},600)},shareNowClick:function(){$.ajax({type:"post",url:"/activity/freshmanShare/isNew",success:function(e){if(console.log(e),e.msg&&"没有登录"==e.msg)window.location.href=e.data.loginUrl;else{var a='{"shareAppTitle":"20元话费、300M流量，3元超值购点击即领","shareAppContent":"51返呗超值新人礼：20元话费3元领，快来抢购吧~","shareAppImage":"https://f.51fanbei.com/h5/app/activity/11/newSpecial-02.png","shareAppUrl":"'+domainName+'/fanbei-web/activity/newSpecialShare","isSubmit":"Y","sharePage":"newSpecialShare"}',o=BASE64.encoder(a);window.location.href="/fanbei-web/opennative?name=APP_SHARE&params="+o}},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecial?type=shareNow"},success:function(e){console.log(e)}})},inviteNowClick:function(){$.ajax({type:"post",url:"/activity/freshmanShare/isNew",success:function(e){console.log(e),e.msg&&"没有登录"==e.msg?window.location.href=e.data.loginUrl:window.location.href="https://app.51fanbei.com/fanbei-web/app/newinvite"},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecial?type=inviteNow"},success:function(e){console.log(e)}})},buyNowClick:function(e){$.ajax({type:"post",url:"/activity/freshmanShare/isNew",success:function(a){console.log(a),a.msg&&"没有登录"==a.msg?window.location.href=a.data.loginUrl:"Y"==a.data.isNew?"SELFSUPPORT"==e.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+e.numId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+e.numId+'"}':"N"==a.data.isNew&&requestMsg("您已不是新用户，暂不能购买，可以去邀请朋友购买或参加邀请有礼活动")},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecial?type=buyNow"},success:function(e){console.log(e)}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/newSpecial.js.map
