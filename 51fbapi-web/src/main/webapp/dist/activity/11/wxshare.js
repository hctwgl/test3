"use strict";var shareInfo={title:"51返呗邀请有礼，快来参与~",desc:"我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",link:urlHost+"/fanbei-web/activity/barginProduct?goodsId="+goodsId+"&productType=share"+goodsType+"&userName="+getInfo().userName+"&testUser="+getInfo().userName,imgUrl:"https://f.51fanbei.com/h5/common/icon/midyearCorner.png",success:function(){alert("分享成功！")},error:function(){alert("分享失败！")}};$(function(){$.ajax({url:"/wechat/getSign",type:"POST",dataType:"json",data:{url:shareInfo.link},success:function(e){console.log("result=",e);var n={debug:!0,appId:e.data.appId,timestamp:e.data.timestamp,nonceStr:e.data.nonceStr,signature:e.data.sign,jsApiList:["onMenuShareTimeline","onMenuShareAppMessage","onMenuShareQQ","onMenuShareWeibo","onMenuShareQZone"]};console.log("d=",n),wx.config(n)}})}),wx.ready(function(){wx.checkJsApi({jsApiList:["onMenuShareTimeline","onMenuShareAppMessage","onMenuShareQQ","onMenuShareWeibo","onMenuShareQZone"]}),wx.onMenuShareAppMessage(shareInfo),wx.onMenuShareTimeline(shareInfo),wx.onMenuShareQQ(shareInfo),wx.onMenuShareQZone(shareInfo),wx.onMenuShareWeibo(shareInfo)}),window.alert=function(e){var n=document.createElement("IFRAME");n.style.display="none",n.setAttribute("src","data:text/plain,"),document.documentElement.appendChild(n),window.frames[0].window.alert(e),n.parentNode.removeChild(n)};
//# sourceMappingURL=../../_srcmap/activity/11/wxshare.js.map
