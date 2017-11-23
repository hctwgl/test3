//获取数据
let vm = new Vue({
    el: '#recreationPayIndex',
    data: {
        content: {},
        ruleShow:false
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            /*$.ajax({
                type: 'post',
                url: "/game/pay/goods",
                data:{'type':'AMUSEMENT'},
                success: function (data) {
                    console.log(data);
                    //self.content=data.data.goodsList;
                    //console.log(self.content)
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });*/
        }
    }
});


