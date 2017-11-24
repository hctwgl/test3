let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;
var userName = getUrl('userName'); //获取用户id
var spread = getUrl('spread'); //获取用户id


let vm = new Vue({
    el: "#main",
    data: {
        goodsData: {}, //所有商品数据
        couponData: [],
        currentData: [],
        redRainData: [],
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
        this.maidian("enter=true");
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
                    self.$nextTick(() => {
                        let allWidth = $("#dayBox").width();
                        let w = $(".status-2").width();
                        let shouldX = (allWidth-w)/2;
                        let posX = $(".status-2").offset().left;
                        if (posX > shouldX) {
                            let moveX = posX - shouldX;
                            $("#dayBox").scrollLeft(moveX)
                        }
                    })
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
            // 获取红包雨开始时间
            $.ajax({
                url: "/activity/double12/startTime",
                type: 'POST',
                dataType: 'json',
                success: function (data) {
                    if (!data.success) {
                        return false;
                    }
                    self.redRainData = data.data;
                    self.countDown();
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });

        },
        countDown: function () { // 倒计时
            let self = this;
            let timer = setInterval(function () {
                let t = (self.redRainData.startTime - self.redRainData.currentTime) / 1000;
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
                self.redRainData.currentTime += 1000;
            }, 1000);
        },
        /*点击优惠券*/
        couponClick: function (item, index) {
            let self = this;
            let couponId = item.id;
            if (!self.isApp) {
                // TODO: 跳转登录页
                this.toRegister();
                return false;
            }
            if (item.isGet == "Y") {
                requestMsg("您已经领取过了，快去使用吧");
                return false;
            }
            if (item.isShow == 'N') {
                requestMsg("活动暂未开始");
                return false;
            }
            if (item.isShow == 'E') {
                requestMsg("活动已结束");
                return false;
            }
            $.ajax({
                url: "/activity/double12/getCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    console.log("returnData=>>", returnData)
                    let d = returnData.data;
                    if (returnData.success) {
                        if (d.result == 'Y') {
                            requestMsg("优惠劵领取成功");
                            self.$set(self.couponData[index], 'isGet', 'Y');
                            self.maidian("couponSuccess=true");
                        } else if (d.result == 'N') {
                            requestMsg("您已经领取过了，快去使用吧");
                            self.maidian("couponSuccess=got")
                        } else {
                            requestMsg("今日优惠券已领取完毕，明天再来领取吧");
                            self.maidian("couponSuccess=end")
                        }
                    } else {
                        if (d == '') {
                            requestMsg("哎呀，出错了！");
                        } else {
                            location.href = d.loginUrl;
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
        },
        buy: function (id) {
            if (this.isApp) {
                // 跳转注册页
                // TODO:
                this.toRegister();
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
                    maidianInfo: '/fanbei-web/activity/doubleTwelve?userName=' + userName+"&" +data + "&spread="+ spread
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
        },
        toRegister: function () {
            location.href = "doubleTwelveRegister?spread=" + spread;
         }    
    }
});