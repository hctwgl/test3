
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
        data:{'sceneId':'347','userName':userName},
        type: 'post',
        success:function (data) {
            data=eval('(' + data + ')');
            alert(data)
            if(data.success){
                requestMsg(data.msg)
            }else{
                if(data.url){
                    location.href=data.url
                }else{
                    requestMsg(data.msg)
                }
            }

        }
    });
}