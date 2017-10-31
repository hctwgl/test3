let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol + '//' + host;
var goodsId = '';
var goodsType = '';
var userName = getUrl('userName'); //获取用户id

let vm = new Vue({
    el: "#barginIndex",
    data: {
        shareTime: 0, //分享次数
        totalData: {}, //所有商品数据
        firstGoods: {}, // iPhonex
        ruleFlag: false, //规则显示flag
        downTime: { d: 0, h: 0, m: 0, s: 0 }, // 倒计时时间
        isWX: false, //是否是微信浏览器
        pageNo: 1, // 加载页数
        sureFlag: false, // 确认是否砍价,
        shareFlag: false,
        goodsId: 0,
        cutData: '',
        goodsType: '',
        url_3: '/activity/de/share',
        url_1: '/activity/de/goods',
        url_2: '/fanbei-web/pickCoupon',
        couponData: [],
        ajaxFlag: true

    },
    created: function() {
        this.judge();
        this.maidian();
    },
    computed: {
        couponNum() {
            return this.firstGoods.couponList ? this.firstGoods.couponList.filter((a, b) => { return a.state != 0 }).length : 0
        }
    },
    methods: {
        judge: function() {
            let ua = window.navigator.userAgent.toLowerCase();
            // todo: 不完善，没有判断其他浏览器
            if (ua.match(/MicroMessenger/i) == 'micromessenger') {
                this.isWX = true;
                this.url_3 = "/activityH5/de/share";
                this.url_1 = "/activityH5/de/goods";
                this.url_2 = "/activityH5/de/pickCoupon";
            } else {
                this.isWX = false;
            }
            this.logData();
        },
        logData: function() { // get 初始化 信息
            let self = this;
            $.ajax({
                url: self.url_1,
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    if (!data.success) {
                        requestMsg("哎呀，出错了！");
                        return false;
                    }
                    self.totalData = data.data;
                    self.getFirstData();
                    self.countDown();
                    self.getShareTimes();
                    $(".loadingMask").fadeOut();
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        getFirstData: function() {
            var self = this;
            let d = self.totalData.goodsList;
            for (var i = d.length - 1; i >= 0; i--) {
                if (d[i].type == 1) {
                    self.firstGoods = d[i];
                    self.couponData = d[i].couponList;
                    break;
                }
            }
        },
        getShareTimes: function() { // 统计分享次数
            let self = this;
            let d = self.totalData.goodsList;
            console.dir(self.totalData, self.shareTime);
            for (var i = 0; i < d.length; i++) {
                if (d[i].share == 1 && d[i].type != 1) {
                    self.shareTime++;
                }
            }
        },
        showRule: function() {
            this.ruleFlag = true;
        },
        closeRule: function() {
            this.ruleFlag = false;
        },
        closeSure: function() {
            this.sureFlag = false;
        },
        closeShare: function() {
            this.shareFlag = false;
        },
        share: function(id, type,status) { // 点击发起砍价
            goodsId = id;
            goodsType = type;
            shareInfo.link = urlHost + '/fanbei-web/activity/barginProduct?goodsId='+goodsId+'&productType=share'+ goodsType +'&userName='+ userName;
            let self = this;
            if (status==1) {
                self.shareSure();
            } else {
                if (type == 'product') {
                    if (self.shareTime >= 2) {
                        requestMsg('只能砍价两件商品，不要太贪心哦');
                        return false;
                    }
                    self.sureFlag = true; 
                }else {
                    self.shareFn();
                }
            }
        },
        shareFn: function(){ // 分享确认
            let self = this;
            self.sureFlag = false;
            if (self.ajaxFlag) {
                self.ajaxFlag = false;
                $.ajax({
                    url: self.url_3,
                    type: 'POST',
                    dataType: 'json',
                    data: { goodsPriceId: goodsId },
                    success: function(data) {
                        if (!data.success) { // 返回不可砍價
                            if (!data.hasOwnProperty("data")) {
                                requestMsg(data.msg);
                            } else {
                                if (self.isWX) {
                                    location.href = "./barginLogin";
                                } else {
                                    location.href = data.data.loginUrl;
                                }
                            }
                            return false;
                        }
                        self.shareSure();
                    },
                    error: function() {
                        requestMsg("哎呀，出错了！");
                    },
                    complete: function() {
                        self.ajaxFlag = true;
                    }
                }); 
            }
        },
        shareSure: function() { //发起分享
            this.sureFlag = false;
            // 是否登录,APP_SHARE接口会自动判断是否登陆
            if (this.isWX) {
                //todo: 弹窗提示分享
                this.shareFlag = true;
            } else {
                var dat = {
                    shareAppTitle: "51返呗邀请有礼，快来参与~",
                    shareAppContent: "我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",
                    shareAppImage: "https://f.51fanbei.com/h5/common/icon/midyearCorner.png",
                    shareAppUrl: urlHost + '/fanbei-web/activity/barginProduct?goodsId=' + goodsId + '&productType=share' + goodsType + '&userName=' + getInfo().userName,
                    isSubmit: 'Y',
                    sharePage: 'barginIndex'
                }
                dat = JSON.stringify(dat)
                var base64 = BASE64.encoder(dat)
                window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params=' + base64
            }
        },
        toList: function(id) { // 跳转到榜单页
            location.href = "/fanbei-web/activity/barginList?goodsId=" + id;
        },
        toProduct: function(id, type) { // 跳转到商品页
            var name = userName ? userName : getInfo().userName;
            location.href = "/fanbei-web/activity/barginProduct?goodsId=" + id + "&productType=" + type + "&userName=" + name;
        },
        countDown: function() { // 倒计时
            let self = this;
            let timer = setInterval(function() {
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
        couponClick: function(item,index) {
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
                        success: function(returnData) {
                            if (returnData.success) {
                                requestMsg("优惠劵领取成功");
                                self.$set(self.couponData[index],'state',1);
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
                        error: function() {
                            requestMsg("哎呀，出错了！");
                        },
                        complete: function() {
                            self.ajaxFlag = true;
                        }
                    });   
                }
            }else if (item.state == 1) {
                requestMsg("您已经领取，快去使用吧");
            }
        },
        buy: function(id) {
            if (this.isWX) {
                // 跳转应用商城
                location.href = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www';
            } else {
                // 跳转原生app商品购买页
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + id + '"}';
            }
        },
        maidian(data){
            //数据统计
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/barginIndex?userName='+userName},
                success:function (data) {
                    console.log(data)
                }
            });
        }
    }
});