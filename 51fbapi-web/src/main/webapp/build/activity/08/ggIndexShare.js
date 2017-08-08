//var activityId = getUrl("activityId");
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};
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
