"use strict";$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut(),console.log(2)}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)});var vue=new Vue({el:"#vueCon",data:{banner:{imageUrl:"https://img.51fanbei.com/h5/app/activity/wy/bbanner.png",content:"../../app/user/channelRegister?channelCode=wwyy&pointCode=wwyy"},content:{imgUrl:"https://img.51fanbei.com/h5/app/activity/05/mayMovie_01_2.jpg"},barShow:!0},created:function(){this.logData()},methods:{sp:function(n,t){return n.split(",")[t]},swiper:function(){var n=[this.content.tabList[0].name,this.content.tabList[1].name,this.content.tabList[2].name,this.content.tabList[3].name];new Swiper(".swiper-container",{loop:!0,autoHeight:!0,pagination:".swiper-pagination",paginationClickable:!0,paginationBulletRender:function(t,e,a){return'<span class="'+a+' bullet">'+n[e]+"</span>"}})},handleScroll:function(){jQuery(window).scrollTop()>=207?jQuery("#navWrap").addClass("fixTop"):jQuery("#navWrap").removeClass("fixTop")},maidian:function(n){$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/wyBorrow?type="+n},success:function(n){console.log(n)}})},logData:function logData(){var self=this;$.ajax({url:"/borrow/loanShop",type:"post",success:function success(data){self.content=eval("("+data+")"),self.content=self.content.data,self.content.tabList[0].loanShopList.unshift({iconUrl:"https://img.51fanbei.com/h5/app/activity/wy/bicon.png",linkUrl:"../../app/user/channelRegister?channelCode=wwyy&pointCode=wwyy",lsmIntro:"最高可借20000元，日息低至0.5元",lsmName:"爱上街-极速贷",lsmNo:"51fanbei",marketPoint:"205983,本月放款人数"}),console.log(self.content),""==self.content.scrollbar.content&&(self.barShow=!1),self.$nextTick(function(){self.swiper(),2==getBlatFrom()?(window.addEventListener("touchstart",self.handleScroll),window.addEventListener("touchmove",self.handleScroll),window.addEventListener("touchend",self.handleScroll)):window.addEventListener("scroll",self.handleScroll)})}})}}});
//# sourceMappingURL=../../_srcmap/activity/wy/wyBorrow.js.map
