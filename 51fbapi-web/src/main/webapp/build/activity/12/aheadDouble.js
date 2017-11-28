let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;
let groupId = getUrl('groupId');
let goodArr1 = [
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/apple.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=197',
        name:'苹果'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/vivo.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=195',
        name:'vivo'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/oppo.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=193',
        name:'oppo'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/xiaomi.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=203',
        name:'小米'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/huawei.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=204',
        name:'华为'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/honer.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=205',
        name:'荣耀'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/meizu.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=213',
        name:'魅族'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/telephone/meitu.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=202',
        name:'美图'
    }
];
let goodArr2 = [
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/sony.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=219',
        name:'SONY索尼专题'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/nikan.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=215',
        name:'Nikon尼康专题'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/canon.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=212',
        name:'Canon佳能专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/acer.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=211',
        name:'宏碁(acer)'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/sansung.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=210',
        name:'三星（SAMSUNG）'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/hp.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=209',
        name:'惠普(HP)'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/vsus.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=208',
        name:'华硕（ASUS）'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/lenove.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=207',
        name:'联想（Lenovo）'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/dell.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=206',
        name:'戴尔（DELL）'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/computer/huawei.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=221',
        name:'huawei'
    }
];
let goodArr3 = [
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/DW.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=191',
        name:'DW品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/tiansuo.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=192',
        name:'天梭品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/CASIO.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=194',
        name:'卡西欧品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/armani.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=201',
        name:'阿玛尼品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/Rossini.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=200',
        name:'罗西尼品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/Longines.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=199',
        name:'浪琴品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/SWAROVSKI.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=190',
        name:'施华洛世奇品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/zhekun.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=189',
        name:'喆堃珠宝品牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/sports.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=196',
        name:'服饰运动潮牌专场'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/watch/MK.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=198',
        name:'MK品牌专场'
    }
];
let goodArr4 = [
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/household/supor.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=214',
        name:'家电-苏泊尔 '
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/household/liren.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=216',
        name:'家电-利仁'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/household/sansung.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=217',
        name:'家电-三星'
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/household/media.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=218',
        name:'家电-美的 '
    },
    {
        img: 'https://f.51fanbei.com/h5/app/activity/12/brand/household/jiuyang.png',
        link: 'https://app.51fanbei.com/app/goods/goodsListModel?modelId=220',
        name:'家电-九阳'
    }
];
var userName = getUrl('userName'); //获取用户id
var spread = getUrl('spread'); //获取用户id


let vm = new Vue({
    el: "#main",
    data: {
        goodsData: [

        ],
        tabs: [{
                tab: "手机",
                key: 0,
            },
            {
                tab: "数码电脑",
                key: 1
            },
            {
                tab: "手表配饰",
                key: 2
            },
            {
                tab: "家电",
                key: 3
            },
        ],
        goods: [
            goodArr1,
            goodArr2,
            goodArr3,
            goodArr4,
        ],
        pretab: 0,
        nowkey: 0,
        couponData: [],
        currentData: [],
        redRainData: [],
        couponFlag: true, // 显示优惠券flag
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

        toRegister: function () {
            location.href = "doubleTwelveRegister?spread=" + spread;
        }
    }
});