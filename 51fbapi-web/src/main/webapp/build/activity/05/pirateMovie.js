/**
 * Created by Administrator on 2017/5/25 0025.
 */
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};
$(function(){
    var currentStamp = Date.parse(new Date());
    var activityTime = "2017-05-20 10:00:00";
    var activityStamp = Date.parse(activityTime);
     //点击立抢10元优惠券
    $('.grabTenyuan').click(function(){
        //判断活动时间
     	  if(currentStamp<activityStamp){
        	$(".tips").fadeIn();
        	setTimeout('$(".tips").fadeOut()', 2000);
        } else {
              loginSuccess(userName)
           }       
    });



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
      $.ajax({
            url: '/fanbei-web/getBrandUrl',
            data:{'shopId':'1','userName':userName},
            type: 'POST',
            success:function (data) {
                data=eval('(' + data + ')');
                if(data.success){
                   location.href=data.url;
                }else{
                   requestMsg(data.msg);
                }
            }
        });
    })

})

function loginSuccess(obj) {
    userName=obj;
    $.ajax({
        url: '/fanbei-web/pickBoluomeCoupon',
        data:{'sceneId':'8168','userName':userName},
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
                        var appVersion = getInfo().appVersion.replace(/\./g,"");
                        if ( appVersion < "360" ) {
                            requestMsg("请退出当前活动页面,登录后再进行领劵");
                        } else {
                            location.href=data.url;
                        }
                    }
                }else{
                    requestMsg(data.msg);
                }
            }
        }
    });

}