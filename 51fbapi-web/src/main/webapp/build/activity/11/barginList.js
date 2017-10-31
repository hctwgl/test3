let goodsId = getUrl('goodsId');//获取模板id


let vm = new Vue({
    el: "#barginList",
    data: { 
        goodsData:{}, //所有商品数据
        listData: [],
        ruleFlag: false, //规则显示flag
        downTime: {d: 0, h: 0, m: 0, s: 0},
        loadFlag: false,
        listNum: 1
    },
    created: function() {
        this.logData();  
        this.maidian();
    },
    methods: {
        logData:function() {   // get 初始化 信息
            let self = this;    
            $.ajax({
                url: '/activity/de/top',
                type: 'POST',
                dataType: 'json',
                data: {goodsPriceId: goodsId},
                success: function(data){
                    console.log("initData=", data);
                    if (data.success) {
                        self.goodsData = data.data;
                        self.countDown();
                    } else {
                        requestMsg("哎呀，出错了！");
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
            self.listFn();
            self.scrollFn();
        },
        listFn: function() {
            let self = this;
            self.loadFlag = false;
            $.ajax({
                url: '/activity/de/topList',
                type: 'POST',
                dataType: 'json',
                data: {goodsPriceId: goodsId, pageNo: self.listNum},
                success: function(data){
                    if (data.success) {
                        if (data.data.listPerson.length>0) {
                            self.listData = self.listData.concat(data.data.listPerson);
                            self.listNum++;
                            self.loadFlag = true;
                        }
                    } else {
                        requestMsg("哎呀，出错了！");
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
            
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
        scrollFn: function() {
            console.log("start")
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
        maidian(data){
            //数据统计
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/barginList?type=list'},
                success:function (data) {
                    console.log(data)
                }
            });
        }
    }
})