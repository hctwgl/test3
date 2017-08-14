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
                        lazy.init();
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