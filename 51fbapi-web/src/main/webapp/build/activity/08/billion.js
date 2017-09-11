var RMB = 0;
var t = 0;
var startTime = null;
var vm = new Vue({
    el: '#billion',
    data: {
        returnData: [],
        num: '',
        list600: [],
        list700: [],
        list800: [],
        list900: [],
        list1000: [],
        list: {},
        data: null,
        active: 0,
        famen: true,
        timeoutdata: [],
        str: {}
    },
    created: function () {
        let _this = this;
        _this.initial();
        //setInterver(_this.initial(),1000);//五分钟执行一次
        // loading();
        (function () {
            setInterval(_this.initial, 5 * 60 * 1000) //a让initial中的请求五分钟执行一次
        })();
    },
    methods: {
        initial() {
            let _this = this;
            //实时显示小额现金贷总交易额。
            $.ajax({
                url: '/fanbei-web/activity/borrowCashActivities',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    var str = data.data; //获取返回的破十五亿金额
                    var num = JSON.parse(str);
                    console.log(num);
                    var endTime = num.endTime; //活动结束时间戳 1505361600000
                    var nowTime = num.startrTime; //活动开始时间 1505016000000
                    var startrTime = num.currentDate;
                    console.log(endTime);
                    console.log(nowTime);
                    console.log(startrTime);
                   _this.num = num; //转成json字符串
                    //console.log( _this.num);
                    countDown(num);

                    //判断小额现金贷是否为10位
                    if (num.length >= 10) {
                        //隐藏9位数的背景和样式
                        $(".totalMoney").hide();
                        $('.num').hide();
                        //让10位数的背景和样式显示
                        $('.totalMoney1').show();
                        $('.num1').show();

                    } else if (num == 1500000000) { //到达15亿的时候
                        $("#scroll_div").show(); //显示顶部轮播
                    }


                    ScrollImgLeft();
                    //文字轮播
                }


            })

            _this.$nextTick(function () {
                RMB = document.getElementById('RMB').innerHTML;
                //判断开奖金额大于600时显示右侧中奖用户列表
                if (RMB > 600) {
                    $(".winningUser").show();
                    $(".cash").css({
                        'width': '3rem',
                        'float': 'left'
                    });
                } else {
                    $(".winningUser").hide();
                    $(".cash").css({
                        'width': '100%',
                        'float': 'none'
                    });
                }

                //判断进度条的亮度
                if (RMB == 600) {
                    _this.active = 0;
                } else if (RMB == 700) {
                    _this.active = 1;

                } else if (RMB == 800) {
                    _this.active = 2;

                } else if (RMB == 900) {
                    _this.active = 3;

                } else if (RMB == 1000) {
                    _this.active = 4;

                }
                //_this.active = 0;//默认显示第一个

            })

            //十亿中奖用户(每五分钟调一次)
            //判断小额现金贷是否达到十亿
            $.ajax({
                url: '/fanbei-web/activity/getBillionWinUser',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    console.log(data);
                    if (data !== "") {
                        //破十亿时让顶部轮播显示并显示实时中奖用户
                        $("#scroll_div").show();
                    }
                }

            })


        },
        //点击立即借钱
        goBorrowMoney() {
            $.ajax({
                url: '/fanbei-web/tearRiskPacketActivity',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    console.log(data);
                    console.log(data.data.status);
                    if (data.data.loginUrl) {
                        location.href = data.data.loginUrl;
                    } else {
                        // var appVersion = getInfo().appVersion.replace(/\./g, "");
                        var status = data.data.status;
                        var idNumber = data.data.idNumber;
                        var realName = data.data.realName;
                        console.log(status);
                        // alert(status);

                        if (status == 'A1') {
                            window.location.href = '/fanbei-web/opennative?name=DO_SCAN_ID'; // 去扫描身份证、人脸识别
                        } else if (status == 'A2') {
                            window.location.href = '/fanbei-web/opennative?name=DO_BIND_CARD&params={"idNumber":"' + idNumber + '","realName":"' + realName + '"}'; // 去绑定银行卡
                        } else if (status == 'A3' || status == 'A4') {
                            window.location.href = '/fanbei-web/opennative?name=DO_PROMOTE_BASIC'; // 去提升信用基础认证
                        } else if (status == 'B' || status == 'D') {
                            window.location.href = '/fanbei-web/opennative?name=BORROW_MONEY'; // 去借钱、进入借贷超市页面
                        } else if (status == 'C') {
                            window.location.href = '/fanbei-web/opennative?name=DO_PROMOTE_EXTRA'; // 去提升信用补充认证
                        }
                    }
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
        //点击查看更多弹出弹窗
        lists() {
            var t = this;
            $('.mask').show();
            $(".alertRule").show();

            $.ajax({
                url: '/fanbei-web/activity/getWinUser',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    console.log(data);
                    for (var key in data) {
                        var a = 'list' + key;
                        // console.log(JSON.parse(data[key]));
                        t[a] = JSON.parse(data[key]);
                    }
                    //   console.log(600,t.list600);
                    //   console.log(700,t.list700);
                    //   console.log(800,t.list800);
                    //   console.log(900,t.list900);
                    //   console.log(1000,t.list1000);

                }
            })

        },
        //点击遮罩和叉叉隐藏
        noShow() {
            $('.mask').hide();
            $(".alertRule").hide();
        },



    }

});

