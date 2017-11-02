let activityId = getUrl("activityId");
var groupId=getUrl('groupId');
//获取数据
new Vue({
    el: '#appleStartSchool',
    data: {
        content: [],
        couponCont:[]
    },
    created: function () {
        this.loginData();
        this.logCoupon();
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
        logCoupon() {
            //获取优惠券初始化信息
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
       //点击商品进入详情页
        goodClick:function(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}';
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}';
            }
        },
        couponClick:function(item){//点击领券
            var couponId=item.couponId;
            console.log(couponId);
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId:couponId
                },
                success: function(returnData){
                    console.log(returnData)
                    if(returnData.success){
                        requestMsg("优惠劵领取成功");
                        item.drawStatus='Y';
                    }else{
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            requestMsg(returnData.msg);
                            requestMsg("优惠券个数超过最大领券个数");
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                        }
                    }
                },
                error: function(){
                    requestMsg("哎呀，出错了！");
                }
            });

        }
    }
})