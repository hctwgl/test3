//活动日历显示
/*判断时间list*/ // 根据时间判断导航栏高亮
let currentStarmp = new Date().getTime();
let oneTime = Date.parse(new Date('2017/11/1 00:00:00'));
let twoTime = Date.parse(new Date('2017/11/9 00:00:00'));
let threeTime = Date.parse(new Date('2017/11/12 00:00:00'));
let fourTime = Date.parse(new Date('2017/11/12 00:00:00'));
let fiveTime = Date.parse(new Date('2017/11/11 00:00:00'))
let sixTime = Date.parse(new Date('2017/11/13 00:00:00'))
let sevenTime = Date.parse(new Date('2017/11/10 00:00:00'))
// console.log(new Date(currentStarmp))
if (currentStarmp < oneTime) { //11.1号之前
    addStyle(0);
}
if (currentStarmp > oneTime && currentStarmp < twoTime) { //11.1-11.8
    addStyle(0);
}
if (currentStarmp >= twoTime && currentStarmp <= sevenTime) { //11.9-11.10
    addStyle(1);
}
if (currentStarmp >= fiveTime && currentStarmp < threeTime) { //11.11
    addStyle(2);
}
if (currentStarmp > sixTime) { //11.13
    addStyle(3);
}

function addStyle(i) {
    $('.active').eq(i).addClass('active01');
    $('.active').eq(i).siblings().removeClass('active01');
    /*  $('.time').eq(i).find('span').addClass('active02');
     $('.time').eq(i).siblings().find('span').removeClass('active02'); */
    $('.active').eq(i).find('.tangle').addClass('tangleOne') //添加三角
    $('.active').eq(i).find('.tangle').siblings().removeClass('tangleOne') //移除三角
    $('.active').find('.tangleTwo').eq(i).hide(); //隐藏显示的三角图片
    $('.active').find('.tangleTwo').eq(i).siblings().show();

}



var groupId = getUrl("groupId"); //获取活动Id
var modelId = getUrl("modelId"); //获取模板Id
var imgrooturl = "https://f.51fanbei.com/h5/app/activity/11";

