/*判断时间list*/ // 根据时间判断导航栏高亮
function time() {
    let currentStarmp = new Date().getTime();
    let oneTime = Date.parse(new Date('2017/10/19 00:00:00'));
    let twoTime = Date.parse(new Date('2017/10/25 00:00:00'));
    let threeTime = Date.parse(new Date('2017/10/26 00:00:00'));
    let fourTime = Date.parse(new Date('2017/10/27 00:00:00'));
    let fiveTime = Date.parse(new Date('2017/11/3 00:00:00'));
    let sixTime = Date.parse(new Date('2017/10/25 00:00:00'));
    console.log(currentStarmp);
    console.log(fourTime);

        console.log(2)

    if (currentStarmp >= oneTime) { //日期为10.18时
        addStyle(0);
    }
    if (currentStarmp >= twoTime && currentStarmp < fourTime) { //日期为10.25-10.26 显示领劵按钮
        addStyle(1);
        $('.rightNow').css('background-color', '#7430e8');
    }
    if (currentStarmp >= fourTime) { //日期为10.27 且27预约按钮变成立即购买 并且领劵模块隐藏
        addStyle(2);
        console.log($('.buyNow').show())
        console.log(document.getElementsByClassName('coupon'))
        $('.btn').text('立即购买')
     
        // $('.btn').hide();
        $('.coupon').hide(); //领劵模块进行隐藏
    }
    if (currentStarmp >= fiveTime) { //日期为11.3
        addStyle(3);
    }
    if (currentStarmp >= oneTime && currentStarmp <= sixTime) { //10.18-10.24领劵按钮置灰 并且不可点击
        $('.rightNow').css('background-color', '#d2d2d2'); //领劵按钮置灰
        // $('.rightNow').removeAttr('onclick'); //去掉标签中的onclick事件
        this.flag = false

    }

    function addStyle(i) {
        $('.time').eq(i).addClass('active01');
        $('.time').eq(i).siblings().removeClass('active01');
        $('.time').eq(i).find('span').addClass('active02');
        $('.time').eq(i).siblings().find('span').removeClass('active02');
    }
}

//title
var title = decodeURI(getUrl('title'));
//console.logconsole.log(title)
document.title = title;
var modelId = getUrl("modelId"); //获取活动Id
var protocol = window.location.protocol;
var host = window.location.host;
var domainName = protocol + '//' + host; //获取域名

// 获取数据
let vm = new Vue({
    el: '.bigBox',
    data: {
        content: {},
        prizeCont: {},
        orderStatus: '',
        loginStatus: '',
        ruleShow: '',
        couponCount: '',
        getStatus80: '',
        getStatus100: '',
        getStatus150: '',
        couponobj: {
            status80: 80,
            status100: 100,
            status150: 150,
        },
        flag: true
    },
    created: function () {
        // this.logData();
        // this.loginPrize()
    },
    mounted() {
        this.logData();
        this.loginPrize()
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/partActivityInfo',
                data: {
                    'modelId': modelId
                },
                success: function (data) {
                    self.content = eval('(' + data + ')').data.activityList.slice(0, 3);
                    //console.log(eval('(' + data + ')'));
                    self.content.firstList = self.content[0].activityGoodsList.slice(0, 1);
                    self.content.secondList = self.content[1].activityGoodsList;
                    self.content.thirdList = self.content[2].activityGoodsList;
                    console.log(self.content);
                    self.$nextTick(function () {
                        time()
                        $(".loadingMask").fadeOut();
                        $("img.lazy").lazyload({
                            placeholder: "http://f.51fanbei.com/h5/common/images/bitmap1.png", //用图片提前占位
                            effect: "fadeIn", // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                        time.call(self)
                    })

                },
                error: function () {
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        //优惠券初始化数据
        loginPrize() {
            let self = this;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/activity/getActivityGood',
                success: function (data) {
                    data = eval('(' + data + ')');
                    // console.log(000000);
                    console.log(data);
                    self.orderStatus = data.status;
                    self.loginStatus = data.loginStatus;
                    self.couponCount = data.couponCount; //预约成功人数
                    self.getStatus80 = data.getStatus80 //80优惠券
                    self.getStatus100 = data.getStatus100 //100代金券
                    self.getStatus150 = data.getStatus150 //150代金券
                    self.loginStatus = data.loginStatus //登录状态

                    // 判断优惠券领取状态
                    if (self.getStatus80 == 'Y') {
                        $('.rightNow').eq(0).addClass('gray')
                        $('.rightNow').eq(0).html("已经领取");
                    }
                    if (self.getStatus100 == 'Y') {
                        $('.rightNow').eq(1).addClass('gray')
                        $('.rightNow').eq(1).html("已经领取");
                    }
                    if (self.getStatus150 == 'Y') {
                        $('.rightNow').eq(2).addClass('gray')
                        $('.rightNow').eq(2).html("已经领取");
                    }

                },
                error: function () {
                    requestMsg('哎呀，出错了！');
                }
            })
        },
        // 点击领取优惠券 并请求数据
        clickCoupon(e) {

            function getstatus(nodecl) {
                if (nodecl.indexOf('status80') > -1) {
                    return 'status80'
                } else if (nodecl.indexOf('status100') > -1) {
                    return 'status100'
                } else if (nodecl.indexOf('status150') > -1) {
                    return 'status150'
                }
            }

            let nodecl = e.target.className;
            let self = this;
            var couponAmount = document.getElementsByClassName('money');
            var couponArray = Array.prototype.slice.call(couponAmount); //转换伪数组
            console.log(self.couponobj, getstatus(nodecl), nodecl)

            $.ajax({
                type: 'post',
                url: '/fanbei-web/activity/getReservationCoupons',
                data: {
                    couponAmount: self.couponobj[getstatus(nodecl)]
                },
                success: function (data) {
                    console.log(data);
                    console.log(nodecl);
                    //弹窗
                    if (data.success) {
                        requestMsg(JSON.parse(data).msg);
                        self.loginPrize()
                    } else {
                        requestMsg(JSON.parse(data).msg);
                        console.log(self.loginPrize)
                        self.loginPrize()
                    }

                    if (self.loginStatus == "N") { //判断登录状态
                        window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
                    }


                },
                error: function () {
                    requestMsg('哎呀，出错了！');
                }
            })
        },
         //点击预约与商品
        orderAndGoodClick(){
                // alert(12222222222);
                window.location.href='iphonexRegister?channelCode=YiPhonex&pointCode=Yiphonex';
         },
        // 点击活动规则
        ruleClick() {
            let self = this;
            self.ruleShow = 'Y';
        },
        //点击蒙版
        maskClick() {
            let self = this;
            self.ruleShow = '';
        }
    }
})
// app调用web的方法
function alaShareData() {
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "iPhone X 预约立减100", // 分享的title
        'shareAppContent': "十周年 翘首以待！1元预约立减100，每日限抽取5名成功分享用户获iPhone X大奖，限时秒杀iPhone 6 仅1999元！", // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/09/iphone8_06.jpg", // 分享右边小图
        "shareAppUrl": domainName + "/fanbei-web/activity/iphoneXShare?modelId=" + modelId + "&title=" , // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "iphoneXShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj); // obj对象转换成json对象
    return dataStr;
};


