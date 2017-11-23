let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;


let vm = new Vue({
    el: "#main",
    data: {
        goodsData: {}, //所有商品数据
        couponData: [],
        currentData: [],
        couponFlag: true, // 显示优惠券flag
        downTime: {
            d: 0,
            h: 0,
            m: 0,
            s: 0
        }, // 倒计时时间
        isApp: true,
        couponUrl: "/activity/double12/couponHomePage",
    },
    created: function () {
        this.isAppFn();
    },
    computed: {
        couponNum() {
            return this.firstGoods.couponList ? this.firstGoods.couponList.filter((a, b) => {
                return a.state != 0
            }).length : 0
        }
    },
    methods: {
        isAppFn: function () {
            let isAppParam = getUrl('spread');
            if (isAppParam != '') {
                this.isApp = false;
                this.couponUrl = "/activityH5/double12/couponHomePage";
            }
            this.logData();
        },
        logData: function () { // get 初始化 信息
            let self = this;
            // 获取优惠券信息
            $.ajax({
                url: self.couponUrl,
                type: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (!data.success) {
                        location.href = data.data.loginUrl;
                        return false;
                    }
                    data = {
                        "data": {
                            "couponList": [{
                                "amount": 6.00,
                                "id": 1047,
                                "isGet": "Y",
                                "isShow": "Y",
                                "ishas": "Y",
                                "name": "6元还款抵用券",
                                "threshold": "6元还款抵用券"
                            }, {
                                "amount": 3.00,
                                "id": 1046,
                                "isGet": "N",
                                "isShow": "Y",
                                "ishas": "N",
                                "name": "3元还款抵用券",
                                "threshold": "3元还款抵用券"
                            }, {
                                "amount": 1.00,
                                "id": 1045,
                                "isGet": "N",
                                "isShow": "Y",
                                "ishas": "Y",
                                "name": "抓娃娃1元现金红包",
                                "threshold": "抓娃娃1元现金红包"
                            }, {
                                "amount": 9.00,
                                "id": 1048,
                                "isGet": "N",
                                "isShow": "N",
                                "ishas": "Y",
                                "name": "9元还款抵用券",
                                "threshold": "9元还款抵用券"
                            }]
                        },
                        "msg": "获取优惠券列表成功",
                        "success": true
                    }
                    self.couponData = data.data.couponList;
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
            // 获取秒杀商品列表
            $.ajax({
                url: "/activity/double12/goodsHomePage",
                type: 'POST',
                dataType: 'json',
                success: function (data) {
                    console.log(data.data)
                    if (!data.success) {
                        requestMsg("哎呀，获取优惠券出错了！");
                        return false;
                    }
                    self.goodsData = data.data.goodsMap;
                    for (const key in self.goodsData) {
                        if (self.goodsData.hasOwnProperty(key)) {
                            const element = self.goodsData[key];
                            if (element.type == "O") {
                                self.currentData = element;
                            }
                        }
                    }
                    // $(".loadingMask").fadeOut();
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
            let couponId = item.id;
            if (item.isGet == "Y") {
                requestMsg("您已经领取过了，快去使用吧");
                return false;
            }
            $.ajax({
                url: self.couponUrl,
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    console.log("returnData=>>", returnData)
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
            /*  if (item.state == 2) {
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
             } */
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
                data: {
                    maidianInfo: '/fanbei-web/activity/barginIndex?userName=' + userName,
                    spread: spread
                },
                success: function (data) {
                    console.log(data)
                }
            });
        },
        changeFlag: function () {
            this.couponFlag = !this.couponFlag;
        },
        changeProduct: function (key) {
            this.currentData = this.goodsData[key];
        }
    }
});