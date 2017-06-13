
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
      console.log(returnData);
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