function ScrollImgLeft() {
    var speed = 50;
    var MyMar = null;
    var scroll_begin = document.getElementById("scroll_begin");
    var scroll_end = document.getElementById("scroll_end");
    var scroll_div = document.getElementById("scroll_div");
    scroll_end.innerHTML = scroll_begin.innerHTML;

    function Marquee() {
        if (scroll_end.offsetWidth - scroll_div.scrollLeft <= 0)
            scroll_div.scrollLeft -= scroll_begin.offsetWidth;
        else
            scroll_div.scrollLeft++;
    }
    MyMar = setInterval(Marquee, speed);
    scroll_div.onmouseover = function () {　　　　　　　
        clearInterval(MyMar);　　　　　
    }
    scroll_div.onmouseout = function () {　　　　　　　
        MyMar = setInterval(Marquee, speed);　　　　　　　　　
    }
}
//倒計時
 /* var num = {
    "amount": 1166942,
    "currentDate": 1504885534518,
    "startrTime": 1505016000000,
    "endTime": 1505361600000
};  */
function countDown(num){
var currentDate, diffTimer, day;
var endTime = num.endTime; //活动结束时间戳 1505361600000
var nowTime = num.startrTime; //活动开始时间 1505016000000
var startrTime = num.currentDate; //后台服务器当前时间 1504884213607
if (startrTime > nowTime) { //当活动还未开始的时候进行判断
    $('#count').html('活动暂未开始')
} else if (nowTime > endTime) {
    $('#count').html('活动结束')
} else {
        var activeDaY = new Date(nowTime).getDate();
        var hour = new Date(nowTime).getHours();
        if (hour > 10 || new Date(startrTime).getDate() == new Date(nowTime).getDate()) {
            activeDaY += 1;
        }
        var year = new Date(nowTime).getFullYear();
        var month = new Date(nowTime).getMonth() + 1;
        var timer = setInterval(function () {
            nowTime += 1000;
            let end = new Date(year + '/' + month + '/' + activeDaY + ' 10:00:00');
            if (activeDaY == new Date(endTime).getDate()) {
                end = new Date(endTime);
            }
            // console.log(year + '/' + month + '/' + activeDaY + ' 10:00:00');
            // console.log(new Date(nowTime));
            t = end.getTime() - nowTime;
            var Hour = Math.floor(t / 1000 / 60 / 60 % 24);
            var Minute = Math.floor(t / 1000 / 60 % 60);
            var Second = Math.floor(t / 1000 % 60);
            document.getElementById("hour").innerHTML = Hour + "时";
            document.getElementById("minute").innerHTML = Minute + "分";
            document.getElementById("second").innerHTML = Second + "秒";
            if (t < 1000) {
                // debugger
                nowTime = end.getTime();
                activeDaY = activeDaY + 1;
            }

            if (nowTime > endTime) {
                document.getElementById("hour").innerHTML = 0 + "时";
                document.getElementById("minute").innerHTML = 0 + "分";
                document.getElementById("second").innerHTML = 0 + "秒";
                clearInterval(timer)
            }
        }, 1000)
    }
}