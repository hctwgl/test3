// var mySwiper = new Swiper('.swiper-container', {
//     // autoplay: 5000,//可选选项，自动滑动
//     pagination: '.swiper-pagination',
//     paginationType: 'bullets'
// })

let vm = new Vue({
    el: "#bankBox",
    data: {
        allData: [],
        len: 0,
        topLen: 0
    },
    created: function () {
        this.logData();

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
        jump: function (data) {
            location.href = data;
        }
    }
})