
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

// 获取网站的域名
let domainName = domainName();

// app调用web的方法
function alaShareData(){
    // 分享内容
    let dataObj = {
      'appLogin': 'N', // 是否需要登录，Y需要，N不需要
      'type': 'share', // 此页面的类型
      'shareAppTitle': 'OPPO R11预约返利300元',  // 分享的title
      'shareAppContent': 'OPPO R11全明星首发，疯陪到底！0元预约享12期分期免息，更有超级返利300元！有，且只在51返呗 GO>>>',  // 分享的内容
      'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
      'shareAppUrl': domainName+'/fanbei-web/activity/oppoR11?oppoR11Share=oppoR11Share',  // 分享后的链接
      'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
      'sharePage': 'oppoR11' // 分享的页面
    };
    let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
    return dataStr;
};

// 锁定分享后的页面
let oppoR11Share = getUrl("oppoR11Share");
if (oppoR11Share == "oppoR11Share") {
  $(".goRegister").removeClass("hide");
  $(".banner").addClass("hide");

  $(".mobileList").addClass("hide");
  $(".mobileListImg").removeClass("hide");
}

// 初始化页面
window.onload=function(){
  $.ajax({
    url: "/app/activity/reserveActivityInfo",
    type: "POST",
    dataType: "JSON",
    data: {
        userName: userName
    },
    success: function(returnData){
      if (returnData.data.isHaveReservationRecord == "Y") {
        $("#btn").attr('src', 'https://fs.51fanbei.com/h5/app/activity/06/oppo2_2.png');
      }
    },
    error: function(){
      requestMsg("请求失败");
    }
  });
}

// 弹窗
$('.mask').click(function(){
    $('.mask').css('display','none');
    $('.orderSuccess').css('display','none');
    window.location.reload();
});
$('.close').click(function(){
    $('.mask').css('display','none');
    $('.orderSuccess').css('display','none');
    window.location.reload();
});

// 倒计时
$(function(){
  // 结束时间的时间戳
  let endDate = new Date("June 22,2017 10:00:00")
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

// 点击手机弹窗
new Vue({
  el: '#oppoR11',
  methods:{
    btnBox: function(){
      $.ajax({
        url: '/app/activity/reserveActivityGoods',
        dataType:'json',
        data:{'userName':userName},
        type: 'post',
        success:function (data) {
          if(data.success){
            $('.mask').css('display','block');
            $('.orderSuccess').css('display','block');
          }else{
            if(data.url){
              location.href=data.url;
            }else{
              requestMsg(data.msg);
            }
          }
        },
        error: function(){
          requestMsg("请求失败");
        }
      });
    },
    mobilePopup: function(){ // 显示手机弹窗
      $('.popupBox').removeClass('hide');
      $('.popup').removeClass('hide');
      // $('body').css('overflow', 'hidden');
    },
    close: function(){ // 关闭手机弹窗
      $('.popupBox').addClass('hide');
      $('.popup').addClass('hide');
    }
  }
});

// mobileList手机列表
function addMobileListModel(goodsList,notifyUrl) {

  let html = '';
  for (let i = 0; i < goodsList.length; i++) {
    // let privateGoodsId = notifyUrl + '&params={"privateGoodsId":"'+goodsList[i].goodsId+'"}';
    let goodsIcon = goodsList[i].goodsIcon;
    let goodsName = goodsList[i].goodsName;
    let saleAmount = goodsList[i].saleAmount;
    let remark = goodsList[i].remark;

    let privateGoodsId='';

    if (getBlatFrom()==2){
      var ver=getInfo().appVersion;
      if( ver&&ver < 365 ){
        privateGoodsId = 'https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.YbuWvi&id=552277681637&skuId=3382955645849&areaId=330100&user_id=3165788212&cat_id=2&is_b=1&rn=020d728da7a3eb34b3c8613c1ade59cd';
      }
    }else{
      privateGoodsId = notifyUrl + '&params={"privateGoodsId":"'+goodsList[i].goodsId+'"}';
    }

    html+=`<li>
            <a href='${privateGoodsId}'>
              <img src="${goodsIcon}">
              <div class="mobileListContent">
                <p><i></i>${goodsName}</p>
                <span>¥${saleAmount}</span>
                <span>${remark}</span>
              </div>
            </a>
          </li>`;
  }
  return html;
};

// 初始化页面
let page = 0;
window.onload=function(){
  page++;
  console.log(page);
  $.ajax({
    url: "/app/activity/getSelfSupportGoodsInfo",
    type: "POST",
    dataType: "JSON",
    data: {
      pageNo: page
    },
    success: function(returnData){
      $("#mobileList").append(addMobileListModel(returnData.data.goodsList,returnData.data.notifyUrl));
    },
    error: function(){
      requestMsg("请求失败");
    }
  });
}

// 对ios的版本进行判断
// if(getBlatFrom()==2){
//     var ver=getInfo().appVersion;
//     if( ver&&ver < 365 ){
//       // $('.alert').fadeIn();
//       // $('#shadow').fadeIn();
//       alert(2222);
//       var data = $("#mobileList a");
//       alert(data);
//       console.log(data);
//       $("#mobileList a").attr('href', 'https://www.baidu.com');
//
//
//
//     }
// }


// alert(2222);
// var data = $("#mobileList a");
// alert(data);
// console.log(data);
// $("#mobileList li").children('a').attr('href', 'https://www.baidu.com');
