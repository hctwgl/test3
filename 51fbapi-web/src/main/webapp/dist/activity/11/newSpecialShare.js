"use strict";var protocol=window.location.protocol,host=window.location.host,domainName=protocol+"//"+host,shareInfo={title:"20元话费、300M流量，3元超值购点击即领",desc:"51返呗超值新人礼：20元话费3元领，快来抢购吧",link:domainName+"/fanbei-web/activity/newUserShare",imgUrl:"https://f.51fanbei.com/h5/app/activity/11/newUser061.png",success:function(){requestMsg("分享成功！")},error:function(){requestMsg("分享失败！")},cancel:function(e){requestMsg("取消分享！")}};$(function(){$.ajax({url:"/wechat/getSign",type:"POST",dataType:"json",data:{url:encodeURIComponent(window.location.href.split("#")[0])},success:function(e){var n={debug:!1,appId:e.data.appId,timestamp:e.data.timestamp,nonceStr:e.data.nonceStr,signature:e.data.sign,jsApiList:["onMenuShareTimeline","onMenuShareAppMessage","onMenuShareQQ","onMenuShareWeibo","onMenuShareQZone"]};wx.config(n)}})}),wx.ready(function(){wx.checkJsApi({jsApiList:["onMenuShareTimeline","onMenuShareAppMessage","onMenuShareQQ","onMenuShareWeibo","onMenuShareQZone"]}),wx.onMenuShareAppMessage(shareInfo),wx.onMenuShareTimeline(shareInfo),wx.onMenuShareQQ(shareInfo),wx.onMenuShareQZone(shareInfo),wx.onMenuShareWeibo(shareInfo)});var vm=new Vue({el:"#newUser",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function(){var e=this;$.ajax({type:"post",url:"/activity/freshmanShare/homePage",success:function(n){console.log(n),e.content=n.data.goodsList,console.log(e.content,"self.content"),e.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"https://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}}),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newUser?type=newUser_ini"},success:function(e){console.log(e)}})},activityDetailClick:function(){this.ruleShow=!0,$("body").addClass("overFlowClick"),$("html").addClass("overFlowClick"),$(".alertRule").animate({left:"15.4%"},600)},closeClick:function(){this.ruleShow=!1,$("body").removeClass("overFlowClick"),$("html").removeClass("overFlowClick"),$(".alertRule").animate({left:"140%"},600)},down:function(){window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www",$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecialShare?type=down"},success:function(e){console.log(e)}})},noDown:function(){$(".alertApp").hide(),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecialShare?type=cansole"},success:function(e){console.log(e)}})},buyNowClick:function(e){$(".alertApp").show(),$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/newSpecialShare?type=alertApp"},success:function(e){console.log(e)}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/newSpecialShare.js.map
