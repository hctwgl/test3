/**
 * Created by nizhiwei-labtop on 2017/7/10.
 */
let activityId = getUrl("activityId");
let vue=new Vue({
    el:'#godness',
    data:{
        content:[],
        tableContent:[],
        message:{},
    },
    created:function () {
         this.init();
    },
    methods: {
        init(){
            let self=this;
            $.ajax({
                url:"/fanbei-web/newEncoreActivityInfo",
                type:'post',
                data:{'activityId':activityId},
                success:function (data) {
                   var del=eval('(' + data+ ')');
                   self.message=del.data;
                    console.log(self.message);
                   
                }
            });
        },
        buyNow(item){
            let self=this;
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        }
    }
});
