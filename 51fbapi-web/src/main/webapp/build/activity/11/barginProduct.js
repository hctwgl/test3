let goodsId = getUrl('goodsId');//获取模板id
let productType = getUrl('productType');//获取模板id
/*
    productType
    iPhone: iPhone商品页
    product: 普通商品页
    shareiPhone: 分享iPhone商品页
    shareProduct: 分享普通商品页
*/

let vm = new Vue({
    el: "#barginProduct",
    data: { 
        goodsData:{}, //所有商品数据
        friendData: {},
        ruleFlag: false, //规则显示flag
        barginFlag: false, //砍价显示flag
        downTime: {d: 0, h: 0, m: 0, s: 0},
        productType: productType,
        cutData: '',
        progressWidth: 0,  // 进度条长度
        tipLeft:0, // 砍价进度提示
    },
    created: function() {
        this.logData();
        this.countDown();
    },
    methods: {
        // get 初始化 信息
        logData:function() {
            let self = this;
            // requestMsg('哎呀，出错了！');
            // let data = {
            //     endTime: "2017-11-12 00:00:00",
            //     image: 'https://f.51fanbei.com/h5/app/activity/11/image/bargin03.png',
            //     originalPrice: 2892,
            //     cutPrice: 265,
            //     name: "格兰仕微波炉DH78787大容量",
            //     totalCount: 2
            // }
           

            let data2 = {
                pageNo: 1,
                friendList: [
                    {
                        headImgUrl: 'https://f.51fanbei.com/h5/app/activity/11/image/head.png',
                        nickname: 'laowang',
                        cutPrice: 256,
                        remainPrice: 8489
                    },
                    {
                        headImgUrl: 'https://f.51fanbei.com/h5/app/activity/11/image/head.png',
                        nickname: 'hwehfuwef',
                        cutPrice: 454,
                        remainPrice: 8022
                    },
                    {
                        headImgUrl: 'https://f.51fanbei.com/h5/app/activity/11/image/head.png',
                        nickname: '的覅微风纪委',
                        cutPrice: 1110,
                        remainPrice: 7000
                    },
                    {
                        headImgUrl: 'https://f.51fanbei.com/h5/app/activity/11/image/head.png',
                        nickname: '车位费围观围观',
                        cutPrice: 66,
                        remainPrice: 6934
                    },

                ]
            }
            self.friendData = data2
            console.log(self.goodsData)
            $.ajax({
                url: '/activity/de/goodsInfo',
                type: 'POST',
                dataType: 'json',
                data: {goodsPriceId: goodsId},
                success: function(data){
                    console.log("initData=", data);
                    self.goodsData = data.data;
                    this.progressWidth = 6.2*this.goodsData.cutPrice/this.goodsData.originalPrice; // 计算滚动条长度
                    this.tipLeft = this.progressWidth - 0.74;
                }
            });
            // $.ajax({
            //     url: '/fanbei-web/activity/de/friend',
            //     type: 'POST',
            //     dataType: 'json',
            //     data: {goodsPriceId: goodsId, pageNo: 1, userName: "52133"},
            //     success: function(data){
            //         console.log("initData=", data);
            //         // goodsList = data.goodsList;
            //         getShareTimes();
            //     }
            // });

        },
        showRule: function() {
            this.ruleFlag = true;
            console.log('click')
        },
        closeRule: function() {
            this.ruleFlag = false;
        },
        countDown: function() {
            let self = this;
            let timer = setInterval(function() {
                let now = new Date();
                let endTime = new Date(self.goodsData.endTime);
                let t = (endTime.getTime() - now.getTime())/1000;
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
        cut: function() {
            // $.ajax({
            //     url: '/fanbei-web/activityH5/de/cutprice',
            //     type: 'POST',
            //     dataType: 'json',
            //     data: {
                        // userId: "125465",
                        // openId: "1254511",
                        // nickName: "老王",
                        // headImgUrl: "https://f.51fanbei.com/h5/app/activity/11/image/head.png"
                //},
            //     success: function(data){
            //         console.log("initData=", data);
            //         // goodsList = data.goodsList;
            //         getShareTimes();
            //     }
            // });
            let data = {
                cutPrice: 236,
                code: 1
            }
            this.barginFlag = true;
            this.cutData = data;   
            // if (data.code==1) {
            // } else if (data.code==2) {
            //     requestMsg("已经砍过价了");
            // } else {
            //     requestMsg("已砍至最低价，无法完成本次砍价");
            // }
        },
        closeBargin: function () {
            this.barginFlag = false;
        }
    }
})