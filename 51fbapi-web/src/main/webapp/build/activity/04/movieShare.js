
/**
 * Created by nizhiwei on 2017/4/19.
 */
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}

//显示电影详情
function More(i) {
    if ($('.morePic').eq(i).hasClass('down')) {
        $('.morePic').removeClass('down');
        $('.details').hide();
    } else {
        $('.morePic').removeClass('down').eq(i).addClass('down');
        $('.details').hide().eq(i).show();
    }
}
//点击抢卷按钮
function shareBtn() {
    jQuery.ajax({
        url: 'pickBoluomeCoupon',
        data:{'sceneId':'8139','userName':userName},
        type: 'post',
        success:function (data) {
            // data=eval('(' + data + ')');
            data=JSON.parse(data);
            console.log(data);
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
}