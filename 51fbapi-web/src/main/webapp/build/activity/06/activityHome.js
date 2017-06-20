/**
 * Created by yoe on 2017/5/27.
**/

let finished = 0;
let page = 1; // 默认页数从1开始

// app调用web的方法
function alaShareData(){
  // 分享内容
  let dataObj = {
    'appLogin': 'N', // 是否需要登录，Y需要，N不需要
    'type': 'share', // 此页面的类型
    'shareAppTitle': '年中盛宴，错过这次等一年',  // 分享的title
    'shareAppContent': '分期免息“购”优惠，嗨购全球高佣好货，你要的攻略在这里！',  // 分享的内容
    'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
    'shareAppUrl': 'https://app.51fanbei.com/fanbei-web/activity/feastRaidersShare',  // 分享后的链接
    'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
    'sharePage': 'feastRaidersShare' // 分享的页面
  };

  let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
  return dataStr;
}
function statistics(id){
  $.ajax({
      url:'/fanbei-web/qualityGoodsStatistics',
      type:'post',
      data:{goodsId:id},
      success:function (data) {
          console.log(id);
          console.log(data);
      }
  })
}
// 精品推荐Model
function addModel(goodsList,notifyUrl) {

  let html = '';
  for (let i = 0; i < goodsList.length; i++) {
    let saleAmount = toDecimal2(goodsList[i].saleAmount);  // 售价
    let rebateAmount = toDecimal2(goodsList[i].rebateAmount);  // 返利
    let goodsUrl = notifyUrl + '&params={"goodsId":"'+goodsList[i].goodsId+'"}';
    let con=`<div class="price rebate">
                  <span>￥${saleAmount}</span>
                  <span><i>返</i>￥${rebateAmount}</span>
              </div>`;
    if(goodsList[i].goodsType==1){
        let amount = toDecimal2(goodsList[i].nperMap.amount);  // 每期金额
        if(goodsList[i].nperMap.isFree==1){
            let amount = toDecimal2(goodsList[i].nperMap.freeAmount);  // 每期金额
        }
        let totalAmount = toDecimal2(goodsList[i].nperMap.totalAmount);  // 抢购价
        con=`<div class="price stages">
                 <p style="color:#f98011"><i class="monthCorner"></i>￥${amount}×${goodsList[i].nperMap.nper}</p>
                 <p>抢购价：￥${totalAmount}</p>
               </div>`
    }

    html +='<li class="clearfix">'
            +'<a onclick="statistics('+goodsList[i].goodsId+')" href='+goodsUrl+'>'
              +'<img src='+goodsList[i].goodsIcon+'>'
              +'<div class="boutiqueHomeContent clearfix">'
                  +'<p class="title" style="-webkit-box-orient: vertical;">'+goodsList[i].goodName+'</p>'
                  +con
                  +'<button>马上抢</button>'
              +'</div>'
            +'</a>'
          +'</li>';
    }
    return html;
};

// 初始化页面
window.onload=function(){
  $.ajax({
    url: "/fanbei-web/mainActivityInfo",
    type: "post",
    dataType: "JSON",
    success: function(returnData){
      if (returnData.success) {
        console.log(returnData);
        let notifyUrl = returnData.data.notifyUrl;  // goodsId

        // 会场链接
        let mainActivityList = returnData.data["mainActivityList"];
        for (var i = 0; i < mainActivityList.length; i++) {
          let activityUrl = mainActivityList[i].activityUrl;
          let sort = mainActivityList[i].sort;
          let goodType = mainActivityList[i].goodType;

          let dataType = $("#selectedHome li").attr("data-type");
          if (dataType = sort) {
            $("#selectedHome li").eq(i).find('a').attr("href",activityUrl);
          } else {
            requestMsg("会场不存在");
          }
        }

        // 精品title
        let goodTitle = returnData.data["goodTitle"];
        $("#goodTitle").append(goodTitle);

        // 精品列表
        let qualityGoodsList = returnData.data["qualityGoodsList"];
        $("#qualityGoodsList").append(addModel(qualityGoodsList,notifyUrl));

        finished=0

      }else{
        requestMsg(returnData.msg);
      }
    },
    error: function(){
      requestMsg("请求失败");
    }
  });
}

// 倒计时
$(function(){
  // 结束时间的时间戳
  let endDate = new Date("June 30,2017 23:59:59");
  let endStamp = endDate.valueOf();
  // 获取当前时间的时间戳
  let now = new Date();
  let nowTimeStamp = now.valueOf();
  // 相差的时间戳
  let differStamp = endStamp - nowTimeStamp;
  let intDiff = parseInt(differStamp/1000);//倒计时总秒数量

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
