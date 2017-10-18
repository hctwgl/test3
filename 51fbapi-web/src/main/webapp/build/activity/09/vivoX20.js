let modelId=getUrl("modelId");//获取活动Id
//获取数据
let vm = new Vue({
    el: '#vivoX20',
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
                url: '/fanbei-web/partActivityInfo',
                data:{'modelId':modelId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data.activityList.slice(0,3);
                    console.log(eval('(' + data + ')').data);
                    self.content.firstList=self.content[0].activityGoodsList;
                    self.content.secondList=self.content[1].activityGoodsList;
                    self.content.thirdList=self.content[2].activityGoodsList;
                    console.log(self.content);
                    self.$nextTick(function () {
                        /*图片预加载*/
                        $(".first").each(function() {
                            var img = $(this);
                            img.load(function () {
                                $(".loadingMask").fadeOut();
                            });
                            setTimeout(function () {
                                $(".loadingMask").fadeOut();
                            },1000)
                        });
                        $(".loadingMask").fadeOut();
                        $("img.lazy").lazyload({
                            placeholder : "https://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        //点击商品
        goodClick(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        }
    }
})