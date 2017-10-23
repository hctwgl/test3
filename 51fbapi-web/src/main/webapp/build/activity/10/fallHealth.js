let activityId = getUrl("activityId");
//获取数据
new Vue({
    el: '#fallHealth',
    data: {
        content: []
    },
    created: function () {
        this.loginData();
    },
    methods: {
        loginData() { //页面初始化
            let self = this;
            $.ajax({
                url: '/fanbei-web/newEncoreActivityInfo',
                type: 'post',
                data: {'activityId': activityId},
                success: function (data) {
                    //console.log(data)
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    self.$nextTick(function () {
                        // lazy.init();
                        $(".loadingMask").fadeOut();
                        $("img.lazy").lazyload({
                            placeholder : "https://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        //点击商品进入详情页
        goodClick:function(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}';
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}';
            }
        }
    }
})