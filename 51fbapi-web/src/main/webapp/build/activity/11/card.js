
var userName = getUrl('userName'); //获取用户id
var pageType = getUrl('type');

let vm = new Vue({
    el: "#bankBox",
    data: {
        allData: [],
        len: 0,
        topLen: 0
    },
    created: function () {
        this.logData();
        this.maidian('card', userName , pageType);
    },
    mounted: function () {

    },
    methods: {
        logData: function () { // get 初始化 信息
            let self = this;
            $.ajax({
                url: '/app/activity/getHotBanksInfo',
                type: 'GET',
                // dataType: 'json',
                success: function (data) {
                    data = eval('(' + data + ')');
                    if (data.success) {
                        self.allData = data.data;
                        self.len = data.data.creditbanner.length;
                        self.topLen = data.data.lunbanner.length;
                        self.$nextTick(() => {
                            if (self.topLen > 1) {
                                var mySwiper2 = new Swiper('.bannerSwiper', {
                                    loop: true,
                                    autoplay: 5000,
                                    autoplayDisableOnInteraction: false,
                                    pagination: '.mypagination1', // 如果需要分页器
                                    observer: true, //修改swiper自己或子元素时，自动初始化swiper
                                    observeParents: true, //修改swiper的父元素时，自动初始化swiper,
                                });
                                mySwiper2.update();
                            }


                            var mySwiper = new Swiper('.bankSwiper', {
                                // autoplay: 5000,
                                // autoplayDisableOnInteraction: false,
                                pagination: '.mypagination2', // 如果需要分页器
                                observer: true, //修改swiper自己或子元素时，自动初始化swiper
                                observeParents: true //修改swiper的父元素时，自动初始化swiper
                            });
                            mySwiper.update();


                        })
                    } else {
                        requestMsg("哎呀，出错了！");
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });

        },
        jump: function (url,data,type) {
            this.maidian(type, data, pageType);
            location.href = url;
        },
        maidian(type,bank, type2) {
            //数据统计 
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {
                    maidianInfo: '/fanbei-web/activity/card?type=' + type,
                    maidianInfo1: bank,
                    maidianInfo2: type2,
                },
                success: function (data) {
                    console.log(data)
                }
            });
        }
    }
})