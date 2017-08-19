/*
* @Author: yoe
* @Date:   2017-08-18 15:37:34
*/


let modelId=getUrl('modelId');

new Vue({
    el:'#altair',
    data:{
        couponCont: [],
        content: [],
        list01: {},
        list02: {},
        list03: {}
    },
    created:function () {
        this.init();
    },
    methods:{
        init(){
            let self=this;

            $.ajax({  // 优惠券请求
                type: 'post',
                url: "/fanbei-web/activityCouponList",
                // url: '/fanbei-web/couponCategoryInfo',
                success: function (res) {
                    self.couponCont=(JSON.parse(res)).data;
                    console.log(self.couponCont);
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });

            $.ajax({  // 商品列表请求
                url: '/fanbei-web/partActivityInfo',
                type: 'post',
                data:{
                    modelId: modelId
                },
                success:function (res) {
                    self.content = (JSON.parse(res)).data;
                    console.log(self.content);

                    self.list01=self.content.activityList[0].activityGoodsList;
                    self.list02=self.content.activityList[1].activityGoodsList;
                    self.list03=self.content.activityList[2].activityGoodsList;

                    self.$nextTick(function () { //dom渲染完成后执行
                        lazy.init();
                    })
                }
            });
        },
        goGoodsDetail(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        },
        couponClick(e){
            let self=this;
            var couponId=e.couponId;
            //点击领券
            console.log(couponId);
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId:couponId
                },
                success: function(returnData){
                    if(returnData.success){
                        requestMsg("优惠劵领取成功");
                    }else{
                        console.log(returnData)
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            requestMsg(returnData.msg);
                            requestMsg("优惠券个数超过最大领券个数");
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                        }
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
        }
    }
});
