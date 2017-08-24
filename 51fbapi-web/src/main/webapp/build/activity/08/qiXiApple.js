/**
 * Created by yoe on 2017/8/3.
 */

let activityId = getUrl("activityId");

var vm=new Vue({
    el: '#selfHome',
    data: {
        discountMap: [],
        rebateMap: [],
        returnData: []
    },
    created:function(){
        let _this=this;
        _this.initial();
        loading();
    },
    methods:{
        initial(){
            let _this=this;
            $.ajax({
                url: '/fanbei-web/encoreActivityInfo',
                dataType:'json',
                data:{'activityId':activityId},
                type: 'post',
                success:function (data) {
                    console.log(data);
                    _this.discountMap=data.data.recommendGoodsList.slice(0,3);
                    _this.rebateMap=data.data.recommendGoodsList.slice(3);
                    _this.returnData=data.data;
                     _this.$nextTick(function () {
                         $(".loadingMask").fadeOut();
                        $("img.lazy").lazyload({
                            placeholder : "http://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })

                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
        },
        goGoodsDetail(item){
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        }
    }
});