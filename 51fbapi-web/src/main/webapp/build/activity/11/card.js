// var mySwiper = new Swiper('.swiper-container', {
//     // autoplay: 5000,//可选选项，自动滑动
//     pagination: '.swiper-pagination',
//     paginationType: 'bullets'
// })

let vm = new Vue({
    el: "#bankBox",
    data: {
        allData: [],
        len: 0
    },
    created: function () {
        this.logData();
        
    },
    mounted: function () {
        this.$nextTick(() => {
            var mySwiper = new Swiper('.swiper-container', {
                pagination: '.swiper-pagination', // 如果需要分页器
                observer: true,//修改swiper自己或子元素时，自动初始化swiper
                observeParents: true//修改swiper的父元素时，自动初始化swiper
            });
            mySwiper.update()
        })
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
                    console.log(data.success)
                    if (data.success) {
                        self.allData = data.data;
                        self.len = data.data.creditbanner.length;
                        console.log(">>>>>>>>>>")
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
            location.href= data;
        }
    }
})