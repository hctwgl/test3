
// let userName = "";
// if(getInfo().userName){
//     userName=getInfo().userName;
// };
let activityId = getUrl("activityId");

// 获取网站的域名
let domainName = domainName();

// 点击手机弹窗
var vm=new Vue({
    el: '#oppoR11',
    data: {
        show: [true,false,false,false,],
        url: domainName+'/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"121129"}',
        goodsMobileListMap: []
    },
    created:function(){
        let _this=this;
        $.ajax({
            url: '/fanbei-web/encoreActivityInfo',
            dataType:'json',
            data:{'activityId':activityId},
            type: 'post',
            success:function (data) {
                _this.goodsMobileListMap=data.data;
                console.log(_this.goodsMobileListMap);
            },
            error: function(){
              requestMsg("请求失败");
            }
        });
    },
    methods:{

        oppoList(e){
            for(let i=0;i<this.show.length;i++){
                Vue.set(vm.show,i,false);
            }
            Vue.set(vm.show,e-1,true);

            // 手机的privateGoodsId
            let privateGoodsId=[121129,121130];
            let notifyUrl = "https://app.51fanbei.com/fanbei-web/opennative?name=GOODS_DETAIL_INFO";
            this.url=notifyUrl+'&params={"privateGoodsId":"'+privateGoodsId[e-1]+'"}';  // a链接的url
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
