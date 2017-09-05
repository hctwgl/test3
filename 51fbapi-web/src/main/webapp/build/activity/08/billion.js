var vm = new Vue({
    el: '#billion',
    data: {
        returnData: [],
        num: '',
        list:{}
    },
    created: function () {
        let _this = this;
        _this.initial();
        //setInterver(_this.initial(),1000);//五分钟执行一次
        // loading();
        (function(){setInterval(_this.initial,5*60*1000)})();//a让initial中的请求五分钟执行一次
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
                    str = str.replace(/"/g, ""); //替换掉返回字符串中的引号
                    str = str.replace(/\./g, ""); //替换掉返回字符串中的点
                    _this.num = str.split(""); //将字符串转化成数组
                    console.log(_this.num);
                    var num=_this.num;
                    //判断小额现金贷是否为10位
                    if( num.length>=10){
                            console.log($.compare.length);
                            console.log(111111111111111111111)
                                    //隐藏9位数的背景和样式
                                    $(".totalMoney").hide();
                                    $('..num').hide();
                                    //让10位数的背景和样式显示
                                    $('.totalMoney1').show();
                                    $('.num1').show();
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
                    
                    function GetRTime(){
                        var Day=0;
                        var Hour=0;
                        var Minute=0;
                        var Second=0;
                        var Start=document.getElementById("start");

        //		        var startTime=new Date('2017/09/10 09:26:21');
        //			    var EndTime= new Date('2017/09/11 10:00:00');
                        // var NowTime = new Date();
                        var NowTime = Start;
                        // var EndTime= new Date('2017/09/05 10:00:00');
                        var EndTime= new Date('10:00:00');
                        var t =EndTime.getTime() - NowTime.getTime();
                        if(t>=0){
                            Day=Math.floor(t/1000/60/60/24);
                            Hour=Math.floor(t/1000/60/60%24);
                            Minute=Math.floor(t/1000/60%60);
                            Second=Math.floor(t/1000%60);
                        }else{
                            t+=1000*60*60*24;
                            Day=Math.floor(t/1000/60/60/24);
                            Hour=Math.floor(t/1000/60/60%24);
                            Minute=Math.floor(t/1000/60%60);
                            Second=Math.floor(t/1000%60);
                        }
                         Start=Hour + "时"+Minute + "分"+Second + "秒";
                        /* document.getElementById("day").innerHTML = Day + "天";
                        document.getElementById("hour").innerHTML = Hour + "时";
                        document.getElementById("minute").innerHTML = Minute + "分";
                        document.getElementById("second").innerHTML = Second + "秒"; */
                    }
                    var time1=setInterval(GetRTime,0);
                    GetRTime();
                                
            
                }
                
                            
            })

            //十亿中奖用户(每五分钟调一次)
             //判断小额现金贷是否达到十亿
            $.ajax({
                 url: '/app/activity/getBillionWinUser',
                 dataType: 'json',
                 type: 'post',
                 success: function (data) {
                 console.log(data);
                            if(data!=""){
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
            $('.mask').show();
            $(".alertRule").show();

            $.ajax({
                url: '/app/activity/getWinUser',
                dataType: 'json',
                type: 'post',
                success: function (data) {
                    console.log(data);
                    var list = data;
                    console.log(list);
                    
                }
            })

        },
        //点击遮罩和叉叉隐藏
        noShow() {
            $('.mask').hide();
            $(".alertRule").hide();
        },


        //当开奖金额大于600时显示中奖用户列表
        /* if((".RMB")>600){
            $(".winningUser").show();
            $(".cash").float=="left";
            $(".cash").width=="3rem";
        } */



    }

});