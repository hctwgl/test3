var RMB = 0;
var t = 0;
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
        timeoutdata:[]
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
                url: '/app/activity/borrowCashActivities',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    console.log(data);
                    var str = data.data; //获取返回的破十五亿金额
                    console.log(str);
                    // str = str.replace(/"/g, ""); //替换掉返回字符串中的引号
                    // str = str.replace(/\./g, ""); //替换掉返回字符串中的点
                    // _this.num = str.split(""); //将字符串转化成数组
                    _this.num = JSON.parse(str);
                    console.log(_this.num);
                    var num = _this.num;
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

                    //开奖倒计时
                    //第一次开奖前没有开奖名单 只是显示第一次开奖倒计时
                    //第一次开奖后 左侧显示下一次倒计时 右侧显示最新的开奖结果
                    //从上线开始时间开始倒计时 一直到次日上午10点开始
                    var Day = 0;
                    var Hour = 0;
                    var Minute = 0;
                    var Second = 0;
                    var Start = document.getElementById("start");

                    //var startTime=new Date('2017/09/10 09:26:21');
                    function GetNextDate(time) {
                        //获取当前时间年月日
                        var y = time.getFullYear();
                        var m = time.getMonth() + 1;
                        var d = time.getDate();
                        var t = y + "-" + m + "-" + d + " " + "10:00:00";
                        // var t = y + "-" + m + "-" + d + " " + "23:05:00";
                        var tDate = new Date(Date.parse(t.replace(/-/g, "/")));
                        tDate = +tDate + 24 * 60 * 60 * 1000;
                        tDate = new Date(tDate);
                        return tDate;
                    }

                    function GetRTime() {
                        //var EndTime= new Date('2017/09/11 10:00:00');
                        var NowTime = new Date();
                        var EndTime = GetNextDate(NowTime);

                        t = EndTime.getTime() - NowTime.getTime();
                        if (t >= 0) {
                            Day = Math.floor(t / 1000 / 60 / 60 / 24);
                            Hour = Math.floor(t / 1000 / 60 / 60 % 24);
                            Minute = Math.floor(t / 1000 / 60 % 60);
                            Second = Math.floor(t / 1000 % 60);
                        } else {
                            t += 1000 * 60 * 60 * 24;
                            Day = Math.floor(t / 1000 / 60 / 60 / 24);
                            Hour = Math.floor(t / 1000 / 60 / 60 % 24);
                            Minute = Math.floor(t / 1000 / 60 % 60);
                            Second = Math.floor(t / 1000 % 60);
                        }
                        // document.getElementById("day").innerHTML = Day + "天";
                        document.getElementById("hour").innerHTML = Hour + "时";
                        document.getElementById("minute").innerHTML = Minute + "分";
                        document.getElementById("second").innerHTML = Second + "秒";
                        if (Hour == 0 & Minute == 0 & Second == 0&_this.famen ) { //当倒计时为0时   这里已经做过判断 且再异步下实时判断才行 下面判断只判断一次所以没用
                            _this.famen = false;
                            clearTimeout(time1)
                           setTimeout(function(){
                                _this.famen = true;
                           },1000)//设置定时器干嘛
                            RMB = document.getElementById('RMB').innerHTML;
                            console.log(RMB);
                            $.ajax({
                                url: '/app/activity/randomUser',
                                dataType: 'json',
                                type: 'post',
                                data: {
                                    winAmount: RMB //将开奖金额传给后台
                                },
                                success: function (data) {
                                    console.log(data)
                                    _this.timeoutdata=data;
                                    // var detail = JSON.parse(str);
                                    // console.log(detail);
                                    
                                }
                            })
                             _this.initial();//
                        }


                    }
                    var time1 = setInterval(GetRTime, 0);
                    //clearTimeout(time1);
                    //倒计时为0的时候去请求接口
                    if (_this.famen) {

                    }

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
                url: '/app/activity/getBillionWinUser',
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
                        var appVersion = getInfo().appVersion.replace(/\./g, "");
                        var status = data.data.status;
                        var idNumber = data.data.idNumber;
                        var realName = data.data.realName;

                        if (appVersion <= "374") {
                            window.location.href = '/fanbei-web/opennative?name=BORROW_MONEY'; // 老版本全部跳借钱
                        } else {
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
                url: '/app/activity/getWinUser',
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