"use strict";function addStyle(e){$(".active").eq(e).addClass("active01"),$(".active").eq(e).siblings().removeClass("active01"),$(".active").eq(e).find(".tangle").addClass("tangleOne"),$(".active").eq(e).find(".tangle").siblings().removeClass("tangleOne"),$(".active").find(".tangleTwo").eq(e).hide(),$(".active").find(".tangleTwo").eq(e).siblings().show()}var currentStarmp=(new Date).getTime(),oneTime=Date.parse(new Date("2017/11/1 00:00:00")),twoTime=Date.parse(new Date("2017/11/9 00:00:00")),threeTime=Date.parse(new Date("2017/11/12 00:00:00")),fourTime=Date.parse(new Date("2017/11/12 00:00:00")),fiveTime=Date.parse(new Date("2017/11/11 00:00:00")),sixTime=Date.parse(new Date("2017/11/13 00:00:00")),sevenTime=Date.parse(new Date("2017/11/10 00:00:00"));currentStarmp<oneTime&&addStyle(0),currentStarmp>oneTime&&currentStarmp<twoTime&&addStyle(0),currentStarmp>=twoTime&&currentStarmp<=sevenTime&&addStyle(1),currentStarmp>=fiveTime&&currentStarmp<threeTime&&addStyle(2),currentStarmp>sixTime&&addStyle(3);var groupId=getUrl("groupId"),modelId=getUrl("modelId"),imgrooturl="https://f.51fanbei.com/h5/app/activity/11",vm=new Vue({el:"#doubleEleven",data:{content:"",isShow:!0,m:"",c:"",tab:1,allStartTime:"",productList:"",productListDetail:"",allData:[{name:"苹果",img:imgrooturl+"/brand-01.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=187"},{name:"vivo/OPPO",img:imgrooturl+"/brand-02.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=186"},{name:"韩都衣舍",img:imgrooturl+"/brand-09.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=185"},{name:"DW",img:imgrooturl+"/brand-16.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=183"},{name:"nike",img:imgrooturl+"/brand-11.png",src:""},{name:"addidas",img:imgrooturl+"/brand-12.png",src:""},{name:"gxg",img:imgrooturl+"/brand-25.png",src:""},{name:"兰蔻",img:imgrooturl+"/brand-24.png",src:""},{name:"华为",img:imgrooturl+"/brand-03.png",src:""},{name:"小米",img:imgrooturl+"/brand-04.png",src:""},{name:"天梭",img:imgrooturl+"/brand-29.png",src:""},{name:"lilbetter",img:imgrooturl+"/brand-19.png",src:""},{name:"乐町",img:imgrooturl+"/brand-10.png",src:""},{name:"欧莱雅",img:imgrooturl+"/brand-29.png",src:""},{name:"马克华菲",img:imgrooturl+"/brand-05.png",src:""},{name:"李宁",img:imgrooturl+"/brand-27.png",src:""},{name:"CK",img:imgrooturl+"/brand-26.png",src:""},{name:"newbalance",img:imgrooturl+"/brand-13.png",src:""},{name:"拉夏贝尔",img:imgrooturl+"/brand-08.png",src:""},{name:"Dickies",img:imgrooturl+"/brand-28.png",src:""},{name:"Dior",img:imgrooturl+"/brand-17.png",src:""},{name:"衣香丽影",img:imgrooturl+"/brand-21.png",src:""},{name:"宾卡达",img:imgrooturl+"/brand-15.png",src:""},{name:"鸿星尔克",img:imgrooturl+"/brand-14.png",src:""}],arr:[[{name:"苹果",img:imgrooturl+"/brand-01.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=187"},{name:"vivo/OPPO",img:imgrooturl+"/brand-02.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=186"},{name:"韩都衣舍",img:imgrooturl+"/brand-09.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=185"},{name:"DW",img:imgrooturl+"/brand-16.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=183"},{name:"nike",img:imgrooturl+"/brand-11.png",src:""},{name:"addidas",img:imgrooturl+"/brand-12.png",src:""},{name:"gxg",img:imgrooturl+"/brand-25.png",src:""},{name:"兰蔻",img:imgrooturl+"/brand-24.png",src:""}],[{name:"华为",img:imgrooturl+"/brand-03.png",src:""},{name:"小米",img:imgrooturl+"/brand-04.png",src:""},{name:"天梭",img:imgrooturl+"/brand-29.png",src:""},{name:"lilbetter",img:imgrooturl+"/brand-19.png",src:""},{name:"乐町",img:imgrooturl+"/brand-10.png",src:""},{name:"欧莱雅",img:imgrooturl+"/brand-29.png",src:""},{name:"马克华菲",img:imgrooturl+"/brand-05.png",src:""},{name:"李宁",img:imgrooturl+"/brand-27.png",src:""}],[{name:"CK",img:imgrooturl+"/brand-26.png",src:""},{name:"newbalance",img:imgrooturl+"/brand-13.png",src:""},{name:"拉夏贝尔",img:imgrooturl+"/brand-08.png",src:""},{name:"Dickies",img:imgrooturl+"/brand-28.png",src:""},{name:"Dior",img:imgrooturl+"/brand-17.png",src:""},{name:"衣香丽影",img:imgrooturl+"/brand-21.png",src:""},{name:"宾卡达",img:imgrooturl+"/brand-15.png",src:""},{name:"鸿星尔克",img:imgrooturl+"/brand-14.png",src:""}]]},created:function(){this.logData(),this.coupon(),this.countDown()},mounted:function(){new Swiper(".swiper-container",{loop:!0,pagination:".swiper-pagination",autoplay:4e3,nextButton:".swiper-button-next",prevButton:".swiper-button-prev"}).update()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo",data:{modelId:modelId},success:function success(data){var a=eval("("+data+")");self.productList=a.data.activityList,console.log(self.productList,"self.productList")},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(e){"SELFSUPPORT"==e.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+e.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+e.goodsId+'"}'},coupon:function coupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.content=eval("("+data+")").data,self.m=self.content.couponInfoList.slice(0,1),self.c=JSON.stringify(self.m),self.m=JSON.parse(self.c),console.log(self.m,"self.m")}})},couponClick:function(e){var n=e.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:n},success:function(e){if(e.success)requestMsg("优惠劵领取成功");else{var n=e.data.status;"USER_NOT_EXIST"==n&&(window.location.href=e.url),"OVER"==n&&requestMsg("您已经领取，快去使用吧"),"COUPON_NOT_EXIST"==n&&requestMsg(e.msg),"MORE_THAN"==n&&requestMsg(e.msg)}},error:function(){requestMsg("哎呀，出错了！")}})},countDown:function(){var e=this;$.ajax({type:"post",url:"/activity/de/endtime",success:function(n){function t(e){a(e),e--,p=setInterval(function(){a(e),e--},1e3)}function a(e){var n=0,t=0,a=0,r=0;e>0&&(n=Math.floor(e/86400),t=Math.floor(e/3600)-24*n,a=Math.floor(e/60)-24*n*60-60*t,r=Math.floor(e)-24*n*60*60-60*t*60-60*a,t=t<10?"0"+t:t,a=a<10?"0"+a:a,r=r<10?"0"+r:r),$(".countTwo").html(n+"天 : "+t+"时 : "+a+"分 : "+r+"秒"),$(".blankOne").html(n),$(".blankTwo").html(t),$(".blankThree").html(a),$(".blankFour").html(r),l>=i&&$(".bargain").click(function(){window.location.href="barginIndex?double=barginOne"}),0==n&&0==t&&0==a&&0==r&&($(".black-blank").html("00"),$(".countTwo").html("活动已结束"),$(".bargain").unbind("click"),clearInterval(p))}function r(e){var n=0,t=0,a=0,r=0;e>0&&(n=Math.floor(e/86400),t=Math.floor(e/3600)-24*n,a=Math.floor(e/60)-24*n*60-60*t,r=Math.floor(e)-24*n*60*60-60*t*60-60*a,t=t<10?"0"+t:t,a=a<10?"0"+a:a,r=r<10?"0"+r:r),$(".countThree").html(n+"天 : "+t+"时 : "+a+"分 : "+r+"秒"),w>=b&&$(".redRain").click(function(){window.location.href="redrain?double=redOne"}),0==n&&0==t&&0==a&&0==r&&clearInterval(I)}console.log(n),e.allStartTime=n.data.currentTime,console.log(e.allStartTime,"self.allStartTime");var o=new Date("Nov 1,2017 00:00:00"),i=o.valueOf(),s=new Date("Nov 12,2017 00:00:00"),m=s.valueOf(),g=new Date,l=g.valueOf(),c=m-l,d=parseInt(c/1e3),p=void 0;t(d);var u=new Date("nov 1,2017 00:00:00"),b=u.valueOf(),f=new Date("nov 9,2017 00:00:00"),v=f.valueOf(),h=new Date,w=h.valueOf();console.log(w);var D=v-w,T=parseInt(D/1e3),I=void 0;!function(e){r(T),T--,I=setInterval(function(){r(T),T--},1e3)}(),w>=Date.parse("2017/11/09 00:00:00")&&function(e){for(var n,t=["2017/11/09 10:00:00","2017/11/09 14:00:00","2017/11/09 20:00:00","2017/11/10 10:00:00","2017/11/10 14:00:00","2017/11/10 20:00:00","2017/11/11 10:00:00","2017/11/11 14:00:00","2017/11/11 20:00:00"],a=t.map(function(e,n){return Date.parse(e)}),r=0;r<a.length;r++)if(console.log("1"),a[r]>e){n=a[r];break}var o=n-e;setInterval(function(){o-=1e3;var e=new Date(o),n=e.getHours(),t=e.getMinutes(),a=e.getSeconds();n>=8?n-=8:n+=16,n=n<10?"0"+n:n,t=t<10?"0"+t:t,a=a<10?"0"+a:a,$(".countThree").text(n+"时："+t+"分："+a+"秒")},1e3)}(w),e.allStartTime>Date.parse("2017/11/12 00:00:00")&&($(".countThree").html("活动已结束"),$(".redRain").unbind("click"),clearInterval(t))}})},noShow:function(){this.isShow=!this.isShow,this.isShow?$(".processBar").show():$(".processBar").hide()},tabClick:function(e){this.tab=e+1},tabClickTwo:function(){this.tab<this.productList.length&&++this.tab}}});
//# sourceMappingURL=../../_srcmap/activity/11/doubleEleven.js.map
