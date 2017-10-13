
/**

 * Created by nizhiwei-labtop on 2017/7/10.
 */

// import data from './data.js'



Vue.config.devtools = true


let activityId = getUrl("activityId");
let vue=new Vue({
    el:'#oppoPic',
    data:{
        content:[],
        shoplist: [],
        tableContent:[],
        message:{},
        list:[],
        local:[//将数据存起来
                {des:'前后2000万，拍照更清晰',del:'18k镀金队徽 稀缺资源'},
                {des:'前后2000万，拍照更清晰',del:'后置双摄|6英寸大屏|VOOC闪充'},
                {des:'正面指纹|4G+64GB|VOOC闪充',},
                {des:'前后1600万像素',del:'双卡双待|4G+64GB|vooc闪充'},
                {des:'1600万美颜自拍', del:'正面指纹识别|4G+64GB'},
                {des:'前后1600万臻美镜头',},
                {des:'全新美颜，留住这一刻',del:'后置800万|全网通2.0'}
        ]
    },
    mounted:function () {
        this.$nextTick(()=>{
            this.init();
        })
    },
    methods: {
        init(){
            let self=this;
            $.ajax({
                url:"/fanbei-web/newEncoreActivityInfo",
                type:'post',
                data:{'activityId':activityId},
                success:function (data) {
                //   console.log(data);
                   var del=eval('(' + data+ ')');
                   self.message=del.data;
                    console.log(self.message);
                    self.list=self.message.oneLevelGoodsInfo.goodsList;//后台数据
                    console.log(self.list);

                    // setTimeout(()=>{
                        for(var i=0;i<self.list.length;i++){
                            self.list[i].des=self.local[i].des;
                            self.list[i].del=self.local[i].del;
                            //console.log(self.list[i].del);
                        }
                    // },0)                 
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
    },
    
});





