"use strict";var vm=new Vue({el:"#bankBox",data:{allData:[],len:0,topLen:0},created:function(){this.logData()},mounted:function(){this.$nextTick(function(){new Swiper(".bannerSwiper",{autoplay:5e3,pagination:".mypagination1",observer:!0,observeParents:!0}).update(),new Swiper(".bankSwiper",{autoplay:5e3,pagination:".mypagination2",observer:!0,observeParents:!0}).update()})},methods:{logData:function logData(){var self=this;$.ajax({url:"/app/activity/getHotBanksInfo",type:"GET",success:function success(data){data=eval("("+data+")"),data.success?(self.allData=data.data,self.len=data.data.creditbanner.length,self.topLen=data.data.lunbanner.length):requestMsg("哎呀，出错了！")},error:function(){requestMsg("哎呀，出错了！")}})},jump:function(a){location.href=a}}});
//# sourceMappingURL=../../_srcmap/activity/11/card.js.map
