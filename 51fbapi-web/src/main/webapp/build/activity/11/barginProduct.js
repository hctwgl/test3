let goodsId = getUrl('goodsId');//获取商品id
let productType = getUrl('productType');//获取商品类型
let goodsType  = getUrl('productType');//获取商品类型
let userName = getUrl('testUser');//获取用户id
let code = getUrl("code"); // 获取code
let protocol = window.location.protocol;
let host = window.location.host;
let urlHost = protocol+'//'+host;

/*
    productType
    iPhone: iPhone商品页
    product: 普通商品页
    shareiPhone: wx分享iPhone商品页
    shareproduct: wx分享普通商品页
*/

let vm = new Vue({
    el: "#barginProduct",
    data: { 
        goodsData:{}, //所有商品数据
        friendData: [],
        ruleFlag: false, //规则显示flag
        barginFlag: false, //砍价显示flag
        downTime: {d: 0, h: 0, m: 0, s: 0},
        productType: productType,
        isWX: false,    //是否是微信浏览器
        cutData: '',
        progressWidth: 0,  // 进度条长度
        tipLeft:0, // 砍价进度提示
        loadFlag: false,
        listNum: 1,
        url_1: '/activity/de/goodsInfo',
        url_2: '/activity/de/friend',
        userInfo: {}  //用户信息
    },
    created: function() {
        this.judge();
        this.logData();
        this.listFn();
        this.countDown();
    },
    methods: {
        judge: function() {
            let ua = window.navigator.userAgent.toLowerCase(); 
            // todo: 不完善，没有判断其他浏览器
            if (ua.match(/MicroMessenger/i) == 'micromessenger') { 
                this.isWX = true;
                this.url_1 = "/activityH5/de/goodsInfo";
                this.url_2 = "/activityH5/de/friend";
                let str = encodeURIComponent('http://ktestapp.51fanbei.com/fanbei-web/activity/barginProduct');
                 
                if (!code) {
                    location.href = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx583e90560d329683&redirect_uri='+str+'&response_type=code&scope=snsapi_userinfo&state=123&connect_redirect=1#wechat_redirect';    
                }
            } else { 
                this.isWX = false;
            }
        },
        // get 初始化 信息
        logData:function() {
            let self = this;
            $.ajax({
                url: self.url_1,
                type: 'POST',
                dataType: 'json',
                data: {goodsPriceId: goodsId, userName: userName},
                success: function(data){
                    $(".loadingMask").fadeOut();
                    if (!data.success) {
                        if (self.isWX) {
                           // self.toLogin(); 
                           requestMsg("哎呀，出错了！")
                        }else {
                            location.href = data.data.loginUrl;
                        }
                        return false;
                    }
                    self.goodsData = data.data;
                    self.progressWidth = 6.2*self.goodsData.cutPrice/self.goodsData.originalPrice; // 计算滚动条长度
                    self.tipLeft = self.progressWidth - 0.74;
                },
                error: function() {
                    requestMsg("哎呀，出错了！")
                }
            });
            $.ajax({
                url: '/activity/de/wechat/userInfo',
                type: 'POST',
                dataType: 'json',
                data: {code: code},
                success: function(data){
                    if (data.success) {
                        self.userInfo = data.data;
                    }
                },
                error: function() {
                    requestMsg("哎呀，出错了！")
                }
            });
        },
        listFn: function() {
            let self = this;
            self.loadFlag = false;
            $.ajax({
                url: self.url_2,
                type: 'POST',
                dataType: 'json',
                data: {goodsPriceId: goodsId, pageNo: self.listNum, userName: userName},
                success: function(data){
                    if (data.success){
                        console.log("frend=",data)
                        if (data.data.friendList.length>0) {
                            self.friendData = self.friendData.concat(data.data.friendList);
                            self.listNum++;
                            self.loadFlag = true;
                        }
                    } else {
                        if (self.isWX) {
                           // self.toLogin(); 
                        }else {
                            location.href = data.data.loginUrl;
                        }
                    }
                },
                error: function() {
                    requestMsg("哎呀，出错了！")
                }
            });   
        },
        showRule: function() {
            this.ruleFlag = true;
        },
        closeRule: function() {
            this.ruleFlag = false;
        },
        countDown: function() {
            let self = this;
            let timer = setInterval(function() {
                let t = (self.goodsData.endTime - new Date().getTime())/1000;
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
        toList: function () {
            location.href = "./barginList?goodsId=" + goodsId; 
        },
        toLogin: function() {
            location.href = "./barginLogin?goodsId=" + goodsId; 
        },
        cut: function() {
            let self = this;
            let user = self.userInfo;
            if (!user.hasOwnProperty("openid")) {
                requestMsg("哎呀，出错了！")
                return false;
            }

            $.ajax({
                url: '/activityH5/de/cutPrice',
                type: 'POST',
                dataType: 'json',
                data: {
                    "userId": userName,
                    "goodsPriceId": goodsId,
                    "openId": user.openid,
                    "nickName": user.nickname,
                    "headImgUrl": user.headimgurl
                },
                success: function(data){
                    self.barginFlag = true;
                    self.cutData = data;   
                },
                error: function() {
                    requestMsg("哎呀，出错了！");
                }
            });

        },
        closeBargin: function () {
            this.barginFlag = false;
        },
        scrollFn: function() {
            let self = this;
            $(window).on("scroll", function(e){ 
                if (self.loadFlag) {
                    var scrollTop = $(this).scrollTop();    //滚动条距离顶部的高度
                    var scrollHeight = $(document).height();   //当前页面的总高度
                    var clientHeight = $(this).height();    //当前可视的页面高度
                    if (scrollTop + clientHeight >= scrollHeight-20) {
                        self.listFn();
                    }
                }
            }) 
        },
        buy: function() {
            if (this.isWX) {
                location.href = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www';
            } else {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + goodsId + '"}';
            }
        },
        share: function() { 
            // 是否登录,APP_SHARE接口会自动判断是否登陆
            if (this.isWX) {
                //todo: 弹窗提示分享
                requestMsg("请点击右上角进行分享")
                this.toProduct(goodsId,"product");
            } else {
                var dat = {
                    shareAppTitle: "51返呗邀请有礼，快来参与~",
                    shareAppContent: "我知道一个反利APP，购物不仅返现，邀请好友也赚钱哦~",
                    shareAppImage: "https://f.51fanbei.com/h5/common/icon/midyearCorner.png",
                    shareAppUrl: urlHost + '/fanbei-web/activity/barginProduct?goodsId='+ goodsId+'&productType='+ productType +'&userName='+ getInfo().userName +'&testUser='+ getInfo().userName,
                    isSubmit: 'Y',
                    sharePage: 'barginIndex'
                }
                dat = JSON.stringify(dat)
                var base64 = BASE64.encoder(dat)
                window.location.href='/fanbei-web/opennative?name=APP_SHARE&params=' + base64
            }
        },
    }
})