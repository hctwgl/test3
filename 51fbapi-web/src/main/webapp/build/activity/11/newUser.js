let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;

//获取数据
let vm = new Vue({
    el: '#newUser',
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
                url: "/activity/freshmanShare/homePage",
                success: function (data) {
                     console.log(data);
                     /*self.content=eval('('+data.data+')');*/
                    self.content=data.data.goodsList;
                     console.log(self.content)
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
                data:{maidianInfo:'/fanbei-web/activity/newUser?type=newUser_ini'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击活动详情
        activityDetailClick(){
            let self=this;
            self.ruleShow=true;
            $('body').addClass('overFlowClick');
            $('html').addClass('overFlowClick');
            $('.alertRule').animate({'left':'15.4%'},600);
        },
        //点击closeRule关闭mask和规则
        closeClick(){
            let self=this;
            self.ruleShow=false;
            $('body').removeClass('overFlowClick');
            $('html').removeClass('overFlowClick');
            $('.alertRule').animate({'left':'140%'},600);
        },
        //点击立即分享
        shareNowClick(){
            $.ajax({
                type: 'post',
                url: "/activity/freshmanShare/isNew",
                success: function (data) {
                    console.log(data);
                    if(data.msg&&data.msg=='没有登录'){  //是否登录
                        window.location.href=data.data.loginUrl;
                    }else{ //已登录调用分享
                        let dat='{"shareAppTitle":"20元话费、300M流量，3元超值购点击即领","shareAppContent":"51返呗超值新人礼：20元话费3元领，快来抢购吧~","shareAppImage":"https://f.51fanbei.com/h5/app/activity/11/newUser06.jpg","shareAppUrl":"' + domainName + '/fanbei-web/activity/newUserShare'+ '","isSubmit":"Y","sharePage":"newUserShare'+'"}';
                        let base64 = BASE64.encoder(dat);
                        window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params='+base64;
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //立即分享埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/newUser?type=shareNow'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击邀请有礼---老用户
        inviteNowClick(){
            $.ajax({
                type: 'post',
                url: "/activity/freshmanShare/isNew",
                success: function (data) {
                    console.log(data);
                    if(data.msg&&data.msg=='没有登录'){  //是否登录
                        window.location.href=data.data.loginUrl;
                    }else{ //已登录--到-邀请有礼页面
                        window.location.href = 'https://app.51fanbei.com/fanbei-web/app/newinvite';
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //邀请有礼埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/newUser?type=inviteNow'},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击立即抢购
        buyNowClick(item){
            $.ajax({
                type: 'post',
                url: "/activity/freshmanShare/isNew",
                success: function (data) {
                    console.log(data);
                    if(data.msg&&data.msg=='没有登录'){  //是否登录
                        window.location.href=data.data.loginUrl;
                    }else if(data.data.isNew=='Y'){ //新用户到--商品详情页
                        if ( item.source=="SELFSUPPORT" ) {
                            window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.numId+'"}';
                        } else {
                            window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.numId+'"}';
                        }
                    }else if(data.data.isNew=='N'){
                        requestMsg('您已不是新用户，暂不能购买，可以去邀请朋友购买或参加邀请有礼活动');
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //立即抢购埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/newUser?type=buyNow'},
                success:function (data) {
                    console.log(data)
                }
            });
        }
    }
});