let vm = new Vue({
    el: '#doubleEleven',
    data: {
        content: '',
        isShow: true,
        m: '',
        c: '',
        tab: 1,
        allStartTime: '',
        productList: '',
        productListDetail: '',
        allData: [{
                'name': '苹果',
                'img': imgrooturl + '/brand-01.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=187'
            },
            {
                'name': 'vivo/OPPO',
                'img': imgrooturl + '/brand-02.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=186'
            },
            {
                'name': '韩都衣舍',
                'img': imgrooturl + '/brand-09.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=185'
            },
            {
                'name': 'DW',
                'img': imgrooturl + '/brand-16.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=183'
            },
            {
                'name': 'nike',
                'img': imgrooturl + '/brand-11.png',
                'src': ''
            },
            {
                'name': 'addidas',
                'img': imgrooturl + '/brand-12.png',
                'src': ''
            },
            {
                'name': 'gxg',
                'img': imgrooturl + '/brand-25.png',
                'src': ''
            },
            {
                'name': '兰蔻',
                'img': imgrooturl + '/brand-24.png',
                'src': ''
            },
            {
                'name': '华为',
                'img': imgrooturl + '/brand-03.png',
                'src': ''
            },
            {
                'name': '小米',
                'img': imgrooturl + '/brand-04.png',
                'src': ''
            },
            {
                'name': '天梭',
                'img': imgrooturl + '/brand-29.png',
                'src': ''
            },
            {
                'name': 'lilbetter',
                'img': imgrooturl + '/brand-19.png',
                'src': ''
            },
            {
                'name': '乐町',
                'img': imgrooturl + '/brand-10.png',
                'src': ''
            },
            {
                'name': '欧莱雅',
                'img': imgrooturl + '/brand-29.png',
                'src': ''
            },
            {
                'name': '马克华菲',
                'img': imgrooturl + '/brand-05.png',
                'src': ''
            },
            {
                'name': '李宁',
                'img': imgrooturl + '/brand-27.png',
                'src': ''
            },
            {
                'name': 'CK',
                'img': imgrooturl + '/brand-26.png',
                'src': ''
            },
            {
                'name': 'newbalance',
                'img': imgrooturl + '/brand-13.png',
                'src': ''
            },
            {
                'name': '拉夏贝尔',
                'img': imgrooturl + '/brand-08.png',
                'src': ''
            },
            {
                'name': 'Dickies',
                'img': imgrooturl + '/brand-28.png',
                'src': ''
            },
            {
                'name': 'Dior',
                'img': imgrooturl + '/brand-17.png',
                'src': ''
            },
            {
                'name': '衣香丽影',
                'img': imgrooturl + '/brand-21.png',
                'src': ''
            },
            {
                'name': '宾卡达',
                'img': imgrooturl + '/brand-15.png',
                'src': ''
            },
            {
                'name': '鸿星尔克',
                'img': imgrooturl + '/brand-14.png',
                'src': ''
            }
        ], //返回回来的数据
        arr: [
            [{
                'name': '苹果',
                'img': imgrooturl + '/brand-01.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=187'
            },
            {
                'name': 'vivo/OPPO',
                'img': imgrooturl + '/brand-02.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=186'
            },
            {
                'name': '韩都衣舍',
                'img': imgrooturl + '/brand-09.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=185'
            },
            {
                'name': 'DW',
                'img': imgrooturl + '/brand-16.png',
                'src': 'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=183'
            },
            {
                'name': 'nike',
                'img': imgrooturl + '/brand-11.png',
                'src': ''
            },
            {
                'name': 'addidas',
                'img': imgrooturl + '/brand-12.png',
                'src': ''
            },
            {
                'name': 'gxg',
                'img': imgrooturl + '/brand-25.png',
                'src': ''
            },
            {
                'name': '兰蔻',
                'img': imgrooturl + '/brand-24.png',
                'src': ''
            }],
            [{
                'name': '华为',
                'img': imgrooturl + '/brand-03.png',
                'src': ''
            },
            {
                'name': '小米',
                'img': imgrooturl + '/brand-04.png',
                'src': ''
            },
            {
                'name': '天梭',
                'img': imgrooturl + '/brand-29.png',
                'src': ''
            },
            {
                'name': 'lilbetter',
                'img': imgrooturl + '/brand-19.png',
                'src': ''
            },
            {
                'name': '乐町',
                'img': imgrooturl + '/brand-10.png',
                'src': ''
            },
            {
                'name': '欧莱雅',
                'img': imgrooturl + '/brand-29.png',
                'src': ''
            },
            {
                'name': '马克华菲',
                'img': imgrooturl + '/brand-05.png',
                'src': ''
            },
            {
                'name': '李宁',
                'img': imgrooturl + '/brand-27.png',
                'src': ''
            },
            ]
            ,[{
                'name': 'CK',
                'img': imgrooturl + '/brand-26.png',
                'src': ''
            },{
                'name': 'newbalance',
                'img': imgrooturl + '/brand-13.png',
                'src': ''
            },
            {
                'name': '拉夏贝尔',
                'img': imgrooturl + '/brand-08.png',
                'src': ''
            },
            {
                'name': 'Dickies',
                'img': imgrooturl + '/brand-28.png',
                'src': ''
            },
            {
                'name': 'Dior',
                'img': imgrooturl + '/brand-17.png',
                'src': ''
            },
            {
                'name': '衣香丽影',
                'img': imgrooturl + '/brand-21.png',
                'src': ''
            },
            {
                'name': '宾卡达',
                'img': imgrooturl + '/brand-15.png',
                'src': ''
            },
            {
                'name': '鸿星尔克',
                'img': imgrooturl + '/brand-14.png',
                'src': ''
            }]
        ], //就是把数据转成二维数组。一维是swiper-slide的个数。二维是每个swiper-slide的img的个数和数据，最后转成arr[['图片1','图片2','图片3','图片4','图片5','图片6','图片7','图片8'],[,'图片9','图片10','图片11','图片12','图片13','图片14','图片15','图片16'],['图片17','图片18']]
    },
    created: function () {
        this.logData();
        this.coupon();
        this.countDown();
        /* var arr = new Array([]);
        for (var i = 0; i <= Math.floor(this.allData.length / 8); i++) {
            arr[i] = [];

            arr[i] = this.allData.slice(i * 8, i * 8 + 8);
        }

        this.arr = arr;
        console.log(arr);
        console.log( this.arr.length ); */
    },
    mounted: function () {
        var mySwiper = new Swiper('.swiper-container', {
            loop: true,
            // 如果需要分页器
            pagination: '.swiper-pagination',
            autoplay : 4000,

            // 如果需要前进后退按钮
            nextButton: '.swiper-button-next',
            prevButton: '.swiper-button-prev',

        });
        mySwiper.update()

    },
    methods: {
        //页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo",
                data: {
                    'modelId': modelId
                },
                success: function (data) {
                    //  console.log(JSON.parse(data));
                    var a = eval('(' + data + ')')
                    //  console.log(a);
                    self.productList = a.data.activityList;
                    console.log(self.productList, 'self.productList')

                },
                error: function () {
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        //点击商品
        goodClick(p) {
            if (p.source == "SELFSUPPORT") {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + p.goodsId + '"}';
            } else {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"' + p.goodsId + '"}';
            }
        },
        //优惠券初始化信息
        coupon() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/activityCouponInfo",
                data: {
                    'groupId': groupId
                },
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    self.m = self.content.couponInfoList;
                    self.c = JSON.stringify(self.m);
                    self.m = JSON.parse(self.c);

                    // console.log(self.content);
                    console.log(self.m, 'self.m');
                }
            })
        },
        //点击领券
        couponClick: function (item) {
            let self = this;
            let couponId = item.couponId;
            // alert(couponId)
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    // alert(returnData);
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
        //倒计时初始化接口
        countDown() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/activity/de/endtime",
                success: function (data) {
                    console.log(data);
                    self.allStartTime = data.data.currentTime;
                    console.log(self.allStartTime, 'self.allStartTime');



                    //顶部倒计时和第一次全民砍价倒计时
                    //活动开始时间戳
                    let startTime = new Date("Nov 1,2017 00:00:00");
                    let startStamp = startTime.valueOf();
                    // 结束时间的时间戳
                    let endDate = new Date("Nov 12,2017 00:00:00");
                    // let endDate = new Date("oct 26,2017 00:00:00");
                    let endStamp = endDate.valueOf();
                    // 获取当前时间的时间戳
                    // let now = new Date();
                    let now = self.allStartTime;
                    let nowTimeStamp = now.valueOf();
                    // 相差的时间戳
                    let differStamp = endStamp - nowTimeStamp;
                    let intDiff = parseInt(differStamp / 1000); //倒计时总秒数量
                    let timer12

                    function timer(intDiff) {
                        showTimerS(intDiff);
                        intDiff--;
                        timer12 = setInterval(function () {
                            showTimerS(intDiff);
                            intDiff--;
                        }, 1000);
                    };
                    timer(intDiff);

                    function showTimerS(diff) {
                        var day = 0,
                            hour = 0,
                            minute = 0,
                            second = 0; //时间默认值

                        if (diff > 0) {
                            day = Math.floor(diff / (60 * 60 * 24));
                            hour = Math.floor(diff / (60 * 60)) - (day * 24);
                            minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
                            second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);

                            hour = hour < 10 ? '0' + hour : hour
                            minute = minute < 10 ? '0' + minute : minute
                            second = second < 10 ? '0' + second : second
                        }

                        $('.countTwo').html(day + "天" + " : " + hour + '时' + " : " + minute + '分' + " : " + second + '秒'); //全民倒计时
                        //顶部倒计时
                        $('.blankOne').html(day);
                        $('.blankTwo').html(hour);
                        $('.blankThree').html(minute);
                        $('.blankFour').html(second);
                        //活动开始前显示距离活动开始的时间 点击无跳转
                        //活动中跳转红包雨活动主页 
                        if (nowTimeStamp >= startStamp) {
                            $('.bargain').click(function () {
                                //  window.location.href = 'http://www.baidu.com';
                                window.location.href = 'barginIndex?double=barginOne'; //跳转砍价连接
                            })
                        }
                        //活动结束后显示活动已结束  点击无跳转
                        if (day == 0 && hour == 0 && minute == 0 && second == 0) {
                            $('.black-blank').html('00'); //顶部定时器活动结束后 显示00:00:00
                            $('.countTwo').html('活动已结束');
                            $('.bargain').unbind("click"); //禁止点击事件
                            clearInterval(timer12); //清除定时器

                        }

                    };



                    //第二次红包雨倒计时
                    //活动开始时间戳
                    let begainTime = new Date("nov 1,2017 00:00:00");
                    let begainS = begainTime.valueOf();
                    // 结束时间的时间戳
                    let overDate = new Date("nov 9,2017 00:00:00");
                    // let overDate = new Date("oct 27,2017 00:00:00");
                    let endOver = overDate.valueOf();
                    // 获取当前时间的时间戳
                    // let nowTime = new Date();
                    let nowTime = self.allStartTime;
                    let nowTimeS = nowTime.valueOf();
                    console.log(nowTimeS);
                    // 相差的时间戳
                    let differS = endOver - nowTimeS;
                    let diffValue = parseInt(differS / 1000); //倒计时总秒数量
                    let timer13

                    function timeS(vadiffValuel) {
                        showTime(diffValue);
                        diffValue--;
                        timer13 = setInterval(function () {
                            showTime(diffValue);
                            diffValue--;
                        }, 1000);
                    };
                    timeS(diffValue);

                    function showTime(ctime) {
                        // console.log(ctime, 'ctime')s
                        let time = 'time' + ctime
                        var day = 0,
                            hour = 0,
                            minute = 0,
                            second = 0; //时间默认值

                        if (ctime > 0) {
                            day = Math.floor(ctime / (60 * 60 * 24));
                            hour = Math.floor(ctime / (60 * 60)) - (day * 24);
                            minute = Math.floor(ctime / 60) - (day * 24 * 60) - (hour * 60);
                            second = Math.floor(ctime) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);

                            hour = hour < 10 ? '0' + hour : hour
                            minute = minute < 10 ? '0' + minute : minute
                            second = second < 10 ? '0' + second : second
                        }
                        $('.countThree').html(day + "天" + " : " + hour + '时' + " : " + minute + '分' + " : " + second + '秒'); //红包雨
                        //判断活动时间 活动开始前显示倒计时时间 
                        //活动中跳转红包雨活动主页
                        if (nowTimeS >= begainS) {
                            $('.redRain').click(function () {
                                window.location.href = 'redrain?double=redOne'; //跳转红包雨连接
                            })
                        }
                        // 活动结束后显示活动已结束  点击无跳转
                        if (day == 0 && hour == 0 && minute == 0 && second == 0) {
                            // $('.countThree').html('活动已结束');
                            // $('.redRain').unbind("click");//禁止点击事件
                            clearInterval(timer13); //清除定时器
                        }

                    };



                    //9场倒计时
                    // let myTime = Date.parse('2017/11/09 00:00:00') - data.data.currentTime
                    function afterpre(nowdate) {
                        var activityArray = [
                            "2017/11/09 10:00:00",
                            "2017/11/09 14:00:00",
                            "2017/11/09 20:00:00",
                            "2017/11/10 10:00:00",
                            "2017/11/10 14:00:00",
                            "2017/11/10 20:00:00",
                            "2017/11/11 10:00:00",
                            "2017/11/11 14:00:00",
                            "2017/11/11 20:00:00"
                        ]

                        var stampArray = activityArray.map((a, b) => {
                            return Date.parse(a)
                        })

                        var neededtime
                        for (let i = 0; i < stampArray.length; i++) {
                            console.log('1')
                            if (stampArray[i] > nowdate) {
                                neededtime = stampArray[i]
                                break
                            }
                        }
                        var esctime = neededtime - nowdate;//时间戳差

                        var timer = setInterval(() => {
                            esctime -= 1000
                            let getDate = new Date(esctime)
                            let hour = getDate.getHours()
                            let minute = getDate.getMinutes()
                            let second = getDate.getSeconds()

                            if (hour >= 8) {
                                hour = hour - 8
                            } else {
                                hour = hour + 16
                            }

                            hour = hour < 10 ? '0' + hour : hour
                            minute = minute < 10 ? '0' + minute : minute
                            second = second < 10 ? '0' + second : second

                            $('.countThree').text(`${hour}时：${minute}分：${second}秒`)
                        }, 1000)
                    }

            
                    if(self.allStartTime >= Date.parse("2017/11/09 00:00:00")) {
                        afterpre(self.allStartTime)
                    }
                    if(self.allStartTime>Date.parse("2017/11/12 00:00:00")){
                        $('.countThree').html('活动已结束');
                        $('.redRain').unbind("click");//禁止点击事件
                         clearInterval(timer); //清除定时器
                    }

                }
            })
        },
        //点击显示顶部活动日历
        noShow() {
            this.isShow = !this.isShow;
            if (this.isShow) {
                $('.processBar').show();
            } else {
                $('.processBar').hide();
            }
        },
        //点击tab栏切换
        tabClick(i) {
            this.tab = i + 1;


        },
        //点击tab栏箭头
        tabClickTwo() {
            if (this.tab < this.productList.length) {
                ++this.tab;
            }
        }
    }
})