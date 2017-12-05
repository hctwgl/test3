let orderNo=getUrl('orderNo'); // 获取订单编号
let returnNum = getBlatFrom();  // 判断1为Android，2为ios
//获取数据
let vm = new Vue({
    el: '#gameOrderDetail',
    data: {
        content: {},
        diff:''
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/game/pay/orderInfo",
                data:{'orderNo':orderNo},
                success: function (data) {
                    console.log(data);
                    if(data.success){
                        self.content=data.data;
                        console.log(self.content);
                        self.content.orderStartTime=format((self.content.gmtCreate)*1000);//订单创建时间
                        //订单付款时间
                        if(self.content.gmtPay){
                            self.content.payTime=format((self.content.gmtPay)*1000);
                        }
                        //订单付款倒计时
                        self.diff=(self.content.gmtPayEnd)-(self.content.gmtPayStart);
                        console.log(self.diff);
                        showTimerS(self.diff);
                        self.diff--;
                        window.setInterval(function(){
                            showTimerS(self.diff);
                            self.diff--;
                        }, 1000);
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })

        },
        //去支付
        goPay(){
            let self=this;
            window.location.href='/fanbei-web/opennative?name=BRAND_ORDER_CONFIRM&params={"orderId":"'+orderNo+'","plantform":"'+self.content.plantform+'"}';
        },
        //删除订单
        deleteOrder(){
            let self=this;
            $.ajax({
                type: 'post',
                url: "/game/pay/order/delete",
                data:{'orderId':self.content.orderId},
                success: function (data) {
                    console.log(data,'删除订单');
                    if(returnNum==1){ //returnNum==1--安卓；returnNum==2--ios
                        window.location.href='/fanbei-web/opennative?name=com.alfl.www.user.ui.OrderListActivity';
                    }else{
                        window.location.href='/fanbei-web/opennative?name=GG_ALAFifthAllOrdersController';
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        }
    }
});
function add0(m){return m<10?'0'+m:m }
function format(shijianchuo)
{
//shijianchuo是整数，否则要parseInt转换
    var time = new Date(shijianchuo);
    var y = time.getFullYear();
    var m = time.getMonth()+1;
    var d = time.getDate();
    var h = time.getHours();
    var mm = time.getMinutes();
    var s = time.getSeconds();
    return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
}
//倒计时
function showTimerS( diff ){
    let hour=0,
        minute=0,
        second=0;//时间默认值

    if(diff > 0){
        hour = Math.floor(diff / (60 * 60));
        minute = Math.floor(diff / 60) - (hour * 60);
        second = Math.floor(diff) - (hour * 60 * 60) - (minute * 60);
    }
    if (hour <= 9){
        hour = '0' + hour;
    }
    if (minute <= 9){
        minute = '0' + minute;
    }
    if (second <= 9) {
        second = '0' + second;
    }

    return hour+':'+minute+':'+second
}


