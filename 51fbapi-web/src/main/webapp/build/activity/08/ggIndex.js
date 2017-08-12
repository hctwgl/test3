var activityId=getUrl("activityId");//获取活动Id
var userName = "";//获取用户名
if(getInfo().userName){
    userName=getInfo().userName;
};
var num;//卡片数量
// var domainName = domainName();//域名
var protocol = window.location.protocol;
var host = window.location.host;
var domainName = protocol+'//'+host;
//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {},
        finalPrizeMask:''
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
                    self.$nextTick(function () {
                        var cont = $(".cont1").html();
                        $(".cont2").html(cont);
                        wordMove();//左右移动动画
                    })
                    //首页轮播
                    self.$nextTick(function () {
                        var i = 0;
                        var liWidth=6.25+'rem';
                        var clone = $(".banner .bannerList li").first().clone();//克隆第一张图片
                        $(".banner .bannerList").append(clone);//复制到列表最后
                        var size = self.content.bannerList.length+1;
                        var ulWidth=size*6.25+'rem';
                        $(".banner .bannerList li").width(liWidth);
                        $(".banner .bannerList").width(ulWidth);
                        for (var j = 0; j < size-1; j++) {
                            $(".banner .num").append("<li></li>");
                        }
                        $(".banner .num li").first().addClass("on");
                        setInterval(function () { i++; move();},1500);
                        function move() {
                            if (i == size) {
                                $(".banner .bannerList").css({ left: 0 });
                                i = 1;
                            }
                            if (i == -1) {
                                $(".banner .bannerList").css({ left: -(size - 1) * 6.25+'rem' });
                                i = size - 2;
                            }
                            $(".banner .bannerList").stop().animate({ left: -i * 6.25+'rem'}, 500);

                            if (i == size - 1) {
                                $(".banner .num li").eq(0).addClass("on").siblings().removeClass("on");
                            } else {
                                $(".banner .num li").eq(i).addClass("on").siblings().removeClass("on");
                            }
                        }
                    //判断蒙版
                    var couponList=self.content.boluomeCouponList;
                    for(var i=0;i<couponList.length;i++){
                        couponList[i] = eval("("+couponList[i]+")");
                    }
                    for(var j=0;j<self.content.itemsList.length;j++){//是否可赠送
                        num=self.content.itemsList[j].num;
                        if(num==0){
                            self.finalPrizeMask=true;
                        }else if(num==1){
                            self.finalPrizeMask=false;
                        }else {
                            self.finalPrizeMask=false;
                            $('.card').eq(j).find('.num').css('display','block');
                            $('.presentCard').attr('present','Y');
                        }
                    }//是否可赠送
                    })
                }
            })
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
            //alert(self.finalPrizeMask)
            if(!self.finalPrizeMask){
                $.ajax({
                    type: 'get',
                    url: '/H5GG/pickUpSuperPrize',
                    data:{'activityId':activityId},
                    dataType:'JSON',
                    success: function (returnData) {
                        console.log(returnData)
                        if(returnData.success){
                            requestMsg(returnData.msg);
                            for(var j=0;j<self.content.itemsList.length;j++){
                                num=self.content.itemsList[j].num;
                                if(num==0){
                                    return ""
                                }else{
                                    $('.card').eq(j).find('.num').html('x'+(num-1));
                                    if(num-1==0){
                                        $('.card').eq(j).find('.cardMask').css('display','block');
                                        $('.card').eq(j).find('.num').css('display','none');
                                    }else if(num-1==1){
                                        $('.card').eq(j).find('.num').css('display','none');
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
        "shareAppTitle": "消费有返利 领取88.88元现金红包！",  // 分享的title
        'shareAppContent': "我正在51返呗玩场景点亮活动，你也一起来玩吧~",  // 分享的内容
        "shareAppImage": "https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggIndexShare?loginSource=F&activityId="+activityId+"&userName="+userName,  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggIndexShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};

