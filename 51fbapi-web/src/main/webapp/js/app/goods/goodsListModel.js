/*
* @Author: Yangyang
* @Date:   2017-02-23 14:11:41
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-18 19:21:46
* @title:  商品列表页
*/



var typeCurrentNum = $("#typeCurrent").val(); // 获取当前的type类型
var modelIdNum = getUrl("modelId"); // 获取modelId参数
// 获取categoryList数组
var category = $("#categoryList").val();
var categoryObj = eval('(' + category + ')');

// 获取ip地址
var notifyUrl = $("#notifyUrl").val();
var windowW = $(window).outerWidth(),
    liWArr = [], // 保存每一个li的索引(index),自身宽度(width),距离ul左侧的距离(offsetLeft)
    ulW = 0, // ul初始宽度  通过各个li宽度之和 计算出来
    finished = 0,
    page = 1; // 默认页数从1开始

//导航滑动
function Swipe(ele) {
    this.container = ele;
    this.element = this.container.children[0];
    this.distance=0;
    this.length = this.element.children.length;
    this.speed = 200;
    //执行对象中的handleEvent函数
    this.element.addEventListener("touchstart", this);
    this.element.addEventListener("touchmove", this);
}
Swipe.prototype = {
    handleEvent: function(a) {
        switch (a.type) {
            case "touchstart":
                this.onTouchStart(a);
                break;
            case "touchmove":
                this.onTouchMove(a);
                break;
        }
    },
    onTouchStart: function(a) {
        this.isScrolling = false;
        this.deltaX = 0;
        this.start = {
            pageX: a.touches[0].pageX,
            pageY: a.touches[0].pageY
        };
        this.element.style.MozTransitionDuration = this.element.style.webkitTransitionDuration = this.speed + "ms";
        this.startDistance=this.distance;
        a.stopPropagation()
    },
    onTouchMove: function(a) {
        if (a.touches.length > 1 || a.scale && a.scale !== 1) {
            return
        }
        this.deltaX = a.touches[0].pageX - this.start.pageX;
        (Math.abs(this.deltaX) > Math.abs(a.touches[0].pageY - this.start.pageY))?this.isScrolling = true:this.isScrolling = false;   //判断横滚还是竖滚
        if (this.isScrolling) {
            this.distance=this.startDistance-this.deltaX;
            if(this.distance<0){
                this.distance=0
            }else if(this.distance>this.element.clientWidth-windowW){
                this.distance=this.element.clientWidth-windowW
            }
            this.element.style.left = -this.distance + "px";
            a.stopPropagation()
        }
    }
};

new Swiper(document.getElementById('navWrap'));

var addModel = function addModel(goodsList) {
    var html = '';
    for (var j = 0; j < goodsList.length; j++) {
        // 售价
        var priceAmount = toDecimal2(goodsList[j].priceAmount);
        var amountAmountSplitArr = priceAmount.split(".");
        var amountAmountPriceInteger = amountAmountSplitArr[0];
        var amountAmountPriceDecimal = amountAmountSplitArr[1];
        // 返利
        var rebateAmount = toDecimal2(goodsList[j].rebateAmount);
        var rebateAmountSplitArr = rebateAmount.split(".");
        var rebateAmountPriceInteger = rebateAmountSplitArr[0];
        var rebateAmountPriceDecimal = rebateAmountSplitArr[1];
        var goodInfoUrl = notifyUrl + '&params={"goodsId":"' + goodsList[j].goodsId + '"}';
        html += "<li class=\"goodsListModel_item\">\n                                <a href=\"" + goodInfoUrl + "\">\n                                <img src=\"" + goodsList[j].goodsIcon + "\" class=\"mainContent_img\">\n                                <div class=\"goodsListModel_mainContent_wrap\">\n                                <p class=\"fs_26 fsc_1\">" + goodsList[j].name + "</p>\n                                <p class=\"fs_26 fsc_red\">\n                                <span>\uFFE5" + amountAmountPriceInteger + "</span><span class=\"fs_24\">." + amountAmountPriceDecimal + "</span>\n                                </p>\n                                </div>\n                                <div class=\"goodsListModel_mainContent_rebate_wrap\">\n                                <div class=\"goodsListModel_mainContent_rebate clearfix\">\n                                <span class=\"goodsListModel_rebate fl fs_24 bgc_orange fsc_f tac\">\u8FD4</span>\n                                <p class=\"fl fs_24 fsc_orange\">\n                                <span>\uFFE5" + rebateAmountPriceInteger + "</span><span class=\"fs_22\">." + rebateAmountPriceDecimal + "</span>\n                                </p>\n                                </div>\n                                </div>\n                                </a>\n                                </li>";
    }
    return html;
};

// 导航tab切换
$(function(){
    var mySwiper = new Swiper('.swiper-container', {
        onSlideChangeStart: function(swiper) {
            $(".nav li").eq(swiper.realIndex).click()
        }
    });
    $(".nav li").each(function(index){
        var thisLiW = $(this).outerWidth();
        liWArr.push({
            index: index,
            width: thisLiW,
            offsetLeft: ulW
        });
        ulW += thisLiW;
    });
    if ( ulW<windowW ) {
        $(".nav").css("width", windowW+"px");
    }else{
        $(".nav").css("width", ulW+5+"px");
    }

    // 点击导航事件
    $(".nav li").on('click',function(e){
        e.preventDefault();
        var i = $(this).index();
        $(this).find("span").addClass("current");
        $(this).siblings().find("span").removeClass("current");
        typeCurrentNum =  categoryObj[i].type;
        ulOffsetLeft = $(".nav").offset().left;
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
        }
        $(".nav").css({"left": offset + "px"});
        mySwiper.slideTo(i);
        var isUl = $("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").find('li').length;
        if(isUl<=0){
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
                        var goodsList = returnData.data["goodsList"];
                        console.log(returnData.data["goodsList"])
                        if(goodsList.length>0){
                            html=addModel(goodsList)
                        }else{html = '<div class="nullPrompt"> ' +
                            '<img src="/images/common/040101wuyouhui.png"> ' +
                            '<span style="margin-bottom: 2rem" class="fsc_6">暂无商品</span> ' +
                            '</div>';}
                        $("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").html(html);
                        $('.main_wrap').css('height',$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").height()+'px')

                    } else {
                        requestMsg(returnData.msg);
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
        }else{
            $('.main_wrap').css('height',$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").height()+'px')
        }

    });



    // 下拉的时候加载
    $(window).scroll(function () {
        if(finished==0){
            var scrollTop = $(this).scrollTop();
            var allHeight = $(document).height();
            var windowHeight = $(this).height();
            if (allHeight-windowHeight<=scrollTop+200) {
                page++;
                finished=1; //防止未加载完再次执行
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
                            if(returnData.data["goodsList"]==""){
                                requestMsg("没有更多了...");
                                if (page == 1) {
                                    $(".loadover").remove();
                                }
                            }else{
                                var goodsList = returnData.data["goodsList"];
                                var html =addModel(goodsList);
                                $("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").append(html);
                                finished=0
                            }
                        } else {
                            requestMsg(returnData.msg);
                        }
                        // pageNumber(page);
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                });
            }
        }

    });
});


