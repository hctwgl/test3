let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol+'//'+host;

let vm = new Vue({
    el: "#barginIndex",
    data: { 
        shareTime: 0, //分享次数
        totalData:{}, //所有商品数据
        firstGoods: {}, // iPhonex
        ruleFlag: false, //规则显示flag
        downTime: {d: 0, h: 0, m: 0, s: 0}, // 倒计时时间
        couponNum: 0,
        isWX: false,    //是否是微信浏览器
        pageNo: 1,
        sureFlag: false, // 确认是否砍价,
        goodsId: 0,
        cutData: ''
    },
    created: function() {
        this.logData();
        this.judge();
    },
    methods: {
        judge: function() {
            let ua = window.navigator.userAgent.toLowerCase(); 
            // todo: 不完善，没有判断其他浏览器
            if (ua.match(/MicroMessenger/i) == 'micromessenger') { 
                this.isWX = true;
            } else { 
                this.isWX = false;
            } 
        },
        logData:function() {    // get 初始化 信息
            let self = this;
            $.ajax({
                url: '/activity/de/goods',
                type: 'POST',
                dataType: 'json',
                success: function(data){
                    console.log("initData=", data.data);
                    console.log("initData=", data.data.goodsList[0].couponList);
                    if (!data.success) {
                        requestMsg("哎呀，出错了！");
                        return false;
                    }
                    self.totalData = data.data;
                    self.getFirstData();
                    self.countDown();
                    self.getShareTimes();
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        getFirstData: function(id) {
            var self = this;
            console.log("--->>",this.couponNum)
            let d = self.totalData.goodsList;
            for (var i = d.length - 1; i >= 0; i--) {
                if (d[i].type==1) {
                    self.firstGoods = d[i];
                    break;
                }
            }      
            let a =  self.firstGoods.couponList;
            for (var i = 0; i < a.length; i++) {
                if (a[i].state == 0) {
                    self.couponNum = parseInt(self.couponNum) +1;
                    console.log(">>>",a[i],self.couponNum)
                    
                }
            }
            console.log(">>>",self.couponNum)
            // todo: 优惠券数量bug
        },
        getShareTimes: function() {        // 统计分享次数
            let self = this;
            let d = self.totalData.goodsList;
            console.dir(self.totalData,self.shareTime);
            for (var i = 0; i <d.length; i++) {
                if (d[i].share==1 && d[i].type!=1) {
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
        share: function(id,type) { // 点击发起砍价
            if (type=='product') {
                if (this.shareTime>=2) {
                    requestMsg('只能砍价两件商品，不要太贪心哦');
                    return false;
                }
                this.sureFlag = true;
                this.goodsId = id;
            } else {
                this.shareSure();
            }
        },
        shareSure: function() {
            console.log("假装调用了app接口");
            this.sureFlag = false;
            // 是否登录,APP_SHARE接口会自动判断是否登陆
            if (this.isWX) {
                //todo: 弹窗提示分享
                this.toProduct(this.goodsId,"product");
            } else {
                // todo: userName
                //window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"51返呗邀请有礼，快来参与~","shareAppContent":"我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~","shareAppImage":"https://f.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"'+urlHost+'/fanbei-web/activity/barginProduct?goodsId'+this.goodsId+'&productType=product&userName=","isSubmit":"Y","sharePage":"barginIndex"}'; 
            }
        },
        toList: function(id) { // 跳转到榜单页
            location.href = "./barginList?goodsId=" + id;
        },
        toProduct: function(id,type) { // 跳转到商品页
            location.href = "./barginProduct?goodsId=" + id + "&productType=" + type;
        },
        countDown: function() { // 倒计时
            let self = this;
            let timer = setInterval(function() {
                let t = self.totalData.endTime - new Date().getTime()/1000;
                let d = 0;
                let h = 0;
                let m = 0;
                let s = 0;
                if (t>=0) {
                    d = Math.floor(t/60/60/24);
                    h = Math.floor(t/60/60%24);
                    m = Math.floor(t/60%60);
                    s = Math.floor(t%60);
                }
                self.downTime.d = d;
                self.downTime.h = h;
                self.downTime.m = m;
                self.downTime.s = s;
            }, 1000);      
        },
        /*点击优惠券*/
        couponClick:function(item) {
            //let self = this;
            let couponId = item.couponId;
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    console.log("假装领取了优惠券。couponClickReturn=",returnData);
                    return false;
                    if (returnData.success) {
                        requestMsg("优惠劵领取成功");
                    } else {
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            //requestMsg(returnData.msg);
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
                }
            })
        },
    }
})