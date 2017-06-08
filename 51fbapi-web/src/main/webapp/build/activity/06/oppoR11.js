
var usertName='';
if(getInfo().userName){
    userName=getInfo().userName
}
$(function(){
//点击预约	
	$('.order').click(function(){
		    $.ajax({
            url: 'http://192.168.96.32:8001/goods/reserveActivityGoods',
            dataType:'json',
            data:{'activityId':'112','goodsId':'92850','rsvNums':'1'},
            type: 'post',
            success:function (data) {
                console.log(data);
                if(data.success){
                    $('.mask').css('display','block');
                    $('.orderSuccess').css('display','block');
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
				
	});
	$('.mask').click(function(){
		$('.mask').css('display','none');
		$('.orderSuccess').css('display','none');
	});
})