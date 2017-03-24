/*
* @Author: Yangyang
* @Date:   2017-02-23 14:11:41
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-24 13:28:06
* @title:  商品列表页
*/



var typeCurrentNum = $("#typeCurrent").val(); // 获取当前的type类型
var modelIdNum = getUrl("modelId"); // 获取页面url里面的modelId参数
// 获取categoryList数组
var category = $("#categoryList").val();
var categoryObj = eval('(' + category + ')');

var notifyUrl = $("#notifyUrl").val(); // 获取ip地址

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

    // 如果li的个数小于一屏时ul的宽度
    if ( ulW<windowW ) {
        $(".goodsListModel_header").css("width", windowW+"px");
    }else{
        $(".goodsListModel_header").css("width", ulW+5+"px");
    }

    // 默认goodsList第一个位显示其他的都为隐藏
    $(".goodsListModel_main .goodsListModel_main_list:first-child").removeClass("goodsListModel_main_hide");

    // 点击导航事件
    $(".goodsListModel_header li").on('touchstart mousedown',function(e){
        e.preventDefault();

        var i = $(this).index();
        $(this).find("span").addClass("current");
        $(this).siblings().find("span").removeClass("current");

        $(".goodsListModel_main_list").eq(i).removeClass("goodsListModel_main_hide").siblings().addClass("goodsList_main_hide");
        
        typeCurrentNum =  categoryObj[i].type;
        ulOffsetLeft = $(".goodsListModel_header").offset().left;
        thisLiOffsetUl = liWArr[i].offsetLeft;
        thisLiOffsetDiv = thisLiOffsetUl + ulOffsetLeft; //距离 边框的距离
        var offsetLeft = ((windowW-liWArr[i].width)/2) - thisLiOffsetDiv;
        var offset = ulOffsetLeft+offsetLeft;
        if(offset >0){
            offset = 0;
        }else if(ulW<windowW){
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
                type: typeCurrentNum
            },
            success: function(returnData){

                if (returnData.success) {

                    var html = '';
                    var isUl = $('.goodsListModel_'+typeCurrentNum+'').find('ul').length;
                    
                    if (isUl <= 0) {

                        var goodsList = returnData.data["goodsList"];                        

                        for (var j = 0; j < goodsList.length; j++) {
                            // 售价
                            var saleAmount = toDecimal2(goodsList[j].saleAmount);
                            var amountAmountSplitArr =  saleAmount.split(".");
                            var amountAmountPriceInteger = amountAmountSplitArr[0];
                            var amountAmountPriceDecimal = amountAmountSplitArr[1];
                            // 返利
                            var rebateAmount = toDecimal2(goodsList[j].rebateAmount);
                            var rebateAmountSplitArr =  rebateAmount.split(".");
                            var rebateAmountPriceInteger = rebateAmountSplitArr[0];
                            var rebateAmountPriceDecimal = rebateAmountSplitArr[1];
                            var goodInfoUrl = notifyUrl+'&params={"goodsId":"'+goodsList[j].goodsId+'"}';

                            // 添加占位图
                            var imgUrl = '<img src="'+goodsList[j].goodsIcon+'">';
                            var goodsIconLength = goodsList[j].goodsIcon.length;
                            if (goodsIconLength <= 0) {
                                imgUrl = '<img class="zhanwei" src="/images/app/goods/0205zhanwei.png">'
                            }
                            
                            html+=  '<li class="fl goodsListModel_item bgc_white">'
                                        +'<a href='+goodInfoUrl+'>'
                                            +'<div class="goodsListModel_mainContent_img bgc_e">'
                                                +imgUrl
                                            +'</div>'
                                            +'<div class="goodsListModel_mainContent_main">'
                                                +'<div class="goodsListModel_mainContent_wrap">'
                                                    +'<p class="fs_26 fsc_1">'+goodsList[j].name+'</p>'
                                                    +'<p class="fs_26 fsc_red">'
                                                        +'<span>￥'+amountAmountPriceInteger+'</span>'+'<span class="fs_20">.'+amountAmountPriceDecimal+'</span>'
                                                    +'</p>'
                                                +'</div>'
                                                +'<div class="goodsListModel_mainContent_rebate_wrap">'
                                                    +'<div class="goodsListModel_mainContent_rebate clearfix">'
                                                        +'<span class="goodsListModel_rebate fl fs_26 bgc_orange fsc_f tac">返</span>'
                                                        +'<p class="fl fs_24 fsc_orange">'
                                                            +'<span>￥'+rebateAmountPriceInteger+'</span><span class="fs_20">.'+rebateAmountPriceDecimal+'</span>'
                                                        +'</p>'
                                                    +'</div>'
                                                +'</div>'
                                            +'</div>'
                                        +'</a>'
                                    +'</li>';                                   
                        }

                        var ulCurrent = $(".goodsListModel_mainContent").html('');
                        var ulCurrent = $(".goodsListModel_mainContent").append('<ul class="goodsListModel_mainContent clearfix content-slide goodsListModel_mainContent_'+typeCurrentNum+'">'+html+'</ul>');

                        var divCurrent = $('.goodsListModel_'+typeCurrentNum+'').html('');
                        $(divCurrent).append(ulCurrent);
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


// 上拉加载
$(function(){

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

    function pageNumber(page){
        for (var i = 0; i < categoryObj.length; i++) {
            pageNum = categoryObj[i].pageTotal;
            pageNum = pageNum-1;
            if(page==pageNum){ //最后一页
                sover=1;
                loadover();
            }
        }
    }

    var page = 1; // 默认页数从1开始
    //加载更多  
    function loadmore(obj){

        if(finished==0 && sover==0){

            var scrollTop = $(obj).scrollTop();  
            var scrollHeight = $(document).height();  
            var windowHeight = $(obj).height();
            
            finished=1; //防止未加载完再次执行

            page++;
            if (scrollTop + windowHeight -scrollHeight<=200 ) {
                $.ajax({
                    url: "/app/goods/categoryGoodsList",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        modelId : modelIdNum,
                        pageNo: page,
                        type: typeCurrentNum
                    },
                    success: function(returnData){
                        if (returnData.success) {

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

                                    // 售价
                                    var saleAmount = toDecimal2(goodsList[i].saleAmount);
                                    var amountAmountSplitArr =  saleAmount.split(".");
                                    var amountAmountPriceInteger = amountAmountSplitArr[0];
                                    var amountAmountPriceDecimal = amountAmountSplitArr[1];
                                    // 返利
                                    var rebateAmount = toDecimal2(goodsList[i].rebateAmount);
                                    var rebateAmountSplitArr =  rebateAmount.split(".");
                                    var rebateAmountPriceInteger = rebateAmountSplitArr[0];
                                    var rebateAmountPriceDecimal = rebateAmountSplitArr[1];
                                    var goodInfoUrl = notifyUrl+'&params={"goodsId":"'+goodsList[i].goodsId+'"}';

                                    // 添加占位图
                                    var imgUrl = '<img src="'+goodsList[i].goodsIcon+'">';
                                    var goodsIconLength = goodsList[i].goodsIcon.length;
                                    if (goodsIconLength <= 0) {
                                        imgUrl = '<img class="zhanwei" src="/images/app/goods/0205zhanwei.png">'
                                    }

                                    html+= '<li class="fl goodsListModel_item bgc_white">'
                                                +'<a href='+goodInfoUrl+'>'
                                                    +'<div class="goodsListModel_mainContent_img bgc_e">'
                                                        +imgUrl
                                                    +'</div>'
                                                    +'<div class="goodsListModel_mainContent_main">'
                                                        +'<div class="goodsListModel_mainContent_wrap">'
                                                            +'<p class="fs_28 fsc_1">'+goodsList[i].name+'</p>'
                                                            +'<span class="fs_26 fsc_red">'
                                                                +'<span class="fs_26 fsc_red">'
                                                                    +'<span>￥'+amountAmountPriceInteger+'</span>'+'<span class="fs_20">.'+amountAmountPriceDecimal+'</span>'
                                                                +'</span>'
                                                            +'</span>'
                                                        +'</div>'
                                                        +'<div class="goodsListModel_mainContent_rebate_wrap">'
                                                            +'<div class="goodsListModel_mainContent_rebate clearfix">'
                                                                +'<span class="goodsListModel_rebate fl fs_26 bgc_orange fsc_f tac">返</span>'
                                                                +'<p class="fl fs_24 fsc_orange">'
                                                                    +'<span>￥'+rebateAmountPriceInteger+'</span><span class="fs_20">.'+rebateAmountPriceDecimal+'</span>'
                                                                +'</p>'
                                                            +'</div>'
                                                        +'</div>'
                                                    +'</div>'
                                                +'</a>'
                                            +'</li>';
                                }
                                $(".goodsListModel_mainContent").append(html);
                            }                        
                        } else {
                            requestMsg(returnData.msg);
                        }
                        pageNumber(page);
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                });
            }
        }
    }

    // 下拉的时候加载
    $(window).scroll(function () {
        loadmore($(this));
    });
});