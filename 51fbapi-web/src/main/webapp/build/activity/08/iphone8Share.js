//获奖公告滚动
//1.速度
var speed = 40;
//2.复制 demo1-->demo2
var cont = $(".roll").html();
$(".copyRoll").html(cont);
//3:创建方法定时执行
function hello(){
    var t = $(".myscroll").scrollTop();
    //console.log(t)
    if(t >= $(".roll").height()){
        t = 0;
    }else{
        t++;
    }
    $(".myscroll").scrollTop(t);
    setTimeout("hello()",speed);
}
hello();
/*判断时间list*/
let currentStarmp=new Date().getTime();
let firstStarmp=Date.parse(new Date('2017/09/20 00:00:00'));
let secondStarmp=Date.parse(new Date('2017/09/21 00:00:00'));
let thirdStarmp=Date.parse(new Date('2017/09/23 00:00:00'));
let fourthStarmp=Date.parse(new Date('2017/09/25 00:00:00'));
console.log(currentStarmp);
if(currentStarmp>=firstStarmp&&currentStarmp<secondStarmp){
    addStyle(1);
}
if(currentStarmp>=secondStarmp&&currentStarmp<thirdStarmp){
    addStyle(2);
    $('.limitTime').show();
}
if(currentStarmp>=fourthStarmp){
    addStyle(3);
}
function addStyle(i){
    $('.time').eq(i).addClass('active01');
    $('.time').eq(i).siblings().removeClass('active01');
    $('.time').eq(i).find('span').addClass('active02');
    $('.time').eq(i).siblings().removeClass('active02');
}
var modelId=getUrl("modelId");//获取活动Id
//获取数据
let vm = new Vue({
    el: '#iphone8Share',
    data: {
        content: {},
        prizeCont:{},
        ruleShow:''
    },
    created: function () {
        this.logData();
        this.loginPrize();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/partActivityInfo',
                data:{'modelId':modelId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data.activityList.slice(0,3);
                    console.log(eval('(' + data + ')').data);
                    self.content.firstList=self.content[0].activityGoodsList;
                    self.content.secondList=self.content[1].activityGoodsList;
                    self.content.thirdList=self.content[2].activityGoodsList;
                    console.log(self.content);
                    self.$nextTick(function () {
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
        //获奖公告初始化数据
        loginPrize(){
            let self = this;
            $.ajax({
                type:'post',
                url:'/fanbei-web/activity/getActivityGoods',
                success:function(data){
                    data = eval('(' + data + ')');
                    console.log(data);
                    self.prizeCont=data.winUsers;
                },
                error:function(){
                    requestMsg('哎呀，出错了！');
                }
            })
        },
        //点击预约与商品
        orderAndGoodClick(){
                window.location.href='iphone8ShareRegister?channelCode=iphone8&pointCode=iphone8';
         },
        //点击活动规则
        ruleClick(){
            let self = this;
            self.ruleShow='Y';
        },
        //点击蒙版
        maskClick(){
            let self = this;
            self.ruleShow='';
        }
        }
})

