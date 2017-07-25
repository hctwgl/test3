/**
 * Created by yoe on 2017/7/25.
 */




let activityId = getUrl("activityId");
let modelId = getUrl("modelId");

var vm=new Vue({
    el: '#freeHome',
    data: {
        discountMap: [],
        rebateMap: []
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
                // url: '/fanbei-web/partActivityInfo',
                dataType:'json',
                data:{'activityId':activityId},
                // data:{'modelId':modelId},
                type: 'post',
                success:function (data) {
                    _this.discountMap=data.data.qualityGoodsList.slice(0,3);
                    _this.rebateMap=data.data.qualityGoodsList.slice(4,-1);
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
