let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;
var domainName = protocol + '//' + host; //获取域名
let groupId = getUrl('groupId');
let userName = getUrl('userName'); //获取用户id
let spread = getUrl('spread'); //获取页面推广类型


let vm = new Vue({
    el: "#main",
    data: {
        goodsData: [], // 所有品牌数据
        nowkey: 0,
        nowkey2: 0,
        couponData: [],
        currentData: [], // 当前显示的秒杀商品
        triangleFlag: 0,
        isApp: true,
        couponUrl: "/appH5DoubleEggs/initCoupons",
        goodUrl: "/appH5DoubleEggs/getSecondKillGoodsList",
        currentGood: [], // 当前显示的品牌商品
        brandData: {}, // 初始化品牌数据
        brandList: '', //一级品牌数据
        secondBrandList: '', // 二级品牌数据
        tabFlag: false, //tab显示or隐藏flag
        nowTime: 0, // 当前服务器时间（略有误差）
        secondKillData: '',
        loadFlag: [false, false, false], //加载flag
    },
    created: function() {
        this.isAppFn();
        this.maidian("enter=true");
    },
    watch: {
        loadFlag() {
            if (this.loadFlag[0] && this.loadFlag[1]  && this.loadFlag[2] ) {
                this.$nextTick(() => {
                    setTimeout(() => {
                        $('#tabBox').pin({
                            containerSelector: ".good_con"
                        })
                    }, 0)
                })
            }
        }
    },
    computed: {
        couponNum() {
            return this.firstGoods.couponList ? this.firstGoods.couponList.filter((a, b) => {
                return a.state != 0
            }).length : 0
        }
    },
    methods: {
        changetab(i) {
            this.nowkey = i
            this.nowkey2 = 0
            this.secondBrandList = this.brandList[i].secondCategoryList
            this.changeGoodsData(i, 0)
            this.tabFlag = false
        },
        changetab2(i) {
            this.nowkey2 = i
            this.changeGoodsData(this.nowkey, i)
        },
        isAppFn: function() {
            let isAppParam = getUrl('spread');
            if (isAppParam != 'app') {
                this.isApp = false;
                this.couponUrl = "/H5DoubleEggs/initCoupons";
                this.goodUrl = "/H5DoubleEggs/getSecondKillGoodsList";
            }
            this.logData();
        },
        logData: function() { // get 初始化 信息
            let self = this;
            // 获取优惠券信息
            // var flag1,flag2.flag3 = false,false,false;
            $.ajax({
                url: self.couponUrl,
                type: 'POST',
                dataType: 'json',
                data: {
                    groupId: groupId
                },
                success: function(data) {
                    $(".loadingMask").fadeOut();
                    if (!data.success) {
                        if (data.data != '') {

                        } else {
                            requestMsg("哎呀，获取优惠券出错了！");
                        }
                        return false;
                    }
                    self.couponData = data.data.couponList;
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                },
                complete: function() {
                    self.$set(self.loadFlag, 0, 'true');
                }
            });
            // 获取秒杀商品列表
            $.ajax({
                url: self.goodUrl,
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    if (!data.success) {
                        requestMsg("哎呀，获取商品信息出错了！");
                        return false;
                    }
                    $(".loadingMask").fadeOut();
                    self.secondKillData = data.data.goodsList;
                    self.nowTime = data.data.serviceDate;
                    var d1 = new Date(parseInt(self.nowTime)).toLocaleString();
                    d1 = d1.replace(new RegExp(/-/gm) ,"/");
                    var arr1 = d1.split(" ");
                    var arr2 = arr1[0].split("/");
                    var nowDate = new Date(arr1[0]).getTime();


                    for (var i = 0; i < self.secondKillData.length; i++) {
                        var t = self.secondKillData[i].startTime;
                        var date = new Date(parseInt(t)).toLocaleString();
                        date = date.replace(new RegExp(/-/gm) ,"/");// 兼容iOS系统
                        var arr1 = date.split(" ");
                        var arr2 = arr1[0].split("/");
                        var d = arr2[1] + "月" + arr2[2] + "日";
                        self.secondKillData[i].date = d;
                        self.secondKillData[i].day = new Date(arr1[0]).getTime();
                        if (nowDate > self.secondKillData[i].day) {
                            self.secondKillData[i].type = 0;
                        } else if (nowDate == self.secondKillData[i].day) {
                            self.secondKillData[i].type = 1;
                        } else {
                            self.secondKillData[i].type = 2;
                        }
                    }
                    let flag = false;
                    for (const key in self.secondKillData) {
                        if (self.secondKillData.hasOwnProperty(key)) {
                            const element = self.secondKillData[key];
                            if (element.type == 1) {
                                self.currentData = element;
                                self.triangleFlag = key;
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        self.currentData = self.secondKillData[0];
                        self.triangleFlag = 0;
                    } else {
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
                        self.countDown(self.triangleFlag);
                    }
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                },
                complete: function() {
                    self.$set(self.loadFlag, 1, 'true');
                }
            });
            // 获取品牌
            $.ajax({
                url: "/appH5DoubleEggs/initOnsaleGoods",
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    if (!data.success) {
                        requestMsg("哎呀，获取品牌出错了！");
                        return false;
                    }
                    self.brandData = data.data;
                    self.brandList = data.data.firstCategoryList;
                    self.secondBrandList = data.data.firstCategoryList[0].secondCategoryList;
                    self.currentGood = data.data.goodsList;
                    self.initGoodsData();


                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                },
                complete: function() {
                    self.$set(self.loadFlag, 2, 'true');
                }
            });

        },
        initGoodsData: function() {
            let self = this;
            let data = self.brandList;
            self.goodsData = new Array(data.length);
            for (var i = 0; i < data.length; i++) {
                self.goodsData[i] = new Array(data[i].secondCategoryList.length)
            }
            self.goodsData[0][0] = self.currentGood;
        },
        changeGoodsData: function(a, b) { // 点击品牌分类
            let d = this.goodsData[a][b];
            if (d == undefined) {
                var a = this.brandList[a].secondCategoryList[b];
                if (a != undefined) {
                    this.getGoodsData(this.secondBrandList[b].secondCategoryId, a, b);
                }
            } else {
                this.currentGood = this.goodsData[a][b];
            }
        },
        getGoodsData: function(brandId, a, b) {
            let self = this;
            // 获取单个品牌数据
            $.ajax({
                url: "/appH5DoubleEggs/getOnSaleGoods",
                type: 'POST',
                // dataType: 'json',
                data: {
                    secondCategoryId: brandId
                },
                success: function(data) {
                    if (!data.success) {
                        requestMsg("哎呀，获取品牌出错了！");
                        return false;
                    }
                    self.goodsData[self.nowkey][self.nowkey2] = data.data.goodsList
                    self.currentGood = data.data.goodsList;
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        countDown: function(num) { // 倒计时
            let self = this;
            let data = self.secondKillData[num];
            console.log("time=", data)
            let timer = setInterval(function() {
                self.nowTime += 1000;
                if (self.nowTime >= data.startTime) {
                    for (var i = 0; i < data.goodsListForDate.length; i++) {
                        var d = data.goodsListForDate[i];
                        d.status = 2;
                    }
                    clearInterval(timer);
                }
            }, 1000);
        },
        /*点击优惠券*/
        couponClick: function(item, index) {
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
                requestMsg("活动暂未开始,敬请期待");
                return false;
            }
            if (item.isShow == 'E') {
                requestMsg("活动已结束");
                return false;
            }

            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function(returnData) {
                    if (returnData.success) {
                        requestMsg("优惠劵领取成功");
                        self.$set(self.couponData[index], 'isGet', 'Y');
                        self.maidian("couponSuccess=true&couponId=" + couponId);
                    } else {
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            // requestMsg(returnData.msg);
                            requestMsg("您已经领取过了，快去使用吧");
                            self.maidian("couponSuccess=got&couponId=" + couponId)
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                            self.maidian("couponSuccess=noCoupon&couponId=" + couponId)
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                            self.maidian("couponSuccess=end&couponId=" + couponId)
                        }
                    }
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        productFn: function(item, index) {
            // this.maidian("productId=" + id);
            // status   0:“立即预约“；1：”已预约“；2：”去秒杀“3：”已秒杀“
            // type   0: 已结束  1：进行中   2：即将开始
            console.log(this.isApp == true)
            if (this.isApp) {
                if (this.secondKillData[this.triangleFlag].type == 0) {
                    return false;
                }
                if (item.status == 0) { // 立即预约
                    this.bookFn(item, index);
                } else if (item.status == 1) {
                    requestMsg("您已预约过，无需再预约");
                    return false;
                } else if (item.status == 2) {
                    if (item.count == 0) {
                        requestMsg("您来晚了，商品已抢光");
                        return false;
                    }
                    // 跳转秒杀页秒杀
                    this.maidian("secKillGoodId="+ item.doubleGoodsId);
                    window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + item.doubleGoodsId + '"}';
                    // this.buy(item.doubleGoodsId);
                } else {
                    requestMsg("您已秒杀过此商品");
                }
            } else {
                if (item.type == 0) {
                    return false;
                }
                // 跳转注册页
                this.toRegister();
            }

        },
        bookFn: function(item, index) {
            this.maidian("bookBtn=click")
            let self = this;
            $.ajax({
                url: "/appH5DoubleEggs/subscribe",
                type: 'POST',
                dataType: 'json',
                data: {
                    goodsId: item.doubleGoodsId
                },
                success: function(data) {
                    if (!data.success) {
                        if (data.data.loginUrl) {
                            location.href = data.data.loginUrl;
                            return false;
                        }
                        requestMsg("哎呀，出错了,预约失败");
                        return false;
                    }
                    requestMsg("设置提醒成功，商品开抢后将短信通知您");
                    self.secondKillData[self.triangleFlag].goodsListForDate[index].status = 1;
                    self.maidian("bookBtn=success")
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        buy: function(id) {
            this.maidian("clickProductId="+id)
            if (this.isApp) {
                // 跳转原生app商品购买页
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + id + '"}';
            } else {
                // 跳转注册页
                this.toRegister();
            }
        },
        maidian(data) {
            //数据统计            // 推广埋点
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {
                    maidianInfo: '/fanbei-web/activity/doubleEgg?userName=' + userName + "&" + data,
                    maidianInfo1: spread
                },
                success: function(data) {
                    console.log(data)
                }
            });
        },
        changeProduct: function(key) {
            this.currentData = this.secondKillData[key];
            this.triangleFlag = key;
            $(".product-box").scrollLeft(0);
        },
        showMore: function() {
            this.tabFlag = true;
        },
        closeMore: function() {
            this.tabFlag = false;
        },
        toRegister: function() {
            location.href = "doubleEggRegister.html?spread=" + spread;
        },
        initScroll: function() {
            let t = sessionStorage.getItem('goodType');
            if (t != null) {
                this.nowkey = t;
                setTimeout(() => {
                    $(window).scrollTop(sessionStorage.getItem('scrollTop'));
                    sessionStorage.clear();
                }, 100);
            }
        }
    }
});



// app调用web的方法
function alaShareData() {
    var dataObj = { // 分享内容
        "appLogin": "N", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "元旦狂欢盛宴 畅想欢乐购", // 分享的title
        'shareAppContent': "抢神券红包雨 1212元秒杀iPhone X，X任性到底，欢乐购不停~", // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/12/double12_share.png", // 分享右边小图
        "shareAppUrl": domainName + "/fanbei-web/activity/doubleEgg?groupId=" + groupId + "&spread=2", // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "doubleEgg" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj); // obj对象转换成json对象
    return dataStr;
};

// wx分享参数
var shareInfo = {
    title: "元旦狂欢盛宴 畅想欢乐购",
    desc: "抢神券红包雨 1212元秒杀iPhone X，元旦任性到底，欢乐购不停~",
    link: domainName + "/fanbei-web/activity/doubleEgg?groupId=" + groupId + "&spread=" + spread,
    imgUrl: "http://f.51fanbei.com/h5/app/activity/12/double12_share.png",
    success: function() {
        alert("分享成功！");
    },
    error: function() {
        alert("分享失败！");
    },
    cancel: function(res) {
        // 用户取消分享后执行的回调函数
        alert("取消分享！");
    }
}