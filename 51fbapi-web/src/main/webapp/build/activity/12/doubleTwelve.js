let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;
let groupId = getUrl('groupId');

let goodArr = Object.keys(Array.apply(null, {
    length: 8
})).map(() => {
    return {
        img: 'https://img2.mukewang.com/szimg/59b8a486000107fb05400300.jpg',
        link: 'http://baidu.com'
    }
})
let goodArr1 = Object.keys(Array.apply(null, {
    length: 8
})).map(() => {
    return {
        img: 'https://img4.sycdn.imooc.com/szimg/59eeb21c00012eb205400300.jpg',
        link: 'http://baidu.com'
    }
})

var userName = getUrl('userName'); //获取用户id
var spread = getUrl('spread'); //获取用户id


let vm = new Vue({
    el: "#main",
    data: {
        goodsData: [

        ],
        tabs: [{
                tab: "电子数码",
                key: 0,
            },
            {
                tab: "鞋服箱包",
                key: 1
            },
            {
                tab: "手表配饰",
                key: 2
            },
            {
                tab: "美妆护肤",
                key: 3
            },
        ],
        goods: [
            goodArr,
            goodArr1,
            goodArr,
            goodArr1,
        ],
        pretab: 0,
        nowkey: 0,
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
        touchstartx: null,
        touchendx: null,
        touchdefx: null,
        touchstarty: null,
        touchendy: null,
        touchdefy: null,
        isApp: true,
        couponUrl: "/activity/double12/couponHomePage",
    },
    created: function () {
        this.isAppFn();
        this.maidian("enter=true");
    },
    watch: {
        nowkey(val) {
            var width = -$('.rule').width()
            this.$nextTick(() => {
                $('.goodpi').css({
                    transform: "translateX(" + width * val + "px)",
                })
            })
        },
        touchdefx(val) {
            if (!this.isScroll) return
            if (val > 80) {
                if (this.nowkey < this.goods.length - 1) {
                    this.nowkey++
                }
            } else if (val < -80) {
                if (this.nowkey > 0) {
                    this.nowkey--
                }
            }
        }
    },
    computed: {
        couponNum() {
            return this.firstGoods.couponList ? this.firstGoods.couponList.filter((a, b) => {
                return a.state != 0
            }).length : 0
        },
        isScroll() {
            return Math.abs(this.touchdefy) < 50
        }

    },
    mounted() {
        var self = this
        this.$nextTick(() => {
            setTimeout(() => {
                $('.good_tab').pin({
                    containerSelector: ".good_con"
                })
                $('.goodpi').css({
                    width: $('.rule').width() * self.goods.length + 'px'
                })
                $('.goodeach').css({
                    width: $('.rule').width() + 'px'
                })

                $(".goodpi").on('touchstart', function (e) {
                    var _touch = e.originalEvent.targetTouches[0]
                    self.touchstartx = _touch.pageX
                    self.touchstarty = _touch.clientY
                })
                $(".goodpi").on('touchend', function (e) {
                    var _touch = e.originalEvent.changedTouches[0]
                    self.touchendx = _touch.pageX
                    self.touchendy = _touch.clientY
                    self.touchdefy = self.touchstarty - self.touchendy
                    self.touchdefx = self.touchstartx - self.touchendx
                })
            }, 0)
        })
    },
    methods: {
        changetab(i) {
            this.pretab = this.nowkey
            this.nowkey = i
        },
        linkto(good) {
            this.maidian("good=" +encodeURI(good.link));
            if (!this.isApp) {
                this.toRegister();
            }
            window.location.href = good.link
        },
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
            // $.ajax({
            //     type: 'post',
            //     url: "/fanbei-web/activityCouponInfo",
            //     data: { 'groupId': groupId },
            //     success: function (data) {
            //         let couponCont = eval('(' + data + ')').data;
            //         console.log(couponCont);
            //     },
            //     error: function () {
            //         requestMsg("哎呀，出错了！");
            //     }
            // });
            // 获取优惠券信息
            $.ajax({
                url: self.couponUrl,
                type: 'POST',
                dataType: 'json',
                data: {
                    groupId: groupId
                },
                success: function (data) {
                    if (!data.success) {
                        if (data.data!='') {
                            location.href = data.data.loginUrl;
                        }else {
                            requestMsg("哎呀，获取优惠券出错了！");
                        }
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
                    let flag = false;
                    for (const key in self.goodsData) {
                        if (self.goodsData.hasOwnProperty(key)) {
                            const element = self.goodsData[key];
                            if (element.type == "O") {
                                self.currentData = element;
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        self.currentData = self.goodsData[5];
                    }else {
                        self.$nextTick(() => {
                            let allWidth = $("#dayBox").width();
                            let w = $(".status-2").width();
                            let shouldX = (allWidth - w) / 2;
                            let posX = $(".status-2").offset().left;
                            if (posX > shouldX) {
                                let moveX = posX - shouldX;
                                $("#dayBox").scrollLeft(moveX);
                            }
                        })
                    }
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
           /*  $.ajax({
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
            }); */
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    console.log(returnData)
                    if (returnData.success) {
                        requestMsg("优惠劵领取成功");
                        self.$set(self.couponData[index], 'isGet', 'Y');
                        self.maidian("couponSuccess=true");
                    } else {
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            // requestMsg(returnData.msg);
                            requestMsg("您已经领取过了，快去使用吧");
                            self.maidian("couponSuccess=got")
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                            self.maidian("couponSuccess=noCoupon")
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                            self.maidian("couponSuccess=end")
                        }
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
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
                    maidianInfo: '/fanbei-web/activity/doubleTwelve?userName=' + userName + "&" + data + "&spread=" + spread
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