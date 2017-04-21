'use strict';

/**
 * Created by nizhiwei on 2017/4/19.
 */
let userName = $('#userName').val();
// if(!userName){
//         location.href=location+'&name=APP_LOGIN'
// }
$(function () {
    //显示电影详情
    $('.moreBtn').on('click', function () {
        var i = $('.moreBtn').index(this);
        if ($('.morePic').eq(i).hasClass('down')) {
            $('.morePic').removeClass('down');
            $('.details').hide();
        } else {
            $('.morePic').removeClass('down').eq(i).addClass('down');
            $('.details').hide().eq(i).show();
        }
    });
    //点击抢卷按钮
    $('.shareBtn').on('click', function () {
        $.ajax({
            url: 'pickBoluomeCoupon',
            data:{'sceneId':'347','userName':'13989455620'},
            type: 'post',
            success:function (data) {

            }
        });
    });
});