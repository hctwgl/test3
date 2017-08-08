
var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

var userItemsId=getUrl("userItemsId");

//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {}
    },
    created: function () {
        this.init();
    },
    methods: {
        //获取页面初始化信息
        init() {
            let self=this;
            $.ajax({
                type: 'get',
                url: "/H5GGShare/ggSendItems",
                data:{'userItemsId':userItemsId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);

                    // wordMove();//左右移动动画
                    // var couponList=self.content.boluomeCouponList;
                    // for(var i=0;i<couponList.length;i++){
                    //     couponList[i] = eval("("+couponList[i]+")");
                    // }
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
