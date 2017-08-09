//var activityId = getUrl("activityId");
//获取数据
let vm = new Vue({
    el: '#ggIndexShare',
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
                url: "/H5GGShare/initHomePage",
                data:{activityId:1},
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
            var sceneId=e.sceneId;
            $.ajax({
                url: "/fanbei-web/pickBoluomeCouponForApp",
                type: "POST",
                dataType: "JSON",
                data: {'sceneId':sceneId},
                success: function (returnData) {
                    console.log(returnData)
                    if(returnData.success){
                        requestMsg(returnData.msg);
                    }else{
                        location.href="gglogin"
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
                url: '/fanbei-web/getBrandUrl',
                data:{'shopId':shopId,userName:15839790051},
                dataType:'JSON',
                success: function (returnData) {
                    console.log(returnData)
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href="gglogin";
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            })
        },
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
