var vm = new Vue({
    el: '#billion',
    data: {
        returnData: [],
        num: ''
    },
    created: function () {
        let _this = this;
        _this.initial();
        // loading();
    },
    methods: {
        initial() {
            let _this = this;
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

                    var num = document.getElementsByClassName("num"); //获取十五亿的总金额长度
                    if (num.length >= 10) {
                        element.src = "$commonUrl/billion_07.gif"; //当到达十亿金额的时候img替换成另一张图片
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
        list() {
            $('.mask').show();
            $(".alertRule").show();
        },
        //点击遮罩和叉叉隐藏
        noShow() {
            $('.mask').hide();
            $(".alertRule").hide();
        }

    }

});