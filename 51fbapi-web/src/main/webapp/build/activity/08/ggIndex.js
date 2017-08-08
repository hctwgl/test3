
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};
//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {}
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
                data:{'activityId':1},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    wordMove();//左右移动动画
                    var couponList=self.content.boluomeCouponList;
                    for(var i=0;i<couponList.length;i++){
                        couponList[i] = eval("("+couponList[i]+")");
                    }

                }
            })
        },
        //点击优惠券
        couponClick:function(e){
            var couponId=e.couponId;
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId:couponId
                },
                success: function(returnData){
                    if(returnData.success){
                        requestMsg("恭喜您领券成功");
                    }else{
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            requestMsg(returnData.msg);
                            requestMsg("您已领过优惠券了，快去使用吧 ~");
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                            requestMsg("您下手慢了哦，优惠券已领完，下次再来吧 ~");
                        }
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
            //alert(shopId)
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrl',
                data:{'shopId':shopId,'userName':userName},
                dataType:'JSON',
                success: function (returnData) {
                    if(returnData.success){
                        console.log(returnData)
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
           $.ajax({
                type: 'get',
                url: '/H5GGShare/pickUpSuperPrize',
                data:{'activityId':1},
                dataType:'JSON',
                success: function (data) {
                    console.log(data)
                },
                error: function(){
                    requestMsg("请求失败");
                }
            })
        },
        //点击我要赠送卡片
        presentClick:function(){
            $('.alertPresent').css('display','block');
            $('.mask').css('display','block');
            $('.presentTitle').css('display','block');
            $('.sure').html('确定赠送');
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GGShare/sendItems",
                data:{activityId:1},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(data);
                }
            })
        },//点击我要赠送卡片
        //点击我要索要卡片
        demandClick:function(){
            $('.alertPresent').css('display','block');
            $('.mask').css('display','block');
            $('.demandTitle').css('display','block');
            $('.sure').html('确定索要');
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GGShare/sendItems",
                data:{itemsId:itemsId,userId:userId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(data);
                }
            })
        },//点击我要索要卡片
        ruleClick:function(){
            $('.alertRule').css('display','block');
            $('.mask').css('display','block');
        },
        close:function(){
            $('.alertPresent').css('display','none');
            $('.mask').css('display','none');
            $('.title').css('display','none');
            $('.alertRule').css('display','none');
            $('.mask').css('display','none');
        }
    }
})
