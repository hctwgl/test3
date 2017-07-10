let modelId = getUrl("modelId");
let vm = new Vue({
    el: '#gamePoint',
    data: {
        content: {},
        isActive: true
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
                data: {
                    'modelId': modelId
                },
                success: function (data) {
                    //   self.content = ; 
                    //    console.log(self.content)a    
                    // console.log(data);
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);

                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
        //点击tab栏切换
        isshow: function () {
            this.isActive = !this.isActive;
        },
        buyNow(id){
            // window.location.href = '/fanbei-web/opennative?params={"goodsId":"' + id + '"}'
            window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"' + id + '"}'
        }
    }
})
