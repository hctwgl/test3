let modelId = getUrl("modelId");
console.log(modelId)
//获取数据
let vm = new Vue({
    el: '#huiLi',
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
                type: 'post',
                url: "/fanbei-web/partActivityInfo?modelId="+modelId,
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(data);
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        }
    }
})
