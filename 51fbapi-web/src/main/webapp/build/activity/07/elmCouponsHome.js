
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

// 开始时间的时间戳
// var startDate = new Date("July 7,2017 10:00:00");
// var startStamp = startDate.valueOf();

// 结束时间的时间戳
// var endDate = new Date("July 10,2017 23:59:59");
// var endStamp = endDate.valueOf();

// 获取当前时间的时间戳
// var now = new Date();
// var nowTimeStamp = now.valueOf();


new Vue({
    el: '#elmCouponsHome',
    methods: {
        boluomiHome:function(shopId){  // 进入菠萝蜜专场
            $.ajax({
                url: "/fanbei-web/getBrandUrl",
                type: "POST",
                dataType: "JSON",
                data:{
                    'shopId': shopId,
                    'userName': userName
                },
                success: function(returnData){
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
        },
        boluomiCoupons: function(sceneId){ // 点击领取优惠券
            $.ajax({
                url: '/fanbei-web/pickBoluomeCoupon',
                type: 'POST',
                dataType: "JSON",
                data:{
                  'sceneId': sceneId,
                  'userName': userName
                },
                success: function(returnData){
                  if(returnData.success){
                    requestMsg("领劵成功");
                  }else{
                    if(returnData.url){
                      location.href=returnData.url;
                    }else{
                      requestMsg(returnData.msg);
                    }
                  }
                }
            });
        }
    }
})
