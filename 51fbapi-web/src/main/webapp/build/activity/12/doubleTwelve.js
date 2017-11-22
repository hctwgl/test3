let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;


let vm = new Vue({
    el: "#barginIndex",
    data: {
        totalData: {}, //所有商品数据
        ruleFlag: false, //规则显示flag
        downTime: { d: 0, h: 0, m: 0, s: 0 }, // 倒计时时间
    },
    created: function () {
        this.logData();
    },
    computed: {
        couponNum() {
            return this.firstGoods.couponList ? this.firstGoods.couponList.filter((a, b) => { return a.state != 0 }).length : 0
        }
    },
    methods: {
        logData: function () { // get 初始化 信息
            let self = this;
            $.ajax({
                url: self.url_1,
                type: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (!data.success) {
                        requestMsg("哎呀，出错了！");
                        return false;
                    }
                    self.totalData = data.data;
                    $(".loadingMask").fadeOut();
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        toProduct: function (id, type) { // 跳转到商品页
            location.href = "/fanbei-web/activity/barginProduct?goodsId=" + id + "&productType=" + type + "&userName=" + name + "&spread=" + spread;
        },
        countDown: function () { // 倒计时
            let self = this;
            let timer = setInterval(function () {
                let t = (self.totalData.endTime - new Date().getTime()) / 1000;
                let d = 0;
                let h = 0;
                let m = 0;
                let s = 0;
                if (t >= 0) {
                    d = Math.floor(t / 60 / 60 / 24);
                    h = Math.floor(t / 60 / 60 % 24);
                    m = Math.floor(t / 60 % 60);
                    s = Math.floor(t % 60);
                }
                self.downTime.d = d;
                self.downTime.h = h;
                self.downTime.m = m;
                self.downTime.s = s;
            }, 1000);
        },
        /*点击优惠券*/
        couponClick: function (item, index) {
            let self = this;
            let couponId = item.couponId;
            if (item.state == 2) {
                if (self.ajaxFlag) {
                    self.ajaxFlag = false;
                    $.ajax({
                        url: self.url_2,
                        type: "POST",
                        dataType: "JSON",
                        data: {
                            couponId: couponId
                        },
                        success: function (returnData) {
                            if (returnData.success) {
                                requestMsg("优惠劵领取成功");
                                self.$set(self.couponData[index], 'state', 1);
                            } else {
                                var status = returnData.data["status"];
                                if (status == "USER_NOT_EXIST") { // 用户不存在
                                    if (self.isWx) {
                                        location.href = "./barginLogin";
                                    } else {
                                        window.location.href = returnData.url;
                                    }
                                }
                                if (status == "OVER") { // 优惠券个数超过最大领券个数
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
                        },
                        complete: function () {
                            self.ajaxFlag = true;
                        }
                    });
                }
            } else if (item.state == 1) {
                requestMsg("您已经领取，快去使用吧");
            }
        },
        buy: function (id) {
            if (this.isWX) {
                // 跳转应用商城
                location.href = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www';
            } else {
                // 跳转原生app商品购买页
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + id + '"}';
            }
        },
        maidian(data) {
            //数据统计            // 推广埋点
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: { maidianInfo: '/fanbei-web/activity/barginIndex?userName=' + userName, spread: spread },
                success: function (data) {
                    console.log(data)
                }
            });
        }
    }
});