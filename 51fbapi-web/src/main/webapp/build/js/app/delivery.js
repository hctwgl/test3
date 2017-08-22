/*
 * @Author: Jacky
 * @Date:   2017-08-08 14:11:41
 */

let finished = 0;//防止多次请求ajax
let orderId = getUrl("orderId");
let traces = getUrl("traces");

var vm = new Vue({
    el: '#delivery',
    data: {
        tracesInfo: [],
        shipperName:"",
        shipperCode:"",
        stateDesc:""
    },
    created: function () {
        let _this = this;
        _this.initial();
    },
    methods: {
        initial(){
            let _this = this;
            $.ajax({
                url: '/fanbei-web/getOrderLogistics',
                dataType: 'json',
                data:{
                    'orderId':orderId,
                    'traces':traces
                },
                type: 'get',
                success: function (data) {
                    console.log(data);
                    _this.tracesInfo = data.data.tracesInfo;
                    _this.shipperName = data.data.shipperName;
                    _this.shipperCode = data.data.shipperCode;
                    _this.stateDesc = data.data.stateDesc;
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
    }
});