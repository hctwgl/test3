
//获取数据
let vm = new Vue({
    el: '#newUserShare',
    data: {
        content: {},
        ruleShow:false
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/activity/freshmanShareH5/homePage",
                success: function (data) {
                     console.log(data);
                    /*self.content=eval('('+data.data+')');*/
                    self.content=data.data.goodsList;
                     self.$nextTick(function () {
                        /*图片预加载*/
                        $(".first").each(function() {
                            var img = $(this);
                            img.load(function () {
                                $(".loadingMask").fadeOut();
                            });
                            setTimeout(function () {
                                $(".loadingMask").fadeOut();
                            },1000)
                        });
                        $(".loadingMask").fadeOut();
                        $("img.lazy").lazyload({
                            placeholder : "https://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
            //页面初始化加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/newUserShare?type=newUserShare_ini'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击活动详情
        activityDetailClick(){
            let self=this;
            self.ruleShow=true;
            $('.alertRule').animate({'left':'15.4%'},600);
        },
        //点击closeRule关闭mask和规则
        closeClick(){
            let self=this;
            self.ruleShow=false;
            $('.alertRule').animate({'left':'140%'},600);
        },
        //点击立即抢购
        buyNowClick(){
            window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
        }
    }
});


