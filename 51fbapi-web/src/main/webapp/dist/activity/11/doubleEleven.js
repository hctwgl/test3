"use strict";function addStyle(o){$(".active").eq(o).addClass("active01"),$(".active").eq(o).siblings().removeClass("active01"),$(".active").eq(o).find(".tangle").addClass("tangleOne"),$(".active").eq(o).find(".tangle").siblings().removeClass("tangleOne"),$(".active").find(".tangleTwo").eq(o).hide(),$(".active").find(".tangleTwo").eq(o).siblings().show()}window.onscroll=function(){var o=document.getElementById("doubleEleven-banner"),e=document.getElementById("doubleEleven-activity"),t=document.getElementById("doubleEleven-top"),a=o.offsetHeight,n=e.offsetHeight,i=t.offsetHeight,s=a+n+i,d=document.body.scrollTop,r=document.getElementById("navScroll");d>s?(r.style.position="fixed",r.style.top="0",r.style.left="0",r.style.zIndex="9999",r.style.background="red"):r.style.position="static"};var currentStarmp=(new Date).getTime(),oneTime=Date.parse(new Date("2017/11/1 00:00:00")),twoTime=Date.parse(new Date("2017/11/9 00:00:00")),threeTime=Date.parse(new Date("2017/11/12 00:00:00")),fourTime=Date.parse(new Date("2017/11/12 00:00:00")),fiveTime=Date.parse(new Date("2017/11/11 00:00:00")),sixTime=Date.parse(new Date("2017/11/13 00:00:00")),sevenTime=Date.parse(new Date("2017/11/10 00:00:00")),timerBig;currentStarmp<oneTime&&addStyle(0),currentStarmp>oneTime&&currentStarmp<twoTime&&addStyle(0),currentStarmp>=twoTime&&currentStarmp<fiveTime&&addStyle(1),currentStarmp>=fiveTime&&currentStarmp<sixTime&&addStyle(2),currentStarmp>=sixTime&&addStyle(3);var groupId=getUrl("groupId"),modelId=getUrl("modelId"),imgrooturl="https://f.51fanbei.com/h5/app/activity/11",vm=new Vue({el:"#doubleEleven",data:{content:"",isShow:!0,m:"",c:"",tab:1,allStartTime:"",productList:"",productListDetail:"",flag:!0,allData:[{name:"苹果",img:imgrooturl+"/brand-01.png",src:"https://app.51fanbei.com/fanbei-web/activity/iphoneX?modelId=143&addUiName=SHOWSHARE&title=iPhoneX新品预约"},{name:"vivo/OPPO",img:imgrooturl+"/brand-02.png",src:"https://app.51fanbei.com/fanbei-web/activity/oppoPic?activityId=4"},{name:"韩都衣舍",img:imgrooturl+"/brand-09.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=153"},{name:"DW",img:imgrooturl+"/brand-16.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=160"},{name:"nike",img:imgrooturl+"/brand-11.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=156"},{name:"addidas",img:imgrooturl+"/brand-12.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=157"},{name:"gxg",img:imgrooturl+"/brand-25.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=151"},{name:"兰蔻",img:imgrooturl+"/brand-24.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=164"},{name:"华为",img:imgrooturl+"/brand-03.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=148"},{name:"小米",img:imgrooturl+"/brand-04.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=149"},{name:"天梭",img:imgrooturl+"/brand-29.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=169"},{name:"lilbetter",img:imgrooturl+"/brand-19.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=152"},{name:"乐町",img:imgrooturl+"/brand-10.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=155"},{name:"欧莱雅",img:imgrooturl+"/paris.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=170"},{name:"马克华菲",img:imgrooturl+"/brand-05.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=150"},{name:"李宁",img:imgrooturl+"/brand-27.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=166"},{name:"CK",img:imgrooturl+"/brand-26.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=168"},{name:"newbalance",img:imgrooturl+"/brand-13.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=158"},{name:"拉夏贝尔",img:imgrooturl+"/brand-08.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=154"},{name:"Dickies",img:imgrooturl+"/brand-28.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=167"},{name:"Dior",img:imgrooturl+"/brand-17.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=161"},{name:"衣香丽影",img:imgrooturl+"/brand-21.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=165"},{name:"宾卡达",img:imgrooturl+"/brand-15.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=163"},{name:"鸿星尔克",img:imgrooturl+"/brand-14.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=159"}],arr:[[{name:"苹果",img:imgrooturl+"/brand-01.png",src:"https://app.51fanbei.com/fanbei-web/activity/iphoneX?modelId=143&addUiName=SHOWSHARE&title=iPhoneX新品预约"},{name:"vivo/OPPO",img:imgrooturl+"/brand-02.png",src:"https://app.51fanbei.com/fanbei-web/activity/oppoPic?activityId=4"},{name:"韩都衣舍",img:imgrooturl+"/brand-09.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=153"},{name:"DW",img:imgrooturl+"/brand-16.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=160"},{name:"nike",img:imgrooturl+"/brand-11.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=156"},{name:"addidas",img:imgrooturl+"/brand-12.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=157"},{name:"gxg",img:imgrooturl+"/brand-25.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=151"},{name:"兰蔻",img:imgrooturl+"/brand-24.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=164"}],[{name:"华为",img:imgrooturl+"/brand-03.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=148"},{name:"小米",img:imgrooturl+"/brand-04.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=149"},{name:"天梭",img:imgrooturl+"/brand-29.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=169"},{name:"lilbetter",img:imgrooturl+"/brand-19.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=152"},{name:"乐町",img:imgrooturl+"/brand-10.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=155"},{name:"欧莱雅",img:imgrooturl+"/paris.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=170"},{name:"马克华菲",img:imgrooturl+"/brand-05.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=150"},{name:"李宁",img:imgrooturl+"/brand-27.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=166"}],[{name:"CK",img:imgrooturl+"/brand-26.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=168"},{name:"newbalance",img:imgrooturl+"/brand-13.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=158"},{name:"拉夏贝尔",img:imgrooturl+"/brand-08.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=154"},{name:"Dickies",img:imgrooturl+"/brand-28.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=167"},{name:"Dior",img:imgrooturl+"/brand-17.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=161"},{name:"衣香丽影",img:imgrooturl+"/brand-21.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=165"},{name:"宾卡达",img:imgrooturl+"/brand-15.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=163"},{name:"鸿星尔克",img:imgrooturl+"/brand-14.png",src:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=159"}]]},created:function(){this.logData(),this.coupon(),this.countDown()},mounted:function(){this.$nextTick(function(){new Swiper(".swiper-container",{loop:!0,pagination:".swiper-pagination",nextButton:".swiper-button-next",prevButton:".swiper-button-prev",observer:!0,observeParents:!0}).update()})},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/partActivityInfoV1",data:{modelId:modelId},success:function success(data){var a=eval("("+data+")");self.productList=a.data.activityList,console.log(a)},error:function(){requestMsg("哎呀，出错了！")}})},goodClick:function(o){"SELFSUPPORT"==o.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o.goodsId+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+o.goodsId+'"}'},coupon:function coupon(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.content=eval("("+data+")").data,self.m=self.content.couponInfoList.slice(0,1),self.c=JSON.stringify(self.m),self.m=JSON.parse(self.c)}})},couponClick:function(o){var e=o.couponId;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:e},success:function(o){if(o.success)requestMsg("优惠劵领取成功");else{var e=o.data.status;"USER_NOT_EXIST"==e&&(window.location.href=o.url),"OVER"==e&&requestMsg("您已经领取，快去使用吧"),"COUPON_NOT_EXIST"==e&&requestMsg(o.msg),"MORE_THAN"==e&&requestMsg(o.msg)}},error:function(){requestMsg("哎呀，出错了！")}})},countDown:function(){var o=this;$.ajax({type:"post",url:"/activity/de/endtime",success:function(e){function t(o){var e=0,t=0,a=0,n=0;o>0&&(e=Math.floor(o/86400),t=Math.floor(o/3600)-24*e,a=Math.floor(o/60)-24*e*60-60*t,n=Math.floor(o)-24*e*60*60-60*t*60-60*a,t=t<10?"0"+t:t,a=a<10?"0"+a:a,n=n<10?"0"+n:n),$(".countTwo").html(e+"天 : "+t+"时 : "+a+"分 : "+n+"秒"),$(".blankOne").html(e),$(".blankTwo").html(t),$(".blankThree").html(a),$(".blankFour").html(n),p>=i&&$(".bargain").click(function(){window.location.href="barginIndex?doubleOne=barginOne",$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"barginIndex?doubleOne=barginOne"},success:function(o){console.log(o)}})}),0==e&&0==t&&0==a&&0==n&&($(".black-blank").html("00"),$(".bargain").unbind("click"),clearInterval(g),$(".countTwo").html("活动已结束"))}function a(o){var e=0,t=0,a=0,n=0;o>0&&(e=Math.floor(o/86400),t=Math.floor(o/3600)-24*e,a=Math.floor(o/60)-24*e*60-60*t,n=Math.floor(o)-24*e*60*60-60*t*60-60*a,t=t<10?"0"+t:t,a=a<10?"0"+a:a,n=n<10?"0"+n:n),$(".countThree").html(e+"天 : "+t+"时 : "+a+"分 : "+n+"秒"),I>=u&&I<=Date.parse("2017/11/11 20:00:20")&&$(".redRain").click(function(){window.location.href="redrain?doubleTwo=redOne",$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"redrain?doubleTwo=barginOne"},success:function(o){console.log(o)}})}),0==e&&0==t&&0==a&&0==n&&(clearInterval(M),$(".countThree").html("活动已结束"))}console.log(e),o.allStartTime=e.data.currentTime,console.log(o.allStartTime," self.allStartTime");var n=new Date("Nov 1,2017 00:00:00"),i=n.valueOf(),s=new Date("Nov 12,2017 00:00:00"),d=s.valueOf(),r=o.allStartTime,p=r.valueOf();console.log(p,"nowTimeStamp");var m=d-p,l=parseInt(m/1e3),g=void 0;!function(o){t(o),o--,g=setInterval(function(){console.log(333),t(o),o--},1e3)}(l);var c=new Date("nov 1,2017 00:00:00"),u=c.valueOf(),b=new Date("nov 9,2017 00:00:00"),f=b.valueOf(),h=o.allStartTime,I=h.valueOf();console.log(I,"nowTimeS");var v=f-I,w=parseInt(v/1e3),M=void 0;!function(o){a(w),w--,M=setInterval(function(){a(w),w--},1e3)}(),o.allStartTime>=Date.parse("2017/11/09 00:00:00")&&function(o){for(var e,t=["2017/11/09 10:00:00","2017/11/09 14:00:00","2017/11/09 20:00:00","2017/11/10 10:00:00","2017/11/10 14:00:00","2017/11/10 20:00:00","2017/11/11 10:00:00","2017/11/11 14:00:00","2017/11/11 20:00:00"],a=t.map(function(o,e){return Date.parse(o)}),n=0;n<a.length;n++)if(a[n]>o){e=a[n];break}var i=e-o;timerBig=setInterval(function(){i-=1e3;var o=new Date(i),e=o.getHours(),t=o.getMinutes(),a=o.getSeconds();e>=8?e-=8:e+=16,e=e<10?"0"+e:e,t=t<10?"0"+t:t,a=a<10?"0"+a:a,$(".countThree").text(e+"时："+t+"分："+a+"秒")},1e3)}(o.allStartTime),o.allStartTime>=Date.parse("2017/11/11 20:00:20")&&($(".redRain").unbind("click"),clearInterval(timerBig),$(".countThree").html("活动已结束"))}})},noShow:function(){this.isShow=!this.isShow,this.isShow?$(".processBar").show():$(".processBar").hide()},tabClick:function(o){this.tab=o+1},tabClickTwo:function(){var o=this;o.tab<=o.productList.length&&++o.tab,o.tab>5&&$(".xiba").css("transform","translateX(-"+3*(o.tab-5)/2+"rem)"),o.tab>o.productList.length&&($(".xiba").css("transform","translateX(0rem)"),o.tab=1)},toTop:function(){$(".backTop").click(function(){$("html,body").animate({scrollTop:0},500)})}}});
//# sourceMappingURL=../../_srcmap/activity/11/doubleEleven.js.map
