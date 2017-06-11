/**
 * Created by yoe on 2017/5/27.
**/

let finished = 0;
let page = 1; // 默认页数从1开始

// var addModel = function addModel(goodsList) {
//     var html = '';
//     for (var j = 0; j < goodsList.length; j++) {
//         // 售价
//         var saleAmount = toDecimal2(goodsList[j].saleAmount);
//         var amountAmountSplitArr = saleAmount.split(".");
//         var amountAmountPriceInteger = amountAmountSplitArr[0];
//         var amountAmountPriceDecimal = amountAmountSplitArr[1];
//         // 返利
//         var rebateAmount = toDecimal2(goodsList[j].rebateAmount);
//         var rebateAmountSplitArr = rebateAmount.split(".");
//         var rebateAmountPriceInteger = rebateAmountSplitArr[0];
//         var rebateAmountPriceDecimal = rebateAmountSplitArr[1];
//         var goodInfoUrl = notifyUrl + '&params={"goodsId":"'+goodsList[j].goodsId+'"}';
//         html += '<li class="goodsListModel_item">'
//                 +'<a href='+goodInfoUrl+'>'
//                     +'<img src=" '+goodsList[j].goodsIcon+' " class="mainContent_img">'
//                     +'<div class="goodsListModel_mainContent_wrap">'
//                         +'<p class="fs_26 fsc_1">'+goodsList[j].name+'</p>'
//                         +'<p class="fs_26 fsc_red">'
//                             +'<span>￥'+amountAmountPriceInteger+'</span><span class="fs_24">.'+amountAmountPriceDecimal+'</span>'
//                         +'</p>'
//                     +'</div>'
//                     +'<div class="goodsListModel_mainContent_rebate_wrap">'
//                         +'<div class="goodsListModel_mainContent_rebate clearfix">'
//                             +'<span class="goodsListModel_rebate fl fs_24 bgc_orange fsc_f tac">返</span>'
//                             +'<p class="fl fs_24 fsc_orange">'
//                                 +'<span>￥'+rebateAmountPriceInteger+'</span><span class="fs_22">.'+rebateAmountPriceDecimal+'</span>'
//                             +'</p>'
//                         +'</div>'
//                     +'</div>'
//                 +'</a>'
//             +'</li>';
//     }
//     return html;
// };


// 精品推荐
var addModel = function addModel(goodsList) {
  var html = '';
  for (var i = 0; i < goodsList.length; i++) {
    // 售价
    var saleAmount = toDecimal2(goodsList[i].saleAmount);
    var amountAmountSplitArr = saleAmount.split(".");
    var amountAmountPriceInteger = amountAmountSplitArr[0];
    var amountAmountPriceDecimal = amountAmountSplitArr[1];
    // 返利
    var rebateAmount = toDecimal2(goodsList[i].rebateAmount);
    var rebateAmountSplitArr = rebateAmount.split(".");
    var rebateAmountPriceInteger = rebateAmountSplitArr[0];
    var rebateAmountPriceDecimal = rebateAmountSplitArr[1];

    var goodInfoUrl = notifyUrl + '&params={"goodsId":"'+goodsList[i].goodsId+'"}';

    html += '<h2>精品推荐</h2>'
            +'<ul>'
              '<li class="clearfix">'
                +'<a href='+goodInfoUrl+'>'
                    +'<img src="">'
                    +'<div class="boutiqueHomeContent clearfix">'
                        +'<p class="title">618折上折更有超级返利618折上折更有超级返利618折上折更有超级返利更有超级返利618折上</p>'
                        +'<p class="fs_26 fsc_red">'
                            +'<span>￥'+amountAmountPriceInteger+'</span><span class="fs_24">.'+amountAmountPriceDecimal+'</span>'
                        +'</p>'
                    +'</div>'
                    +'<div class="goodsListModel_mainContent_rebate_wrap">'
                        +'<div class="goodsListModel_mainContent_rebate clearfix">'
                            +'<span class="goodsListModel_rebate fl fs_24 bgc_orange fsc_f tac">返</span>'
                            +'<p class="price"><span>￥5.2</span><span><i>返</i>￥1.2</span></p>'
                            +'<button>马上抢</button>'
                        +'</div>'
                    +'</div>'
                +'</a>'
              +'</li>'
            +'</ul>';
    }
    return html;
};

// 初始化页面
window.onload=function(){
  $.ajax({
    url: "http://192.168.96.41:8081/fanbei-web/mainActivityInfo",
    type: "GET",
    dataType: "JSON",
    success: function(returnData){
      console.log(returnData);
      if (returnData.success) {
        console.log(2222);
      }else{
        requestMsg(returnData.msg);
      }
    },
    error: function(){
      requestMsg("请求失败");
    }
  });
}



// 初始化页面
// $(function(){
//   $(window).on('scroll',function () {
//     if(finished==0){
//       var scrollTop = $(this).scrollTop();
//       var allHeight = $(document).height();
//       var windowHeight = $(this).height();
//       if (allHeight-windowHeight<=scrollTop+400) {
//         page++;
//         finished=1; //防止未加载完再次执行
//         $.ajax({
//           url: "http://192.168.96.41:8081/fanbei-web/mainActivityInfo",
//           type: "GET",
//           dataType: "JSON",
//           data: {
//               modelId : modelIdNum,
//               pageNo: page,
//               type: typeCurrentNum
//           },
//           success: function(returnData){
//             console.log(returnData);
//             if (returnData.success) {
//                 if(returnData.data["goodsList"]==""){
//                   var txt='<div class="loadOver"><span>没有更多了...</span></div>';
//                   $("div[data-type="+typeCurrentNum+"]").append(txt);
//                 }else{
//                   var goodsList = returnData.data["goodsList"];
//                   $("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").append(addModel(goodsList));
//                   finished=0
//                 }
//             }else{
//               requestMsg(returnData.msg);
//             }
//             // pageNumber(page);
//           },
//           error: function(){
//             requestMsg("请求失败");
//           }
//         });
//       }
//     }
//   });
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
