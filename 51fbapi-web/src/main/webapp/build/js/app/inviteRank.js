/*
 * @Author: Jacky
 * @Date:   2017-08-08 14:11:41
 */

let finished = 0;//防止多次请求ajax

var vm = new Vue({
    el: '#inviteRank',
    data: {
        returnData: []
    },
    created: function () {
        let _this = this;
        _this.initial();
    },
    methods: {
        initial(){
            let _this = this;
            $.ajax({
                url: '/fanbei-web/recommendListSort',
                dataType: 'json',
                type: 'get',
                success: function (data) {
                    console.log(data);
                    _this.returnData = data.data;
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
    }
});