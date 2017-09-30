
let activityId=getUrl("activityId");//获取活动Id
let groupId=getUrl('groupId');//获取优惠券列表
let shopId=getUrl('shopId');//获取场景Id
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
        },
        /*点击优惠券*/
        couponClick:function(item) {
            //let self = this;
            let couponId = item.couponId;
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    if (returnData.success) {
                        requestMsg("优惠劵领取成功");
                    } else {
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            //requestMsg(returnData.msg);
                            requestMsg("您已经领取，快去使用吧");
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                        }
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            })
        },
        //点击最后大图进入场景
        fourthContClick(){
            //window.location.href='https://91ala.otosaas.com/menpiao';
            $.ajax({
                url: "/fanbei-web/getBrandUrlV1",
                type: "POST",
                dataType: "JSON",
                data: {
                    'shopId':shopId
                },
                success: function (returnData) {
                    console.log(returnData);
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            })
        }
    }
});
