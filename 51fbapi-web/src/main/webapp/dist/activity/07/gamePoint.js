"use strict";function alaShareData(){var t=domainName(),e={appLogin:"N",type:"share",shareAppTitle:"特卖会",shareAppContent:"51返呗返场加购，精选好货抄低价！爆款精品仅在“特卖会”，拼的就是手速，赶紧来围观~",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:t+"/fanbei-web/activity/gamePoint?gamePointShare=gamePointShare&modelId="+modelId,isSubmit:"N",sharePage:"superGoods"};return JSON.stringify(e)}var modelId=getUrl("modelId"),vm=new Vue({el:"#gamePoint",data:{content:{},isActive:!0},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo?modelId="+modelId,data:{modelId:modelId},success:function success(data){self.content=eval("("+data+")").data,console.log(self.content.activityList[0].activityGoodsList),console.log(self.content.activityList[1])},error:function(){requestMsg("请求失败")}})},isshow:function(){this.isActive=!this.isActive},buyNow:function(t){var e=this,a=getUrl("gamePointShare");window.location.href="gamePointShare"==a?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":e.content.activityList+'&params={"modelId":"'+t+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/07/gamePoint.js.map
