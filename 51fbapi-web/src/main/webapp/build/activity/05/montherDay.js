$(function(){
    var userName = "";
     if(getInfo().userName){
         userName=getInfo().userName
         alert(username)
     }
	//点击购买
	 $('.buy').click(function(){
        $.ajax({
            url: 'getBrandUrl',
            data:{'shopId':'17','userName':'13989455620'},
            type: 'POST',
            success:function (data) {
                alert(data) 
                alert(username)               
                data=eval('(' + data + ')');
                if(data.success){
                   location.href=data.url;
                }else{
                   location.href=data.url; 
                }

            }
        });
	 })

    //点击抢优惠券	 
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