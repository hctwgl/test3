/*
* @Author: Jacky
* @Date:   2017-08-08 14:11:41
*/

let finished = 0;//防止多次请求ajax
let dataMonth = getUrl("dataMonth");

var vm = new Vue({
    el: '#inviteLastwinRank',
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
                url: '/fanbei-web/prizeUser',
                dataType: 'json',
                data:{'dataMonth':dataMonth},
                type: 'post',
                success: function (data) {
                    console.log(data);
                    _this.returnData = data;
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
    }
});
