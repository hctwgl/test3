
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

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
      //console.log(returnData);
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
      //console.log(day+":"+hour+":"+minute+":"+second);
      $('.timeCount').html('距离活动开始还有：'+day+'天'+hour+'时'+minute+'分'+second+'秒');
  }
   
