/*
 * @Author: Jacky
 * @Date:   2017-08-08 14:11:41
 */

let finished = 0;//防止多次请求ajax

var vm = new Vue({
    el: '#inviteLastWinRank',
    data: {
        returnData: [],
        month:"",
        total:""
    },
    created: function () {
        let _this = this;
        _this.initial();
    },
    methods: {
        initial(){
            let _this = this;
            $.ajax({
                url: '/fanbei-web/getPrizeByLastMonth',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    console.debug(data);
                    _this.returnData = data.datalist;
                    _this.month = data.month;
                    _this.total = data.total;
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
    }
});
