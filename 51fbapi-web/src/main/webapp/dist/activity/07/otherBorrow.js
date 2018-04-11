"use strict";
var vue=new Vue({
	el:"#vueCon",
	data:{
		content:{imgUrl:"https://img.51fanbei.com/h5/app/activity/05/mayMovie_01_2.jpg"},
		barShow:!0,
		systemType:3 //  systemType  1 ios,  2 and ,3 web   
	},
	created:function(){
		var u = navigator.userAgent;
		
        var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
        if(isAndroid){
        	this.systemType = 2;
        }
        
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if(isiOS){
        	this.systemType = 1;
        }
        
		this.logData(this.systemType)
	},
	methods:{
		sp:function(n,e){return n.split(",")[e]},imgSwiper:function(){new Swiper(".banner",{loop:!0,autoplay:4e3,pagination:".img-pagination"})},swiper:function(){var n=[this.content.tabList[0].name,this.content.tabList[1].name,this.content.tabList[2].name,this.content.tabList[3].name];new Swiper(".swiper-container",{loop:!0,autoHeight:!0,pagination:".swiper-pagination",paginationClickable:!0,paginationBulletRender:function(e,t,a){return'<span class="'+a+' bullet">'+n[t]+"</span>"}})},handleScroll:function(){jQuery(window).scrollTop()>=207?jQuery("#navWrap").addClass("fixTop"):jQuery("#navWrap").removeClass("fixTop")},
		logData:function logData(systemType){
			var self=this;$.ajax({
				url:"/borrow/loanShop",
				type:"post",
				data:{'systemType':systemType},
				success:function success(data){self.content=eval("("+data+")"),self.content=self.content.data,console.log(self.content),""==self.content.scrollbar.content&&(self.barShow=!1),self.$nextTick(function(){self.imgSwiper(),self.swiper(),console.log(self.divTop),2==getBlatFrom()?(window.addEventListener("touchstart",self.handleScroll),window.addEventListener("touchmove",self.handleScroll),window.addEventListener("touchend",self.handleScroll)):window.addEventListener("scroll",self.handleScroll)})}})}}});
//# sourceMappingURL=../../_srcmap/activity/07/otherBorrow.js.map
