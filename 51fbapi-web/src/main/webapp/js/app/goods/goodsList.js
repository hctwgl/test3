/*
* @Author: Yangyang
* @Date:   2017-02-15 16:08:03
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-02-15 19:38:24
* @title:  商品列表页
*/


// 点击tab切换

// 【1】
// var aLi=$(".goodsList_header li");
// var aDiv=$(".goodsList_main");

// for (var i = 0; i < aLi.length; i++) {
// 	aLi[i].index=i;
// 	$(aLi[i]).click(function(event) {
// 		for (var i = 0; i < aLi.length; i++) {
// 			aLi[i].className="";
// 			aDiv[i].css("display","none");
// 		}
// 		this.className="current";
// 		aDiv[this.index].css("display","block");
// 	});
// }




// 【2】
// var aLi=$(".goodsList_header li");
// var aDiv=$(".goodsList_main");

// for (var i = 0; i < aLi.length; i++) {
// 	aLi.eq[i]=i;
// 	$(aLi[i]).click(function(event) {

// 		for (var i = 0; i < aLi.length; i++) {
// 			var currentName=$(".item").hasClass('current');
// 			if (currentName) {
// 				aDiv.eq[i].css("display","block");
// 			}else{
// 				aDiv.eq[i].css("display","none");
// 			}
// 		}

// 	});
// }


// 【3】

// window.onload=function(){
	// var oTab=document.getElementById("tab");
	// var oLi=document.getElementsByTagName("item");
	// var oDiv=document.getElementsByTagName("goodsList_main");
	// for (var i = 0; i < oLi.length; i++) {
	// 	oLi[i].index=i;
	// 	oLi[i].onclick=function(){
	// 		for (var i = 0; i < oLi.length; i++) {
	// 			oLi[i].className="";
	// 			oDiv.style.display="none";
	// 		}
	// 		this.className="current";
	// 		oDiv[this.index].style.style="block";
	// 	};
	// }

// }


// window.onload = function() {
// 	var oTab = document.getElementById("tab");
// 	var aH3 = oTab.getElementsByTagName("h3");
// 	var aDiv = oTab.getElementsByTagName("div");
// 	for (var i = 0; i < aH3.length; i++) {
// 		aH3[i].index = i;
// 		aH3[i].onclick = function() {
// 			for (var i = 0; i < aH3.length; i++) {
// 				aH3[i].className = "";
// 				aDiv[i].style.display = "none";
// 			}
// 			this.className = "active";
// 			aDiv[this.index].style.display = "block";
// 		}
// 	}
// }



// 【4】
$(document).ready(function() {
  	//tab
    $("#setTab").setTab();
});

/*插件代码*/
(function ($) {
    $.fn.setTab = function () {
    	var getTab=$(this),//this指向调用函数的ID
        panels = getTab.children("div.tab_div").children("div"),
        tabs = getTab.find("li");
 
	    return this.each(function(){
	      	$(tabs).click(function(e) {
		        var index = $.inArray(this, $(this).parent().find("li"));//this指向li
		        if (panels.eq(index)[0]) {
		          	$(tabs).removeClass("current");
		          	$(this).addClass("current");
		          	panels.css("display", "none").eq(index).css("display", "block");
		        }
	      	});
	    });
 	}
})(jQuery);

















