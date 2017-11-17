let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;

//获取数据
let vm = new Vue({
    el: '#ggFix',
    data: {
        content: {},
        ruleShow:false,
        couponCont:{},
        firstTitle:'',
        firstValue:'',
        secondTitle:'',
        secondValue:'',
        myRebateMoney:''
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            //初始化数据
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/homePage",
                success: function (data) {
                    //console.log(data);
                    self.content=eval('('+data+')').data;
                    console.log(self.content);
                    /*外卖券+返利金*/
                    self.firstTitle=self.content.resultList[0].name;
                    self.firstValue=self.content.resultList[0].value;
                    self.secondTitle=self.content.resultList[1].name;
                    self.secondValue=self.content.resultList[1].value;
                    /*我的奖励*/
                    if(self.content.totalRebate && self.content.totalRebate!=0){
                        console.log(self.content.totalRebate)
                        self.content.totalRebate=self.content.totalRebate.toString(); //奖励金金额
                        self.myRebateMoney=self.content.totalRebate.split('');
                    }
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
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //优惠券初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/boluomeCoupon",
                success: function (data) {
                    //console.log(data);
                    self.couponCont=eval('('+data+')').data;
                    console.log(self.couponCont);
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击活动规则
        ruleClick(){
            let self=this;
            self.ruleShow=true;
        },
        //点击closeRule关闭mask和规则
        closeClick(){
            let self=this;
            self.ruleShow=false;
        }
    }
});
function step(){
    //方法二
    setTimeout(function(){
        $('.lineBox01').addClass('lineShow01');
        $('.word01').addClass('wordShow01');
        $('.lineBox02').addClass('lineShow02');
        $('.word02').addClass('wordShow02');
        $('.lineBox03').addClass('lineShow03');
        $('.word03').addClass('wordShow03');
        $('.lineBox04').addClass('lineShow04');
        $('.word04').addClass('wordShow04');
    }, 500);
}
step();
