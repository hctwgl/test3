var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

$(function(){
    //点击抢优惠券	 
	$('.shareBtn').click(function(){
        $.ajax({
            url: '/fanbei-web/pickBoluomeCoupon',
            data:{'sceneId':'8139','userName':userName},
            type: 'POST',
            success:function (data) {
                data=eval('(' + data + ')');
                if(data.success){
                    requestMsg("领劵成功")
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
	});

});