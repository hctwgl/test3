//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {}
    },
    created: function () {
        this.logData();
    },
    methods: {
        logData() {
            //获取页面初始化信息
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GGShare/initHomePage",
                data:{activityId:1},
                success: function (data) {
                    //self.content = eval('(' + data + ')').data;
                    console.log(data);
                }
            })
        }
    }
})
