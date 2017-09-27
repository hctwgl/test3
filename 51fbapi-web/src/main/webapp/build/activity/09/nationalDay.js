
let activityId=getUrl("activityId");//获取活动Id
let groupId=getUrl('groupId');//获取优惠券列表
//获取数据
let vm = new Vue({
    el: '#nationalDay',
    data: {
        content: {},
        couponCont:{}
    },
    created: function () {
        this.logData();
        this.logCoupon();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/newEncoreActivityInfo',
                data:{'activityId':activityId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
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
                            placeholder : "http://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
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
        /*点击商品*/
        goodClick:function(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        },
        /*获取优惠券列表*/
        logCoupon() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/activityCouponInfo",
                data:{'groupId':groupId},
                success: function (data) {
                    self.couponCont = eval('(' + data + ')').data;
                    console.log(self.couponCont);
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        }
    }
})
