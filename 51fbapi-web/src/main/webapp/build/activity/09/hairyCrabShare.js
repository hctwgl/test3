
let modelId=getUrl('modelId');//获取模板id
let groupId=getUrl('groupId');//获取优惠券组id
//获取数据
let vm = new Vue({
    el: '#hairyCrabShare',
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
                url: "/fanbei-web/partActivityInfo",
                data:{'modelId':modelId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data.activityList.slice(0,2);
                    console.log(eval('(' + data + ')').data);
                    self.content.firstList=self.content[0].activityGoodsList;
                    self.content.secondList=self.content[1].activityGoodsList;
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
        //获取优惠券初始化信息
        logCoupon() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/activityCouponInfo",
                data:{'groupId':groupId},
                success: function (data) {
                    self.couponCont = eval('(' + data + ')').data.couponInfoList[0];
                    console.log(self.couponCont.couponId);
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        //点击领券
        couponClick:function() {
            window.location.href='hairyCrabRegister?channelCode=hairyCrab&pointCode=hairyCrab';
        },
        //点击商品
        goodClick:function(){
            window.location.href='hairyCrabRegister?channelCode=hairyCrab&pointCode=hairyCrab';
        }
    }
});

