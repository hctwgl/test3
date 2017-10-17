/**
 * Created by nizhiwei-labtop on 2017/10/11.
 */
function mai() {
    $.ajax({
        url: '/fanbei-web/postMaidianInfo',
        type: 'post',
        data: {maidianInfo: '/fanbei-web/activity/czbank?type=btn'},
        success: function (data) {
            console.log(data)
        }
    });
}