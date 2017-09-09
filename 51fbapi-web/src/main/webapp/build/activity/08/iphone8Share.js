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
//获取数据
let vm = new Vue({
    el: '#iphone8Share',
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
        goodClick(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}';
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}';
            }
        },
        orderClick(item){
            window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}';
        }
    }
})

