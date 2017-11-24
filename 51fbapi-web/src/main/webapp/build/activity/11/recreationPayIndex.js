let type=getUrl('type'); //获取类型 GAME 游戏/AMUSEMENT 娱乐
//获取数据
let vm = new Vue({
    el: '#recreationPayIndex',
    data: {
        content: {}
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
                url: "/game/pay/goods",
                data:{'type':type},
                success: function (data) {
                    //console.log(data);
                    self.content=data.data;
                    console.log(self.content);
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //小数点后一位
        fixNum(num){
            let numFix=num.toFixed(1);
            return numFix;
        }
    }
});


