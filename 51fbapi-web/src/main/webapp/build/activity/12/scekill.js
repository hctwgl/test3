
let finished = 0;//防止多次请求ajax
let vm=new Vue({
    el:'#vueCon',
    data:{
        content:{
            bannerList:[
                'https://img.51fanbei.com/h5/app/activity/05/mayMovie_01_2.jpg'
            ],
            goodsList:[
                // {
                //     goodName:'Beats正品粉色头戴式…',
                //     rebateAmount:22,
                //     priceAmount:999,
                //     goodType:1,
                //     goodsIcon:'',
                //     volume:100,
                //     goodsUrl:'http://baidu.com',
                //     total:53,
                //     nperMap:{
                //         isFree:1,
                //         freeAmount:5,
                //         amount:10
                //     }
                // },
                // {
                //     goodName:'小米红米a4手机',
                //     rebateAmount:22,
                //     priceAmount:999,
                //     goodType:2,
                //     goodsIcon:'',
                //     volume:100,
                //     goodsUrl:'http://baidu.com',
                //     total:1000,
                //     nperMap:{
                //         isFree:0,
                //         freeAmount:5,
                //         amount:10
                //     }
                // }
            ]
        },
        type:1
    },
    created:function () {
        this.logData(1);

    },
    methods: {
        logData(page){
            let self=this;
            let url='/fanbei-web/activity/getFlashSaleGoods';
            if(self.type===2){
                url='/fanbei-web/activity/getBookingRushGoods';//tomorrow
            }
            $.ajax({
                url:url,
                type:'post',
                data:{pageNo:page},
                success:function (data) {
                    data = eval('(' + data + ')');
                    if(self.type===1){
                        self.content.bannerList = data.data.BannerList;
                    }
                    self.content.goodsList = data.data.goodsList;
                    console.log(self.content);
                    finished=0;
                    self.$nextTick(function(){
                        let imgS=new Swiper ('.banner', {
                            loop: true,
                            autoplay : 4000,
                            pagination: '.img-pagination',

                        });
                    })
                }
            });
        },
        loading(data){//判断有无存量，商品进度条
            let a=data.volume,
                b=data.total;
            if(a<b){
                return parseInt(a/b*100)+"%"
            }else{
                return 0
            }
        },
        typeClick(type){
            this.type=type;
            this.logData(1)

        },
        remind(data){
            if(data.reserve=='N'){
                $.ajax({
                    url:'/fanbei-web/activity/reserveGoods',
                    type:'post',
                    data:{goodsId:data.goodsId},
                    success:function (res) {
                        res = eval('(' + res + ')');
                        if(res.success){
                            requestMsg('预约成功');
                            data.reserve='Y'
                        }else{
                            requestMsg('预约失败')
                        }
                    }
                })
            }
        },

        goodsClick(data){
            $.ajax({
                    url:'/fanbei-web/activity/checkGoods',
                    type:'post',
                    data:{goodsId:data.goodsId},
                    success:function (res) {
                        res = eval('(' + res + ')');
                        if(res.success){
                            window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+data.goodsId+'"}'
                        }else{
                            requestMsg(res.msg);
                            data.volume=data.total;
                        }
                    }
                })
        }
    }
});
//滚动加载更多商品
let page=1;
$(window).on('scroll',function () {
    if(finished===0){
        let scrollTop = $(this).scrollTop();
        let allHeight = $(document).height();
        let windowHeight = $(this).height();
        if (allHeight-windowHeight<=scrollTop+400) {
            page++;
            finished=1; //防止未加载完再次执行
            let url='/fanbei-web/activity/getFlashSaleGoods';
            if(vm.type===2){
                url='/fanbei-web/activity/getBookingRushGoods';//tomorrow
            }
            $.ajax({
                url:url,
                type:'post',
                data:{pageNo:page},
                success:function (data) {
                    data = eval('(' + data + ')');
                    if(data.data.goodsList.length>0){
                        if(vm.type===1){
                            vm.content.bannerList = data.data.BannerList;
                        }
                        vm.content.goodsList=vm.content.goodsList.concat(data.data.goodsList);
                        console.log(vm.content);
                        finished=0;
                    }
                }
            });
        }
    }
});