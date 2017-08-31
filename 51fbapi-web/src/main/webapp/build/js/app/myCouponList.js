var tabWidth = 0;
var liWidth = 0;
var ulWidth = 0;
//获取数据
let vm = new Vue({
    el: '#myCouponList',
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
                data: {'status': "NOUSE"},
                url: "/fanbei-web/getMineCouponInfo",
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    //计算排序与即将到期
                    var diff = 0;
                    //即将到期
                    for (var j = 0; j < self.content.couponList.length; j++) {
                        var currentTime = self.content.couponList[j].currentTime;
                        var startTime = self.content.couponList[j].gmtStart;
                        var endTime = self.content.couponList[j].gmtEnd;
                        //console.log(endTime)
                        self.content.couponList[j].start = format(startTime);
                        self.content.couponList[j].end = format(endTime);
                        //console.log(couponCategory.couponInfoList[j].end)
                        diff = (endTime - currentTime) / 1000;
                        var h = parseInt(diff / 3600);
                        var state;
                        if (0 < h && h < 48) {
                            state = 'timeOver';
                        } else {
                            state = 'noTimeOver';
                        }
                        self.content.couponList[j].state = state;
                    }

                    //计算年月日
                    function format(stramp) { //stramp是整数，否则要parseInt转换 
                        var time = new Date(stramp);
                        var y = time.getFullYear();
                        var m = time.getMonth() + 1;
                        var d = time.getDate();
                        if (m < 10) {
                            m = '0' + m
                        }
                        if (d < 10) {
                            d = '0' + d
                        }
                        return y + '.' + m + '.' + d;
                    }

                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        },

        couponClick: function (e) {
            let self = this;
            var couponId = e.couponId;
            var shopUrl = e.shopUrl;
            var couponType = e.type;
            //只有现金券、满减券、会场券时，券状态有去用券 可点击跳转 其他不可
            if (e.isDraw == 'N' && (couponType == 'FULLVOUCHER' || couponType == 'CASH' || couponType == 'ACTIVITY')) {
                //去用券
                if (shopUrl) {
                    window.location.href = shopUrl;
                } else {
                    window.location.href = '/fanbei-web/opennative?name=APP_HOME';
                }
            } else if (e.isDraw == 'Y') {
                //点击领券
                //console.log(1)
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
                            e.isDraw = 'N';
                        } else {
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
                                $(".couponLi").eq(i).css('display', 'none');
                            }
                        }
                    },
                    error: function () {
                        requestMsg("请求失败");
                    }
                });
            }
        }//couponClick--

    }
})
