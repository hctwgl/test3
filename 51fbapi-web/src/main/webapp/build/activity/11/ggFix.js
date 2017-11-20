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
                    //判断优惠券初始化状态
                    self.$nextTick(function () {
                        for(var k=0;k<self.couponCont.boluomeCouponList.length;k++){
                            //console.log(self.couponCont.boluomeCouponList[k].isHas);
                            if(self.couponCont.boluomeCouponList[k].isHas=='Y'){
                                $('.coupon').eq(k).addClass('changeGray');
                                $('.coupon').eq(k).find('.getCoupon').html('已领取');
                            }
                        }
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击领取优惠券
        couponClick:function(index){
            let self = this;
            let sceneId=self.couponCont.boluomeCouponList[index].sceneId;
            $.ajax({
                url: "/h5GgActivity/pickBoluomeCoupon",
                type: "POST",
                dataType: "JSON",
                data: {'sceneId':sceneId},
                success: function(returnData){
                    console.log(returnData);
                    if(returnData.success){
                        if(self.couponCont.boluomeCouponList[index].isHas=='N'){
                            requestMsg(returnData.msg);
                            $('.coupon').eq(index).addClass('changeGray');
                            $('.coupon').eq(index).find('.getCoupon').html('已领取');
                        }else{
                            requestMsg(returnData.msg);
                        }
                    }else{
                        window.location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });

        },
        //点击卡片
        cardClick:function(e){
            let shopId=e.shopId;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrlV1',
                data:{'shopId':shopId},
                dataType:'JSON',
                success: function (returnData) {
                    console.log(returnData);
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
            //点击卡片加埋点
            /*$.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggFix?type=card&typeId='+shopId},
                success:function (data) {
                    console.log(data)
                }
            });*/
        },
        //老用户同享霸王餐
        contOldUserClick(){
            window.location.href='ggOverlord';
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
