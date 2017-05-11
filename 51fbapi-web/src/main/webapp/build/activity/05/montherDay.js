$(function(){
	//点击购买
	 $('.buy').click(function(){
        location.href="http://91ala.test.otosaas.com/shengxian";
	 })
    //点击抢优惠券
	 var userName = "";
     if(getInfo().userName){
         userName=getInfo().userName
     }
	 $('.saleOff').click(function(){
        $.ajax({
            url: 'pickBoluomeCoupon',
            data:{'sceneId':'387','userName':userName},
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
	 })
})