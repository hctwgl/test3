

$(function(){
  var userName = "";
  if(getInfo().userName){
      userName=getInfo().userName;
  };

  //点击预约
  $('.order').click(function(){
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
                if (getBlatFrom() == 2) {
                    location.href=data.url;
                }else{
                    requestMsg("请退出当前活动页面,登录后再进行预约");
                }
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

    $('.mask').click(function(){
        $('.mask').css('display','none');
        $('.orderSuccess').css('display','none');
    });
    $('.close').click(function(){
        $('.mask').css('display','none');
        $('.orderSuccess').css('display','none');
    });
})
