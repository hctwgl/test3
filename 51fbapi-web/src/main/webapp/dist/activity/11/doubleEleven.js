"use strict";function addStyle(e){$(".active").eq(e).addClass("active01"),$(".active").eq(e).siblings().removeClass("active01"),$(".tangle").eq(e).addClass("tangleOne"),$(".tangle").eq(e).siblings().removeClass("tangleOne"),$(".tangle>.tangleTwo").eq(e).hide()}var currentStarmp=(new Date).getTime(),oneTime=Date.parse(new Date("2017/11/1 00:00:00")),twoTime=Date.parse(new Date("2017/11/9 00:00:00")),threeTime=Date.parse(new Date("2017/11/12 00:00:00")),fourTime=Date.parse(new Date("2017/11/12 00:00:00")),fiveTime=Date.parse(new Date("2017/11/11 00:00:00")),sixTime=Date.parse(new Date("2017/11/13 00:00:00"));console.log(new Date(currentStarmp)),currentStarmp<oneTime&&addStyle(0),currentStarmp>oneTime&&currentStarmp<=threeTime&&addStyle(0),currentStarmp>=twoTime&&currentStarmp<=threeTime&&addStyle(1),currentStarmp>=fiveTime&&currentStarmp<threeTime&&addStyle(2),currentStarmp>=sixTime&&addStyle(3),$(function(){function e(e){var t=0,a=0,o=0,n=0;e>0&&(t=Math.floor(e/86400),a=Math.floor(e/3600)-24*t,o=Math.floor(e/60)-24*t*60-60*a,n=Math.floor(e)-24*t*60*60-60*a*60-60*o),$(".countTwo").html(t+"天 : "+a+"时 : "+o+"分 : "+n+"秒"),$(".blankOne").html(t),$(".blankTwo").html(a),$(".blankThree").html(o),$(".blankFour").html(n),0==t&&0==a&&0==o&&0==n&&(window.location.href="/activity/barginIndex",$(".countTwo").html("活动已结束"))}function t(e){var t=0,a=0,o=0,n=0;e>0&&(t=Math.floor(e/86400),a=Math.floor(e/3600)-24*t,o=Math.floor(e/60)-24*t*60-60*a,n=Math.floor(e)-24*t*60*60-60*a*60-60*o),$(".countThree").html(t+"天 : "+a+"时 : "+o+"分 : "+n+"秒"),0==t&&0==a&&0==o&&0==n&&(window.location.href="",$(".countThree").html("活动已结束"))}var a=new Date("oct 29,2017 23:59:59"),o=a.valueOf(),n=new Date,r=n.valueOf(),i=o-r,s=parseInt(i/1e3);!function(t){e(t),t--,window.setInterval(function(){e(t),t--},1e3)}(s);var l=new Date("oct 28,2017 23:59:59"),m=l.valueOf(),c=new Date,g=c.valueOf(),d=m-g,u=parseInt(d/1e3);!function(e){t(e),e--,window.setInterval(function(){t(e),e--},1e3)}(u)});var groupId=getUrl("groupId"),modelId=getUrl("modelId"),imgrooturl="https://f.51fanbei.com/h5/app/activity/11",vm=new Vue({el:"#doubleEleven",data:{content:"",isShow:!0,m:"",c:"",tab:1,productList:"",productListDetail:"",allData:[{name:"苹果",img:imgrooturl+"/brand-01.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=187"},{name:"vivo/OPPO",img:imgrooturl+"/brand-02.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=186"},{name:"华为",img:imgrooturl+"/brand-03.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=185"},{name:"小米",img:imgrooturl+"/brand-04.png",src:"http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=183"},{name:"马克华菲",img:imgrooturl+"/brand-05.png",src:""},{name:"PLAYBOY",img:imgrooturl+"/brand-06.png",src:""},{name:"GUESS",img:imgrooturl+"/brand-07.png",src:""},{name:"拉夏贝尔",img:imgrooturl+"/brand-08.png",src:""},{name:"韩都衣舍",img:imgrooturl+"/brand-09.png",src:""},{name:"小米",img:imgrooturl+"/brand-10.png",src:""},{name:"NIKE",img:imgrooturl+"/brand-11.png",src:""},{name:"adidas",img:imgrooturl+"/brand-12.png",src:""},{name:"newbalance",img:imgrooturl+"/brand-13.png",src:""},{name:"鸿星尔克",img:imgrooturl+"/brand-14.png",src:""},{name:"宾卡达",img:imgrooturl+"/brand-15.png",src:""},{name:"DW",img:imgrooturl+"/brand-16.png",src:""},{name:"Dior",img:imgrooturl+"/brand-17.png",src:""},{name:"雅诗兰黛",img:imgrooturl+"/brand-18.png",src:""},{name:"lilbetter",img:imgrooturl+"/brand-19.png",src:""},{name:"速写",img:imgrooturl+"/brand-20.png",src:""},{name:"衣香丽影",img:imgrooturl+"/brand-21.png",src:""},{name:"妖精的口袋",img:imgrooturl+"/brand-22.png",src:""},{name:"后",img:imgrooturl+"/brand-23.png",src:""}],htmlStr:"",n:0,arr:[],mm:[[1,2,3],[2,3,4]]},created:function(){this.logData(),this.coupon();for(var e=new Array([]),t=0;t<=Math.floor(this.allData.length/8);t++)e[t]=[],e[t]=this.allData.slice(8*t,8*t+8);this.arr=e,console.log(this.arr.length)},mounted:function(){new Swiper(".swiper-container",{loop:!0,nextButton:".swiper-button-next",prevButton:".swiper-button-prev"}).update()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfo",data:{modelId:modelId},success:function success(data){var a=eval("("+data+")");self.productList=a.data.activityList,console.log(self.productList)},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(e){"SELFSUPPORT"==e.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+e.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+e.goodsId+'"}'},coupon:function coupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.content=eval("("+data+")").data,self.m=self.content.couponInfoList,self.c=JSON.stringify(self.m),self.m=JSON.parse(self.c),console.log(self.m)}})},couponClick:function(e){var t=e.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:t},success:function(e){if(e.success)requestMsg("优惠劵领取成功");else{var t=e.data.status;"USER_NOT_EXIST"==t&&(window.location.href=e.url),"OVER"==t&&requestMsg("您已经领取，快去使用吧"),"COUPON_NOT_EXIST"==t&&requestMsg(e.msg),"MORE_THAN"==t&&requestMsg(e.msg)}},error:function(){requestMsg("哎呀，出错了！")}})},noShow:function(){this.isShow=!this.isShow,this.isShow?$(".processBar").show():$(".processBar").hide()},tabClick:function(e){this.tab=e+1},tabClickTwo:function(){this.tab<this.productList.length&&++this.tab}}});
//# sourceMappingURL=../../_srcmap/activity/11/doubleEleven.js.map
