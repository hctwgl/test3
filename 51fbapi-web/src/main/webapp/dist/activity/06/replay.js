"use strict";function alaShareData(){var e=domainName(),t={appLogin:"N",type:"share",shareAppTitle:"人气爆款专场",shareAppContent:"我抢到了一款爆款商品!\n点击查看",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:e+"/fanbei-web/activity/replay?replayShare=replayShare&activityId=13",isSubmit:"N",sharePage:"replay"};return JSON.stringify(t)}var activityId=getUrl("activityId"),vm=new Vue({el:"#replay",data:{content:{}},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/encoreActivityInfo",data:{activityId:13},success:function success(data){self.content=eval("("+data+")"),console.log(self.content),self.content=self.content.data,console.log(self.content),console.log(self.content.recommendGoodsList[0].goodsId)},error:function(){requestMsg("请求失败")}})},show:function(e){var t=this,a=getUrl("replayShare");window.location.href="replayShare"==a?"http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www":t.content.notifyUrl+'&params={"goodsId":"'+e+'"}'}}});
//# sourceMappingURL=../../_srcmap/activity/06/replay.js.map
