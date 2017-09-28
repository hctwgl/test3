var RMB = 0;
var t = 0;
var startTime = null;
var timerId;
var diff=0;//時間間距
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
        str: {},
        hourData: "",
        minuteData: "",
        secondData: "",
        aNum:"",
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
                    _this.num = num; //转成json字符串
                    //console.log( _this.num);
                    countDown({
                            currentDate: num.currentDate,
                            startrTime: num.startrTime,
                            endTime: num.endTime
                        },
                        _this); //倒计时调用

                    //判断小额现金贷是否为10位
                    var moneyTotal = num.amount;
                    moneyTotal = moneyTotal.replace(/"/g, ""); //替换掉返回字符串中的引号
                    moneyTotal = moneyTotal.replace(/"/g, ""); //替换掉返回字符串中的点
                    var number = moneyTotal.split(""); //将字符串转化成数组
                    /* console.log(moneyTotal);
                    console.log(number); */
                    if (number.length >= 10) {
                        //隐藏9位数的背景和样式
                        $(".totalMoney").hide();
                        $('.num').hide();
                        //让10位数的背景和样式显示
                        $('.totalMoney1').show();
                        $('.num1').show();

                    } else if (number.length >= 1500000000) { //到达15亿的时候
                        $("#scroll_div").show(); //显示顶部轮播
                    }


                    ScrollImgLeft();
                    //文字轮播
                }


            })
            //请求显示右侧列表10个中奖用户
            $.ajax({
                        url: '/fanbei-web/activity/getWinUsers',
                        dataType: 'json',
                        type: 'post',
                        success: function (data) {
                            console.log(2222222222222)
                            console.log(data);
                            _this.timeoutdata=data;

                        }
                })

            //判断显示右侧列表
            _this.$nextTick(function () {
                RMB = document.getElementById('RMB').innerHTML;
                //判断开奖金额大于600时显示右侧中奖用户列表
                if (RMB > 600) {
                    $(".winningUser").show();
                    $(".cash").css({
                        'width': '2.8rem',
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
                    _this.aNum=data;
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

            //把弹框下面一层的滚动事件禁掉
            $("body").css({"overflow":"hidden","height":"100%"});
            $("html").css({"overflow":"hidden","height":"100%"});

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
                   //_this.list=data;

                }
            })

        },
        //点击遮罩和叉叉隐藏
        noShow() {
            $('.mask').hide();
            $(".alertRule").hide();

            //把弹框下面一层的滚动事件禁掉的放出來
            $("body").css("overflow","auto");
            $("html").css("overflow","auto");
        },



    }

});
    //文字轮播
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
    function countDown(num, _this) {
        var nowTime = num.currentDate; //当前后台时间
        var endTime = num.endTime; //活动结束时间
        var startrTime = num.startrTime; //活动开始时间
         console.log(new Date(startrTime), '活动开始时间');
        console.log(new Date(nowTime), '111后天服务器当前时间');
        console.log(new Date(endTime), '活动结束时间'); 
        var diffTimer, day, end;
        var Hour, Minute, Second;
        if (startrTime > nowTime) {
            $('#count').html('活动暂未开始')
        } /* else if (nowTime > endTime) {
            $('#count').html('活动结束');

        } */ else {
            var startDay = new Date(startrTime).getDate();//活动开始日
            var endDay = new Date(endTime).getDate()//活动结束日
            var hour = new Date(nowTime).getHours();//服务器当前时
            var year = new Date(nowTime).getFullYear();//服务器当前年
            var month = new Date(nowTime).getMonth() + 1;//服务器当前月
            var activeDaY = new Date(nowTime).getDate();//服务器当前日
            //倒计时第一天的时候 不管倒计时什么时候开始都要倒计时到第二天要加1
            if (hour > 10 || startDay == activeDaY) {
                activeDaY += 1;
            } 
            //定时器
            clearInterval(timerId);
            timerId = setInterval(function () {
                nowTime += 1000;
                //end = new Date(year + '/' + month + '/' + activeDaY + ' 10:00:00'); //第二天结束时间
                var tim = (year + '/' + month + '/' + activeDaY + ' 11:40:00');
                end = new Date(tim);
                //console.log(end)
                if (activeDaY == endDay) {//后台当前时间和结束时间同一天的时候重新赋值
                    end = new Date(endTime);
                } 
                t = end.getTime() - nowTime;
                Hour = Math.floor(t / 1000 / 60 / 60 % 24);
                Minute = Math.floor(t / 1000 / 60 % 60);
                Second = Math.floor(t / 1000 % 60);
                _this.hourData = Hour;
                _this.minuteData = Minute;
                _this.secondData = Second;
                //每日10点的时候调用中奖名单接口
           /*      var money=document.getElementById("RMB").innerHTML;//获取中奖现金
                //console.log(money);
                 if( _this.hourData==0 &&  _this.minuteData==0 && _this.secondData==0){
                    $.ajax({
                        url: '/fanbei-web/activity/randomUser',
                        dataType: 'json',
                        type: 'post',
                        data:{winAmount:money},
                        success: function (data) {
                            console.log(data);
                           // _this.timeoutdata=data;

                        }
                    })
                }  */
                if (t < 1000) {
                    // debugger
                    nowTime = end.getTime();
                    activeDaY = activeDaY + 1;
                }
                if (nowTime > endTime) {
                    _this.hourData = '00';
                    _this.minuteData = "00";
                    _this.secondData = "00";
                    clearInterval(timerId)
                }
                // console.log(Second)
            }, 1000)
        }


}