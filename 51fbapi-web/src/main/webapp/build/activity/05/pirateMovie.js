/**
 * Created by Administrator on 2017/5/25 0025.
 */
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};
$(function(){
    var currentStamp = Date.parse(new Date());
    var activityTime = "2017-05-26 10:00:00";
    var activityStamp = Date.parse(activityTime);
     //点击立抢10元优惠券
    $('.grabTenyuan').click(function(){
        //判断活动时间
     	  if(currentStamp<activityStamp){
        	$(".tips").fadeIn();
        	setTimeout('$(".tips").fadeOut()', 2000);
        } else {
                $.ajax({
                url: '/fanbei-web/pickBoluomeCoupon',
                data:{'sceneId':'8139','userName':userName},
                type: 'POST',
                success:function (data) {
                    data=eval('(' + data + ')');
                    if(data.success){
                        requestMsg("领劵成功");
                    }else{
                        if(data.url){
                            if (getBlatFrom() == 2) {
                                location.href=data.url;
                            }else{
                                requestMsg("请退出当前活动页面,登录后再进行领劵");
                            }
                        }else{
                            requestMsg(data.msg);
                        }
                    }
                }
              });    
           }       
    })

    //点击规则
    $('.clickRule').click(function(){
      $('.mask').css('display','block');
      $('.rule').fadeIn();
    });
    $('.mask').click(function(){
      $('.mask').css('display','none');
      $('.rule').fadeOut();
    });

    //点击一起出发吧
    $('.go').click(function(){
      location.href="kouMovie";
    })

})

