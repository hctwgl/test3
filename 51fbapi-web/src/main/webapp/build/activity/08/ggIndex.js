var activityId=getUrl("activityId");//获取活动Id
var userName = "";//获取用户名
if(getInfo().userName){
    userName=getInfo().userName;
};
var num;//卡片数量
var protocol = window.location.protocol;
var host = window.location.host;
var domainName = protocol+'//'+host;
//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {},
        finalPrizeMask:false
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GG/initHomePage",
                data:{'activityId':activityId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    console.log(typeof(self.content.boluomeCouponList[0]));
                    self.$nextTick(function () {
                        var cont = $(".cont1").html();
                        $(".cont2").html(cont);
                        wordMove();//左右移动动画
                    })
                    self.$nextTick(function () {
                        self.imgSwiper();//首页轮播
                        //判断蒙版
                        for(var j=0;j<self.content.itemsList.length;j++){//是否可赠送
                            num=self.content.itemsList[j].num;
                            if(num>=2){
                                $('.card').eq(j).find('.num').css('display','block');
                                $('.presentCard').attr('present','Y');
                            }
                        }
                        for(var j=0;j<self.content.itemsList.length;j++){//是否可领取终极大奖
                            num=self.content.itemsList[j].num;
                            if(num==0){
                                self.finalPrizeMask=true;
                                break;
                            }
                        }

                    })
                }
            })
        },
        //轮播
        imgSwiper(){
            let mySwiper = new Swiper ('.banner', {
                loop: true,
                speed:1000,
                autoplay :2000,
                autoplayDisableOnInteraction : false
            });
        },
        //点击参与人数进入排行榜
        joinAmountClick:function(){
            window.location.href='ggrankingList?activityId='+activityId;
        },
        //点击轮播图
        bannerClick:function(e){
            var shopId=e.value2;
            //console.log(shopId)
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrlV1',
                data:{'shopId':shopId},
                success: function (returnData) {
                    console.log(returnData)
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                dataType:'JSON',
                error: function(){
                    requestMsg("请求失败");
                }
            })
        },
        //点击优惠券
        couponClick:function(e){
            var sceneId=e.sceneId;
            $.ajax({
                url: "/fanbei-web/pickBoluomeCouponV1",
                type: "POST",
                dataType: "JSON",
                data: {'sceneId':sceneId},
                success: function(returnData){
                    if(returnData.success){
                        requestMsg(returnData.msg);
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
            var shopId=e.refId;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrlV1',
                data:{'shopId':shopId},
                dataType:'JSON',
                success: function (returnData) {
                    //console.log(returnData)
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            })
        },
        //点击获取终极大奖
        finalPrize:function(){
            let self = this;
            if(!self.finalPrizeMask){
                $.ajax({
                    type: 'get',
                    url: '/H5GG/pickUpSuperPrize',
                    data:{'activityId':activityId},
                    dataType:'JSON',
                    success: function (returnData) {
                        console.log(returnData)
                        if(returnData.success){
                            $('.presentCard').attr('present','');
                            $('.mask').css('display','block');
                            $('.alertFinalPrize').css('display','block');
                            for(var j=0;j<self.content.itemsList.length;j++){
                                num=self.content.itemsList[j].num;
                                if(num==0){
                                    return ""
                                }else{
                                    $('.card').eq(j).find('.num').html('x'+(num-1));
                                    if(num-1==0){
                                        $('.card').eq(j).find('.gray').css('display','block');
                                        $('.card').eq(j).find('.num').css('display','none');
                                        self.finalPrizeMask=true;
                                    }else if(num-1==1){
                                        $('.card').eq(j).find('.num').css('display','none');
                                    }else if(num-1>=2){
                                        $('.presentCard').attr('present','Y');
                                    }
                                }
                            }
                        }else{
                            requestMsg(returnData.msg);
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                })
            }

        },
        //点击获取活动规则
        ruleClick:function(){
            $('.alertRule').css('display','block');
            $('.mask').css('display','block');
        },
        //点击关闭弹窗
        close:function(){
            $('.alertPresent').css('display','none');
            $('.mask').css('display','none');
            $('.title').css('display','none');
            $('.alertRule').css('display','none');
            $('.mask').css('display','none');
            $('.alertFinalPrize').css('display','none');
        },
        fixImgUrl:function(i){
            return "http://f.51fanbei.com/h5/app/activity/08/gg000"+i+".png";
        }
    }
})
//首页顶部栏动画-------------------------
var speed = 20;
function wordMove(){
    var left = $(".personAmount").scrollLeft();
    if(left >= $(".cont1").width()){
        left = 0;
    }else{
        left++;
    }
    $(".personAmount").scrollLeft(left);
    setTimeout("wordMove()",speed);
}
// app调用web的方法
function alaShareData(){
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "全民集卡片 领取51元大奖",  // 分享的title
        'shareAppContent': "我正在51返呗收集卡片，集齐可领取51元红包！消费有返利又可以赚钱，快来参加啦~",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/08/gg31.png",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggIndexShare?loginSource=F&activityId="+activityId+"&userName="+userName+"&sharePage=ggIndexShare",  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggIndexShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};

