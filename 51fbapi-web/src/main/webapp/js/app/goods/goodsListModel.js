/*
* @Author: Yangyang
* @Date:   2017-02-23 14:11:41
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-02-24 16:14:28
* @title:  商品列表页
*/


// 导航固定在顶部
$(function(){

	$(window).scroll(function() {
		var scrollTopH= $(document).scrollTop();
		if ( scrollTopH >= 163) {
			$(".header_wrap").addClass("header_wrap_fixed");
		} else {
			$(".header_wrap").removeClass("header_wrap_fixed");
		}
	});

});


// $(function(){

//     var nav=$(".nav"); //得到导航对象

//     var win=$(window); //得到窗口对象

//     var sc=$(document);//得到document文档对象。

//     win.scroll(function(){

//         if(sc.scrollTop()>=60){

//             nav.addClass("fixednav"); 

//             $(".navTmp").fadeIn(); 

//         }else{

//             nav.removeClass("fixednav");

//             $(".navTmp").fadeOut();

//         }

//     })
// });






// 导航tab切换
$(function(){

    var liW = $(".goodsList_header li").outerWidth();
    var windowW = $(window).outerWidth();
    var centerLi = $(windowW-liW)/2;

    var liWArr = [], // 保存每一个li的索引(index),自身宽度(width),距离ul左侧的距离(offsetLeft)
    ulW = 0; // ul初始宽度  通过各个li宽度之和 计算出来
    $(".goodsList_header li").each(function(index){
        var thisLiW = $(this).outerWidth();
        liWArr.push({
            index: index,
            width: thisLiW,
            offsetLeft: ulW
        });
        ulW += thisLiW;
    });

    $(".goodsList_header").css("width", ulW+"px");
    $(".goodsList_header li").click(function(){

        var i = $(this).index();
        $(this).find("span").addClass("current");
        $(this).siblings().find("span").removeClass("current");
    	$(".goodsList_main_item").eq(i).removeClass("goodsList_main_hide").siblings().addClass("goodsList_main_hide");

        ulOffsetLeft = $(".goodsList_header").offset().left;
        thisLiOffsetUl = liWArr[i].offsetLeft;
        thisLiOffsetDiv = thisLiOffsetUl + ulOffsetLeft; //距离 边框的距离
        var offsetLeft = ((windowW-liWArr[i].width)/2) - thisLiOffsetDiv;
        var offset = ulOffsetLeft+offsetLeft;
        if(offset >0){
            offset = 0;
        }else if(offset<windowW-ulW){
            offset = windowW-ulW;
        };
		$(".goodsList_header").animate({
			"left": offset + "px"
		}, 200);
    });

});





















