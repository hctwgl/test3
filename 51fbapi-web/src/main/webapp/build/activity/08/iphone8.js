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

var activityId=getUrl("activityId");//获取活动Id
var protocol = window.location.protocol;
var host = window.location.host;
var domainName = protocol+'//'+host;//获取域名
//获取数据
let vm = new Vue({
    el: '#iphone8',
    data: {
        content: {},
        prizeCont:{},
        orderStatus:'',
        loginStatus:'',
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
                url: '/fanbei-web/newEncoreActivityInfo',
                data:{'activityId':activityId},
                success: function (data) {
                    data = eval('(' + data + ')');
                    console.log(data);
                    self.content=data.data;
                    if(data.success){
                        self.$nextTick(function () {
                            $(".loadingMask").fadeOut();
                            $("img.lazy").lazyload({
                                placeholder : "http://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                                effect : "fadeIn",  // 载入使用的效果
                                threshold: 200 // 提前开始加载
                            });
                        })

                    }
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
                    self.orderStatus=data.status;
                    self.loginStatus=data.loginStatus;
                    console.log(self.loginStatus);
                    console.log(self.orderStatus)
                },
                error:function(){
                    requestMsg('哎呀，出错了！');
                }
            })
        },
        //点击商品
        goodClick(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}';
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}';
            }
        },
        //点击预约
        orderClick(item){
            let self = this;
            if(self.loginStatus=='N'){
                window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
            }else{
                if(self.orderStatus=='FAIL'){
                    window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}';
                }
            }
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
// app调用web的方法
function alaShareData(){
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "iPhone 8 预约立减100",  // 分享的title
        'shareAppContent': "十周年 翘首以待！1元预约立减100，每日限抽取5名成功分享用户获iPhone 8大奖，限时秒杀iPhone 6 仅1999元！",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/09/iphone8_06.jpg",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/iphone8Share?activityId="+activityId+"&sharePage=iphone8Share",  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "iphone8Share" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};
