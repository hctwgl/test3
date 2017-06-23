/*
* @Author: yoe
* @Date:   2017-02-23 14:11:41
* @Last Modified by:   yoe
* @Last Modified time: 2017-05-11 14:21:03
*/

let goodsList1 = $('#goodsList').val();
let goodsList=eval('(' + goodsList1 + ')'); //初始商品数据
console.log(goodsList);

let notifyUrl = $("#notifyUrl").val();//商品跳转原生的链接

let finished = 0;//防止多次请求ajax

//填充商品数据
let addModel = function addModel(goodsList,dom,state) {
    let con='',html = '';
    if(goodsList.length>0){
        // 下拉的时候加载
        for (let j = 0; j < goodsList.length; j++) {
            // 售价
            var saleAmount = toDecimal2(goodsList[j].saleAmount);
            var amountAmountSplitArr = saleAmount.split(".");
            var amountAmountPriceInteger = amountAmountSplitArr[0];
            var amountAmountPriceDecimal = amountAmountSplitArr[1];
            let con1=`<p class="fs_28 fsc_red" style="padding-top: .3rem;padding-bottom: .25rem">
                    <span>￥${amountAmountPriceInteger}</span><span class="fs_22">.${amountAmountPriceDecimal}</span>`;
            if(goodsList[j].goodsType==1){
                let amount=` <span>￥${goodsList[j].nperMap.amount}</span><span class="fs_22">×${goodsList[j].nperMap.nper}期</span>`;
                if(goodsList[j].nperMap.isFree==1){
                    amount=` <span>￥${goodsList[j].nperMap.freeAmount}</span><span class="fs_22">×${goodsList[j].nperMap.nper}期</span>`;
                }
                con=`<div class="goodsListModel_mainContent_rebate_wrap">
                    <div class="goodsListModel_mainContent_rebate clearfix">
                        <span class="goodsListModel_rebate fl fs_22 bgc_orange fsc_f tac">月供</span>
                        <p class="fl fs_24 fsc_orange">
                            ${amount}
                        </p>
                    </div>
                </div>`;
                con1=` <p class="fs_26 fsc_red">
                       <span>￥${amountAmountPriceInteger}</span><span class="fs_20">.${amountAmountPriceDecimal}</span>`
            }
            let goodInfoUrl = notifyUrl + '&params={"goodsId":"'+goodsList[j].goodsId+'"}';
            html += `<li class="goodsListModel_item">
                    <a href='${goodInfoUrl}'>
                        <img src=" ${goodsList[j].goodsIcon}" class="mainContent_img">
                        <div class="goodsListModel_mainContent_wrap">
                            <p class="fs_26 fsc_1">${goodsList[j].name}</p>
                            ${con1}
                                <span class="fs_24"><i class="ba">返</i>￥${goodsList[j].rebateAmount}</span>
                            </p>
                        </div>
                        ${con}
                    </a>
                </li>`;
        }
        finished=0;
    }else{
        html =  `<div class="nullPrompt">
            <img src="http://51fanbei.oss-cn-hangzhou.aliyuncs.com/h5/common/images/040101wuyouhui.png">
            <span style="margin-bottom: 2rem" class="fsc_6">暂无商品</span>
            </div>`;
    }
    if(state==1){
        dom.append(html);
    }else{
        dom.html(html);
    }
};
addModel(goodsList,$('#initGoods'));
// 获取categoryList数组

// 获取页面尺寸
var windowW = $(window).outerWidth(),
    page = 1; // 默认页数从1开始

//导航滑动
class Swipe{
    constructor(ele){
        this.container = ele;
        this.element = this.container.children[0];
        this.distance=0;
        this.length = this.element.children.length;
        this.speed = 200;
        //执行对象中的handleEvent函数
        this.element.addEventListener("touchstart", this);
        this.element.addEventListener("touchmove", this);
    }
    handleEvent(a) {
        switch (a.type) {
            case "touchstart":
                this.onTouchStart(a);
                break;
            case "touchmove":
                this.onTouchMove(a);
                break;
        }
    }
    onTouchStart(a) {
        this.isScrolling = false;
        this.deltaX = 0;
        this.start = {
            pageX: a.touches[0].pageX,
            pageY: a.touches[0].pageY
        };
        this.element.style.MozTransitionDuration = this.element.style.webkitTransitionDuration = this.speed + "ms";
        this.startDistance=this.distance;
        a.stopPropagation()
    }
    onTouchMove(a) {
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
}
new Swipe(document.getElementById('navWrap'));

// 导航tab切换
$(function(){
    // var mySwiper = new Swiper('.swiper-container', {        //tab切换
    //     onSlideChangeStart: function(swiper) {
    //         $(".nav li").eq(swiper.realIndex).click()
    //     }
    // });
    let ulW = 0, // ul初始宽度  通过各个li宽度之和 计算出来
        liWArr = []; // 保存每一个li的索引(index),自身宽度(width),距离ul左侧的距离(offsetLeft)

        $(".nav li").each(function(index){
        let thisLiW = $(this).outerWidth();
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
    let typeCurrentNum = $("#typeCurrent").val(); // 获取当前的type类型
    let modelIdNum = getUrl("modelId"); // 获取modelId参数

    // 点击导航事件
    $(".nav li").on('click',function(e){
        e.preventDefault();
        var i = $(this).index();
        $(this).find("span").addClass("current");
        $(this).siblings().find("span").removeClass("current");
        let categoryObj = eval('(' + $("#categoryList").val() + ')');
        typeCurrentNum =  categoryObj[i].type;
        var ulOffsetLeft = $(".nav").offset().left;
        var thisLiOffsetUl = liWArr[i].offsetLeft;
        var thisLiOffsetDiv = thisLiOffsetUl + ulOffsetLeft; //距离 边框的距离
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
        $(".swiper-slide").hide();
        $("div[data-type="+typeCurrentNum+"]").show();
        var isUl = $("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent");
        if(isUl.find('li').length<=0){
            $.ajax({
                url: "/app/goods/categoryGoodsList",
                type: "POST",
                dataType: "JSON",
                data: {
                    modelId : modelIdNum,
                    pageNo: 1,
                    type: typeCurrentNum
                },
                success: function(data){
                    if (data.success) {
                        addModel(data.data["goodsList"],isUl);
                    } else {
                        requestMsg(data.msg);
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
        }
    });
    //滚动加载更多商品
    $(window).on('scroll',function () {
        if(finished==0){
            var scrollTop = $(this).scrollTop();
            var allHeight = $(document).height();
            var windowHeight = $(this).height();
            if (allHeight-windowHeight<=scrollTop+400) {
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
                                var txt='<div class="loadOver"><span>没有更多了...</span></div>';
                                $("div[data-type="+typeCurrentNum+"]").append(txt);
                            }else{
                                var goodsList = returnData.data["goodsList"];
                                addModel(goodsList,$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent"),1);
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
