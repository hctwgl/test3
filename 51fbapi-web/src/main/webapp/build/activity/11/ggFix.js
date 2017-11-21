let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;
let userName = "";//获取用户名
if(getInfo().userName){
    userName=getInfo().userName;
};
let returnNum = getBlatFrom();  // 判断1为Android，2为ios
let typeFrom=getUrl('typeFrom');
//获取数据
let vm = new Vue({
    el: '#ggFix',
    data: {
        content: {},
        ruleShow:false,
        couponCont:{},
        firstTitle:'',
        firstValue:'',
        secondTitle:'',
        secondValue:'',
        myRebateMoney:'',
        alertData:'',
        alertShow:false
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            //初始化数据
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/homePage",
                success: function (data) {
                    //console.log(data);
                    self.content=eval('('+data+')').data;
                    console.log(self.content);
                    /*外卖券+返利金*/
                    self.firstTitle=self.content.resultList[0].name;
                    self.firstValue=self.content.resultList[0].value;
                    self.secondTitle=self.content.resultList[1].name;
                    self.secondValue=self.content.resultList[1].value;
                    /*我的奖励*/
                    if(self.content.totalRebate && self.content.totalRebate!=0){
                        self.content.totalRebate=self.content.totalRebate.toString(); //奖励金金额
                        self.myRebateMoney=self.content.totalRebate.split('');
                        if(self.myRebateMoney.indexOf(".")==-1){ //判断整数
                            return ;
                        }else{  //判断小数
                            self.$nextTick(function () {
                                let pointIndex = self.myRebateMoney.indexOf(".");
                                $('.fanMoneyStyle i').eq(pointIndex).addClass('pointSpecialStyle');
                                $('.fanMoneyStyle i:gt('+pointIndex+')').addClass('decimalSpecialStyle');
                                console.log(pointIndex);
                            });
                        }
                    }
                    /*图片预加载*/
                    self.$nextTick(function () {
                        $(".first").each(function() {
                            var img = $(this);
                            img.load(function () {
                                $(".loadingMask").fadeOut();
                            });
                            setTimeout(function () {
                                $(".loadingMask").fadeOut();
                            },1000)
                        });
                        $(".loadingMask").fadeOut();
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //优惠券初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/boluomeCoupon",
                success: function (data) {
                    //console.log(data);
                    self.couponCont=eval('('+data+')').data;
                    console.log(self.couponCont);
                    //判断优惠券初始化状态
                    self.$nextTick(function () {
                        for(var k=0;k<self.couponCont.boluomeCouponList.length;k++){
                            //console.log(self.couponCont.boluomeCouponList[k].isHas);
                            if(self.couponCont.boluomeCouponList[k].isHas=='Y'){
                                $('.coupon').eq(k).addClass('changeGray');
                                $('.coupon').eq(k).find('.getCoupon').html('已领取');
                            }
                        }
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //弹窗初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/popUp",
                success: function (data) {
                    //console.log(data);
                    self.alertData=data.data;
                    console.log(self.alertData);
                    if((self.alertData.couponToPop && self.alertData.couponToPop=='Y') || (self.alertData.rebateToPop && self.alertData.rebateToPop=='Y')){
                        self.alertShow=true;
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //初始化加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?typeFrom='+typeFrom},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击领取优惠券
        couponClick:function(index){
            let self = this;
            let sceneId=self.couponCont.boluomeCouponList[index].sceneId;
            $.ajax({
                url: "/h5GgActivity/pickBoluomeCoupon",
                type: "POST",
                dataType: "JSON",
                data: {'sceneId':sceneId},
                success: function(returnData){
                    console.log(returnData);
                    if(returnData.success){
                        if(self.couponCont.boluomeCouponList[index].isHas=='N'){
                            requestMsg(returnData.msg);
                            $('.coupon').eq(index).addClass('changeGray');
                            $('.coupon').eq(index).find('.getCoupon').html('已领取');
                        }else{
                            requestMsg(returnData.msg);
                        }
                    }else{
                        window.location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });

        },
        //点击卡片+埋点
        cardClick:function(e){
            let shopId01=e.shopId;
            let cardName=e.name;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrlV1',
                data:{'shopId':shopId01},
                dataType:'JSON',
                success: function (returnData) {
                    console.log(returnData);
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
            //点击卡片加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?typeFrom=cardClick&cardName='+cardName},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击新用户吃霸王餐+埋点
        contNewUserClick(){
            let self=this;
            let shopId02=self.content.waiMaiShopId;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrlV1',
                data:{'shopId':shopId02},
                dataType:'JSON',
                success: function (returnData) {
                    console.log(returnData);
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
            //点击新用户吃霸王餐加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?typeFrom=newUserBtn'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //老用户同享霸王餐+埋点
        contOldUserClick(){
            let self=this;
            let shopId03=self.content.waiMaiShopId;
            window.location.href='ggOverlord?addUiName=SHOWSHARE&shopId03='+shopId03;
            //点击老用户同吃霸王餐加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?typeFrom=oldUserBtn'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //去账户提现+埋点
        toCashClick(){
            if(returnNum==1){ //returnNum==1--安卓；returnNum==2--ios
                window.location.href='/fanbei-web/opennative?name=GG_com.alfl.www.business.ui.CashLoanActivity';
            }else{
                window.location.href='/fanbei-web/opennative?name=GG_ALACashBorrowingViewController';
            }
            //点击去账户提现加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?typeFrom=toCash'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //去'吃玩住行'首页+埋点
        toRebateMoneyClick(){
            if(returnNum==1){ //returnNum==1--安卓；returnNum==2--ios
                window.location.href='/fanbei-web/opennative?name=GG_com.alfl.www.main.ui.MainActivity_1';
            }else{
                window.location.href='/fanbei-web/opennative?name=GG_ALABrandViewController_1';
            }
            //点击去'吃玩住行'首页加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?typeFrom=toGuangGuangBtn'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //领取188元页面scroll到我的场景
        toGetClick(){
            $('html,body').animate({scrollTop: $('.fourthCont').offset().top}, 800);
        },
        //点击活动规则
        ruleClick(){
            let self=this;
            self.ruleShow=true;
        },
        //点击closeRule关闭mask和规则
        closeClick(){
            let self=this;
            self.ruleShow=false;
            $('.toast').hide();
            self.alertShow=false;
        }
    }
});
function step(){
    //方法二
    setTimeout(function(){
        $('.lineBox01').addClass('lineShow01');
        $('.word01').addClass('wordShow01');
        $('.lineBox02').addClass('lineShow02');
        $('.word02').addClass('wordShow02');
        $('.lineBox03').addClass('lineShow03');
        $('.word03').addClass('wordShow03');
        $('.lineBox04').addClass('lineShow04');
        $('.word04').addClass('wordShow04');
    }, 500);
}
step();

// app调用web的分享方法
function alaShareData(){
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "有人@你~你有最高188元惊喜金待领取！",  // 分享的title
        'shareAppContent': "16元外卖1元购，下单即返20元现金（可提现）~",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/11/ggFix41.jpg",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggFixShare?typeFrom=app&typeFromNum=0&userName="+userName,  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggFixShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};

