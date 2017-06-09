
let usertName='';
if(getInfo().userName){
    userName=getInfo().userName
}
$(function(){
    //点击按钮显示弹窗
     $('.button').click(function(){
            if($('.pop-up').css('display')=="none"){
                $('.pop-up').show();
            }else{
                $('.pop-up').hide();
            }
            console.log(1111);
     });

    //点击抢券按钮
    $('#active_btn').click(function(){
        $.ajax({
            url: '/fanbei-web/pickBoluomeCoupon.htm',
            dataType:'json',
            data:{'sceneId':'8161','userName':'userName'},
            type: 'post',
            success:function (data) {
                console.log(data);
                if(data.success){
                    requestMsg("领劵成功")
                }else{
                    if(data.url=false){
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

    })

})