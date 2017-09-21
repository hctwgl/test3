
let modelId=getUrl('modelId');//获取模板id
let groupId=getUrl('groupId');//获取优惠券组id
//获取域名
let protocol = window.location.protocol;
let host = window.location.host;
let domainName = protocol+'//'+host;
//获取数据
let vm = new Vue({
    el: '#hairyCrab',
    data: {
        content: {},
        couponCont:{}
    },
    created: function () {
        this.logData();
        this.logCoupon();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo",
                data:{'modelId':modelId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data.activityList.slice(0,2);
                    console.log(eval('(' + data + ')').data);
                    self.content.firstList=self.content[0].activityGoodsList;
                    self.content.secondList=self.content[1].activityGoodsList;
                    console.log(self.content);
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
                            placeholder : "http://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        //获取优惠券初始化信息
        logCoupon() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/activityCouponInfo",
                data:{'groupId':groupId},
                success: function (data) {
                    self.couponCont = eval('(' + data + ')').data.couponInfoList[0];
                    console.log(self.couponCont.couponId);
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            });
        },
        //点击领券
        couponClick:function() {
            let self = this;
            let couponId = self.couponCont.couponId;
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    if (returnData.success) {
                        requestMsg("优惠劵领取成功");
                    } else {
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            //requestMsg(returnData.msg);
                            requestMsg("您已经领取，快去使用吧");
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                        }
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            })
        },
        //点击商品
        goodClick:function(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        }
    }
});
// app调用web的方法
function alaShareData(){
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "@所有人，买阳澄湖大闸蟹也可分期了",  // 分享的title
        'shareAppContent': "终于等到你，还好我没放弃。【阳澄湖大闸蟹礼劵】预订开始啦~!最低只要119元6只装",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/09/crab9.png",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/hairyCrabShare?modelId="+modelId+"&groupId="+groupId,  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "hairyCrabShare" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};
