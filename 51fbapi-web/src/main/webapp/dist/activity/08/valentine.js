"use strict";new Vue({el:"#valentine",data:{content:[],imgContent:[{imgRightUrl:"http://f.51fanbei.com/h5/valentine/val01.png",imgLeftUrl:"http://f.51fanbei.com/h5/valentine/val02.png",leftWord01:"99朵 红玫瑰花束",leftWord02:"吾爱你 心动正当时",rightWord01:"99朵 爱心混色花束",rightWord02:"吾爱你 心动正当时"},{imgRightUrl:"http://f.51fanbei.com/h5/valentine/val03.png",imgLeftUrl:"http://f.51fanbei.com/h5/valentine/vai04.png",leftWord01:"99朵 红玫瑰礼盒",leftWord02:"吾爱你 永恒记忆",rightWord01:"11朵 混色玫瑰花礼盒",rightWord02:"吾爱你 初心不变"},{imgRightUrl:"http://f.51fanbei.com/h5/valentine/val05.png",imgLeftUrl:"http://f.51fanbei.com/h5/valentine/val06.png",leftWord01:"永生花 坠入爱河",leftWord02:"吾爱你 永恒记忆",rightWord01:"一生只爱一人",rightWord02:"让小熊替我拥抱你"},{imgRightUrl:"http://f.51fanbei.com/h5/valentine/val07.png",imgLeftUrl:"http://f.51fanbei.com/h5/valentine/val08.png",leftWord01:"心印死心塌地礼盒",leftWord02:"心动小幸运 表达浓醇爱意",rightWord01:"KISSES心心相印512g",rightWord02:"创意拼搭 爱意浓浓"},{imgRightUrl:"http://f.51fanbei.com/h5/valentine/val09.png",imgLeftUrl:"http://f.51fanbei.com/h5/valentine/val10.png",leftWord01:"纯手工玫瑰熊",leftWord02:"我对你的爱表露于心",rightWord01:"甜蜜可爱趴趴熊",rightWord02:"手牵手一起走"}],titleContent:[{titleName:"求婚告白款",titleDes:"其实最好的日子无非是，你在闹他在笑温暖过一生"},{titleName:"钟情于“礼”款",titleDes:"时光苍老，你我不散 你永远是我人生剧集里的主角"},{titleName:"三生三世款",titleDes:"时光苍老，你我不散 你永远是我人生剧集里的主角"},{titleName:"甜蜜好礼款",titleDes:"怦然心动很简单 巧克力心意礼盒，开启甜蜜好食光"},{titleName:"一往情深款",titleDes:"有一种路叫做浪漫 那里有你给我的美好未来"}],couponCont:{}},created:function(){this.loginData(),this.logCoupon(),loading()},methods:{loginData:function loginData(){var self=this;$.ajax({url:"/fanbei-web/partActivityInfo",type:"post",data:{modelId:97},success:function success(data){self.content=eval("("+data+")").data.activityList.slice(0,5),console.log(self.content);for(var i=0;i<self.content.length;i++)self.content[i].imgRightUrl=self.imgContent[i].imgRightUrl,self.content[i].imgLeftUrl=self.imgContent[i].imgLeftUrl,self.content[i].leftWord01=self.imgContent[i].leftWord01,self.content[i].leftWord02=self.imgContent[i].leftWord02,self.content[i].rightWord01=self.imgContent[i].rightWord01,self.content[i].rightWord02=self.imgContent[i].rightWord02,self.content[i].titleName=self.titleContent[i].titleName,self.content[i].titleDes=self.titleContent[i].titleDes;self.$nextTick(function(){$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"http://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("请求失败")}})},goodClick:function(t){"SELFSUPPORT"==t.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t.goodsId+'"}'},logCoupon:function logCoupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:25},success:function success(data){self.couponCont=eval("("+data+")").data,console.log(self.couponCont)},error:function(){requestMsg("请求失败")}})},couponClick:function(t){var e=t.couponId;console.log(e),$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:e},success:function(t){if(t.success)requestMsg("优惠劵领取成功");else{var e=t.data.status;"USER_NOT_EXIST"==e&&(window.location.href=t.url),"OVER"==e&&(requestMsg(t.msg),requestMsg("优惠券个数超过最大领券个数")),"COUPON_NOT_EXIST"==e&&requestMsg(t.msg),"MORE_THAN"==e&&requestMsg(t.msg)}},error:function(){requestMsg("请求失败")}})}}});
//# sourceMappingURL=../../_srcmap/activity/08/valentine.js.map
