/**
 * Created by yoe on 2017/5/27.
 */

/*var addModel = function addModel(goodsList) {
    var html = '';
    for (var j = 0; j < goodsList.length; j++) {
        // 售价
        var saleAmount = toDecimal2(goodsList[j].saleAmount);
        var amountAmountSplitArr = saleAmount.split(".");
        var amountAmountPriceInteger = amountAmountSplitArr[0];
        var amountAmountPriceDecimal = amountAmountSplitArr[1];
        // 返利
        var rebateAmount = toDecimal2(goodsList[j].rebateAmount);
        var rebateAmountSplitArr = rebateAmount.split(".");
        var rebateAmountPriceInteger = rebateAmountSplitArr[0];
        var rebateAmountPriceDecimal = rebateAmountSplitArr[1];
        var goodInfoUrl = notifyUrl + '&params={"goodsId":"'+goodsList[j].goodsId+'"}';
        html += '<li class="goodsListModel_item">'
                +'<a href='+goodInfoUrl+'>'
                    +'<img src=" '+goodsList[j].goodsIcon+' " class="mainContent_img">'
                    +'<div class="goodsListModel_mainContent_wrap">'
                        +'<p class="fs_26 fsc_1">'+goodsList[j].name+'</p>'
                        +'<p class="fs_26 fsc_red">'
                            +'<span>￥'+amountAmountPriceInteger+'</span><span class="fs_24">.'+amountAmountPriceDecimal+'</span>'
                        +'</p>'
                    +'</div>'
                    +'<div class="goodsListModel_mainContent_rebate_wrap">'
                        +'<div class="goodsListModel_mainContent_rebate clearfix">'
                            +'<span class="goodsListModel_rebate fl fs_24 bgc_orange fsc_f tac">返</span>'
                            +'<p class="fl fs_24 fsc_orange">'
                                +'<span>￥'+rebateAmountPriceInteger+'</span><span class="fs_22">.'+rebateAmountPriceDecimal+'</span>'
                            +'</p>'
                        +'</div>'
                    +'</div>'
                +'</a>'
            +'</li>';
    }
    return html;
};*/

// 导航tab切换
/*$(function(){
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
                                $("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").append(addModel(goodsList));
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
});*/



// $(function(){
//     function timer(intDiff){
//         showTimerS(intDiff);
//         intDiff--;
//         window.setInterval(function(){
//             showTimerS(intDiff);
//             intDiff--;

//         }, 1000);
//     } 

//     function showTimerS( diff){
//         var day=0,
//         hour=0,
//         minute=0,
//         second=0;//时间默认值        
            
//         if(diff > 0){
//             day = Math.floor(diff / (60 * 60 * 24));
//             hour = Math.floor(diff / (60 * 60)) - (day * 24);
//             minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
//             second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
//         }
            
//         if (minute <= 9) minute = '0' + minute;
//         if (second <= 9) second = '0' + second;
//         $('#day_show').html(day+"天");
//         $('#hour_show').html('<s id="h"></s>'+hour+'时');
//         $('#minute_show').html('<s></s>'+minute+'分');
//         $('#second_show').html('<s></s>'+second+'秒');
//     }

//     var endPayTime=$("#endPayTime").val();
//     var currentTime=$("#currentTime").val();
//     function getLocalTime(nS) {     
//         nS = nS.substring(0,19);    
//         nS = nS.replace(/-/g,'/'); 
//         return new Date(nS).getTime()/1000;      
//     }

//     // 付款倒计时
//     var endPayTime = getLocalTime(endPayTime.valueOf());
//     var currentTime = getLocalTime(currentTime.valueOf());
//     var cha = endPayTime - currentTime;
//     //alert(endPayTime+"===="+currentTime+"====="+cha);
//     timer(cha); 
// });









// 倒计时
$(function(){

    // 结束时间的时间戳
    var endDate = new Date("June 30,2017 00:00:00")
    var endStamp = endDate.valueOf();

    // 获取当前时间的时间戳
    var now = new Date();
    var nowTimeStamp = now.valueOf();

    // 相差的时间戳
    var differStamp = endStamp - nowTimeStamp;

    var intDiff = parseInt(differStamp/1000);//倒计时总秒数量

    function showTimerS( diff ){
        var day=0,
        hour=0,
        minute=0,
        second=0;//时间默认值        
            
        if(diff > 0){
            day = Math.floor(diff / (60 * 60 * 24));
            hour = Math.floor(diff / (60 * 60)) - (day * 24);
            minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
            second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
        }
            
        if (minute <= 9) minute = '0' + minute;
        if (second <= 9) second = '0' + second;
        $('#day_show').html(day+"天");
        $('#hour_show').html('<s id="h"></s>'+hour+'时');
        $('#minute_show').html('<s></s>'+minute+'分');
        $('#second_show').html('<s></s>'+second+'秒');
    };

    function timer(intDiff){
        showTimerS(intDiff);
        intDiff--;
        window.setInterval(function(){
            showTimerS(intDiff);
            intDiff--;
        }, 1000);
    };

    timer(intDiff); 
});


