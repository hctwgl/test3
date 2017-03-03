/*
* @Author: Yangyang
* @Date:   2017-02-23 14:11:41
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-03 17:37:12
* @title:  商品列表页
*/


var pageTotal = $("#pageTotal").val();
var modelIdNum = $("#modelId").val();
var pageNoNum = 1;
pageNoNum++;
var typeCurrentNum = $("#typeCurrent").val();
var categoryListType = $("#categoryListType").val();

$(".goodsListModel_main .goodsListModel_main_list:first-child").removeClass('goodsListModel_hide');
$(".goodsListModel_main_list:first-child").siblings().html("");


// 导航tab切换
$(function(){

    var liW = $(".goodsListModel_header li").outerWidth();
    var windowW = $(window).outerWidth();
    var centerLi = $(windowW-liW)/2;
    var liWArr = [], // 保存每一个li的索引(index),自身宽度(width),距离ul左侧的距离(offsetLeft)
    ulW = 0; // ul初始宽度  通过各个li宽度之和 计算出来
    $(".goodsListModel_header li").each(function(index){
        var thisLiW = $(this).outerWidth();
        liWArr.push({
            index: index,
            width: thisLiW,
            offsetLeft: ulW
        });
        ulW += thisLiW;
    });

    $(".goodsListModel_header").css("width", ulW+0.5+"px");

    $(".goodsListModel_header li").click(function(){

        var i = $(this).index();
        $(this).find("span").addClass("current");
        $(this).siblings().find("span").removeClass("current");
    	$(".goodsListModel_main_item").eq(i).removeClass("goodsListModel_main_hide").siblings().addClass("goodsList_main_hide");

        ulOffsetLeft = $(".goodsListModel_header").offset().left;
        thisLiOffsetUl = liWArr[i].offsetLeft;
        thisLiOffsetDiv = thisLiOffsetUl + ulOffsetLeft; //距离 边框的距离
        var offsetLeft = ((windowW-liWArr[i].width)/2) - thisLiOffsetDiv;
        var offset = ulOffsetLeft+offsetLeft;
        if(offset >0){
            offset = 0;
        }else if(offset<windowW-ulW){
            offset = windowW-ulW;
        };
		$(".goodsListModel_header").animate({
			"left": offset + "px"
		}, 200);

        $.ajax({
            url: "/app/goods/categoryGoodsList",
            type: "POST",
            dataType: "JSON",
            data: {
                modelId : modelIdNum,
                pageNo: 1,
                type: categoryListType
            },
            success: function(returnData){

                if (returnData.success) {
                    var html = '';
                    var goodsList = returnData.data["goodsList"];
                    for (var i = 0; i < goodsList.length; i++) {

                        var priceAmount = toDecimal2(goodsList[i].priceAmount);
                        var amountAmountSplitArr =  priceAmount.split(".");
                        var amountAmountPriceInteger = amountAmountSplitArr[0];
                        var amountAmountPriceDecimal = amountAmountSplitArr[1];

                        var rebateAmount = toDecimal2(goodsList[i].rebateAmount);
                        var rebateAmountSplitArr =  rebateAmount.split(".");
                        var rebateAmountPriceInteger = rebateAmountSplitArr[0];
                        var rebateAmountPriceDecimal = rebateAmountSplitArr[1];

                        html+=  '<ul class="goodsListModel_mainContent clearfix">'
                                    +'<li class="fl bdc_cb goodsListModel_item">'
                                        +'<a href="'+goodsList[i].goodsUrl+'">'
                                            +'<img src="'+goodsList[i].thumbnailIcon+'" class="goodsListModel_mainContent_img">'
                                            +'<div class="goodsListModel_mainContent_main">'
                                                +'<div class="goodsListModel_mainContent_wrap">'
                                                    +'<p class="fs_28 fsc_1">'+goodsList[i].name+'</p>'
                                                    +'<span class="fs_26 fsc_red">'
                                                        +'<span class="fs_26 fsc_red">'
                                                            +'<span>￥'+amountAmountPriceInteger+'</span>'
                                                            +'<span class="fs_20">.'+amountAmountPriceDecimal+'</span>'
                                                        +'</span>'
                                                    +'</span>'
                                                +'</div>'
                                                +'<div class="goodsListModel_mainContent_rebate_wrap">'
                                                    +'<div class="goodsListModel_mainContent_rebate clearfix">'
                                                        +'<span class="fl fs_26 bgc_orange fsc_f tac">返</span>'
                                                        +'<p class="fl fs_24 fsc_orange">'
                                                            +'<span>￥'+rebateAmountPriceInteger+'</span>'
                                                            +'<span class="fs_20">.'+rebateAmountPriceDecimal+'</span>'
                                                        +'</p>'
                                                    +'</div>'
                                                +'</div>'
                                            +'</div>'
                                        +'</a>'
                                    +'</li>'
                                +'</ul>';
                        
                        var index=i+1;
                        $('.goodsListModel_main_list').eq(index).append(html);
                        $('.goodsListModel_main_list').eq(index).removeClass("goodsListModel_hide");
                        $('.goodsListModel_main_list').eq(index).siblings().addClass("goodsListModel_hide");
                    }

                } else {
                    requestMsg(returnData.msg);
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        });
    });
});







// 下拉加载
$(function(){

    var page = 1; // 默认页数从1开始
    var finished = 0; 
    var sover = 0;
    
    //加载完  
    function loadover(){
        if(sover==1){
            var overtext="没有更多了...";
            $(".loadmore").remove();
            if($(".loadover").length>0){
                $(".loadover span").eq(0).html(overtext);
            }else{
                var txt='<div class="loadover"><span>'+overtext+'</span></div>';
                $("body").append(txt);
            }
        }
    };

    //加载更多  
    function loadmore(obj){
        if(finished==0 && sover==0){
            var scrollTop = $(obj).scrollTop();
            var scrollHeight = $(document).height();
            var windowHeight = $(obj).height();
            if($(".loadmore").length==0){
                var txt='<div class="loadmore"><span class="loading"></span>加载中..</div>';
                $("body").append(txt);
            }
            //if (scrollTop + windowHeight -scrollHeight<=500 ) { //此处是滚动条到底部时候触发的事件，在这里写要加载的数据，或者是拉动滚动条的操作
                finished=1; //防止未加载完再次执行

                $.ajax({
                    url: "/app/goods/categoryGoodsList",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        modelId : modelIdNum,
                        pageNo: pageNoNum  ,
                        type: typeCurrentNum
                    },
                    success: function(returnData){
                        if (returnData.success) {
                            
                            console.log(returnData);

                            if(returnData==""){
                                sover = 1;
                                loadover();                  
                                if (page == 1) {
                                    $(".loadover").remove();
                                }
                            }else{
                                var html = '';
                                var goodsList = returnData.data["goodsList"];
                                for(var i = 0; i < goodsList.length; i++){

                                    var priceAmount = toDecimal2(goodsList[i].priceAmount);
                                    var amountAmountSplitArr =  priceAmount.split(".");
                                    var amountAmountPriceInteger = amountAmountSplitArr[0];
                                    var amountAmountPriceDecimal = amountAmountSplitArr[1];

                                    var rebateAmount = toDecimal2(goodsList[i].rebateAmount);
                                    var rebateAmountSplitArr =  rebateAmount.split(".");
                                    var rebateAmountPriceInteger = rebateAmountSplitArr[0];
                                    var rebateAmountPriceDecimal = rebateAmountSplitArr[1];

                                    html+= '<li class="fl bdc_cb goodsListModel_item">'
                                                +'<a href="'+goodsList[i].goodsUrl+'">'
                                                    +'<img src="'+goodsList[i].thumbnailIcon+'" class="goodsListModel_mainContent_img">'
                                                    +'<div class="goodsListModel_mainContent_main">'
                                                        +'<div class="goodsListModel_mainContent_wrap">'
                                                            +'<p class="fs_28 fsc_1">'+goodsList[i].name+'</p>'
                                                            +'<span class="fs_26 fsc_red">'
                                                                +'<span class="fs_26 fsc_red">'
                                                                    +'<span>￥'+amountAmountPriceInteger+'</span>'
                                                                    +'<span class="fs_20">.'+amountAmountPriceDecimal+'</span>'
                                                                +'</span>'
                                                            +'</span>'
                                                        +'</div>'
                                                        +'<div class="goodsListModel_mainContent_rebate_wrap">'
                                                            +'<div class="goodsListModel_mainContent_rebate clearfix">'
                                                                +'<span class="fl fs_26 bgc_orange fsc_f tac">返</span>'
                                                                +'<p class="fl fs_24 fsc_orange">'
                                                                    +'<span>￥'+rebateAmountPriceInteger+'</span>'
                                                                    +'<span class="fs_20">.'+rebateAmountPriceDecimal+'</span>'
                                                                +'</p>'
                                                            +'</div>'
                                                        +'</div>'
                                                    +'</div>'
                                                +'</a>'
                                            +'</li>';
                                }
                                $(".loadmore").remove();
                                $('.goodsListModel_mainContent').append(html);
                                page+=1;
                                finished=0;
                                if(page==pageTotal){ //最后一页
                                    sover=1;
                                    loadover();
                                }
                            }
                        } else {
                            requestMsg(returnData.msg);
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                });
            //}
        }
    }

    $(window).scroll(function () {
        var totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop());
        var documentheight = parseFloat($(document).height()); // 文本的高度
        if (documentheight - totalheight <= 200) {
            loadmore($(this));
        }
    });
});




