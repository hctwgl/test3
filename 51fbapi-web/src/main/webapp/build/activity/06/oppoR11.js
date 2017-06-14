
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

// let protocol = window.location.protocol;
// let host = window.location.host;
// let utlHost = protocol+'//'+host;

// app调用web的方法
function alaShareData(){
    // 分享内容
    let dataObj = {
        'appLogin': 'N', // 是否需要登录，Y需要，N不需要
        'type': 'share', // 此页面的类型
        'shareAppTitle': 'OPPO R11预约返利300元',  // 分享的title
        'shareAppContent': 'OPPO R11全明星首发，疯陪到底！0元预约享12期分期免息，更有超级返利300元！有，且只在51返呗 GO>>>',  // 分享的内容
        'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
        'shareAppUrl': 'https://app.51fanbei.com/fanbei-web/activity/oppoR11?oppoR11Share=oppoR11Share',  // 分享后的链接
        'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
        'sharePage': 'oppoR11' // 分享的页面
    };
    let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
    return dataStr;
};

var oppoR11Share = getUrl("oppoR11Share");
if (oppoR11Share == "oppoR11Share") {
  $(".goRegister").removeClass("hide");
  $(".banner").addClass("hide");
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

//点击预约
$('#btn').click(function(){
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
});

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

var endStamp=Date.parse(new Date("June 18,2017 00:00:00"));
var currentStamp=Date.parse(new Date());
var diffStamp=(endStamp-currentStamp)/1000;

$(function(){  
  stamp();
  var timer=setInterval("stamp()",1000);
  if(diffStamp<=0){
    clearInterval(timer);
    $('.timeCount').html('距离活动已开始......');
  }  
})

function stamp(){
      diffStamp--;
      var day=Math.floor(diffStamp/(24*3600));
      var hour=Math.floor(diffStamp/3600)-day*24;
      var minute=Math.floor(diffStamp/60)-(day*24*60)-(hour*60);
      var second=diffStamp%60;
      if(hour<10){hour='0'+hour}
      if(minute<10){minute='0'+minute}  
      if(second<10){second='0'+second}
      $('.timeCount').html('距离活动开始还有：'+day+'天'+hour+'时'+minute+'分'+second+'秒');
  }
   
