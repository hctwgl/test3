"use strict";var userName=getUrl("userName"),vm=new Vue({el:"#bankBox",data:{allData:[],len:0,topLen:0},created:function(){this.logData(),this.maidian("card",userName)},mounted:function(){},methods:{logData:function logData(){var self=this;$.ajax({url:"/app/activity/getHotBanksInfo",type:"GET",success:function success(data){data=eval("("+data+")"),data.success?(self.allData=data.data,self.len=data.data.creditbanner.length,self.topLen=data.data.lunbanner.length,self.$nextTick(function(){if(self.topLen>1){new Swiper(".bannerSwiper",{loop:!0,autoplay:5e3,autoplayDisableOnInteraction:!1,pagination:".mypagination1",observer:!0,observeParents:!0}).update()}new Swiper(".bankSwiper",{pagination:".mypagination2",observer:!0,observeParents:!0}).update()})):requestMsg("哎呀，出错了！")},error:function(){requestMsg("哎呀，出错了！")}})},jump:function(a,e,t){this.maidian(t,e),location.href=a},maidian:function(a,e){$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/card?type="+a+"&bank="+e},success:function(a){console.log(a)}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/card.js.map