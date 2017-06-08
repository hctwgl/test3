

// 规则显示
$(".clickRule").click(function(){
  $(".rule").fadeIn();
  $(".mask").removeClass("hide");
});

$(".mask").click(function(){
  $(".rule").fadeOut();
  $(this).addClass("hide");
});

// 开始时间的时间戳
var startDate = new Date("June 8,2017 10:00:00");
var startStamp = startDate.valueOf();
var endDate = new Date("June 9,2017 23:59:59")
var endStamp = endDate.valueOf();

// 获取当前时间的时间戳
var now = new Date();
var nowTimeStamp = now.valueOf();

$('.grabTenyuan').click(function(){
    //判断活动时间
    if(startStamp <= nowTimeStamp && nowTimeStamp <= startStamp && ){
      requestMsg("很抱歉，活动尚未开始！");
    } else {
      loginSuccess(userName)
    }
});

// 领券
function loginSuccess(obj) {
  userName = obj;
  $.ajax({
    url: '/fanbei-web/pickBoluomeCoupon',
    data:{'sceneId':'8267','userName':userName},
    type: 'POST',
    success:function (data) {
      data=eval('(' + data + ')');
      if(data.success){
          requestMsg("领劵成功");
      }else{
        if(data.url){
          location.href=data.url;
        }else{
          requestMsg(data.msg);
        }
      }
    }
  });
}
