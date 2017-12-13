
var groupId = getUrl("groupId"); //获取活动Id
var modelId = getUrl("modelId"); //获取模板Id
var imgrooturl = "https://f.51fanbei.com/h5/app/activity/11";
// 获取页面尺寸
var windowW = $(window).outerWidth(),
page = 1; // 默认页数从1开始
let finished = 0;//防止多次请求ajax

let vm = new Vue({
    el: '#listPageControll',
    data: {
        content: '',
        isShow: true,
        m: '',
        c: '',
        tab: 1,
        allStartTime: '',
        productList: '',
        productListDetail: '',
        flag :true,
        categoryList:'',
        leimu:'',
        detailDes:'',
        categoryId:'',
        arr:[],
        index:'',
        return:'',
        page:'',
        pageNo:1, //todo
        getId:''

    },
    created: function () {
        this.category();
        this.scrollFn();
    },
    methods: {
        //导航类目请求接口
        category() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "https://testsupplier.51fanbei.com/category/h5/list ",
                success: function (data) {
                    let categoryList=data;
                    console.log(categoryList,'categoryList');
                    self.leimu=categoryList.data;
                    let aa=[];
                    for(var i=0;i<self.leimu.length;i++){
                        let index=self.leimu[i].rid;//获取id
                        aa.push(index);
                
                    }
                     self.arr=aa;
                    if(data.code!==1000){//code不等于1000的时候弹出信息
                        requestMsg(data.msg);
                    }

                    self.logData(self.arr[0])

                },
                error: function () {
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        //商品初始化信息
          logData(c) {
            let self = this;
            console.log(self.pageNo)
            console.log(self.detailDes,">>>>>>>")
            $.ajax({
                type: 'post',
                url: "https://testsupplier.51fanbei.com/goods/h5/list",
                data:{
                    categoryId:c,
                    pageNo:self.pageNo
                },
                success: function (data) {
                    let productList=data;
                    console.log(productList.data,'productList');
                    if(productList.data.length>0){
                        self.detailDes=self.detailDes.concat(productList.data);
                        // for(var i=0; i< productList.data.length; i++){
                        //     // self.detailDes.push(productList.data[i]);
                        //     console.log(self.detailDes,'2222222222222222222')
                        // }
                        self.detailDes=productList.data;
                        console.log( self.detailDes,'detailDes')
                        self.page=productList.pageNo;//页数
                        self.pageNo=self.page;
                    }else{
                        $('.nomore').show();//显示没有更多数据的文字
                    }

                },
                error: function () {
                    requestMsg('哎呀，出错了！')
                }
            })

        }, 
        scrollFn: function() {
            let self=this;
                $(window).scroll(function () {
                    var scrollTop = $(this).scrollTop();//滚动条距离顶部的高度
                    var scrollHeight = $(document).height();//当前页面的总高度
                    var windowHeight = $(this).height();//当前可视的页面高度
                    console.log(windowHeight,'windowHeight');
                    if (scrollTop + windowHeight >= scrollHeight) {
                        self.pageNo++;
                        self.logData(self.getId);
                        
                    }  
                    /* if(scrollHeight-windowHeight<=scrollTop+400){
                        // self.pageNo++;
                        self.logData(self.getId);
                        
                    } */
                });
        },
        //点击商品
        goodClick(p) {
            if (p.source == "SELFSUPPORT") {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + p.rid + '"}';
            } else {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"' + p.rid + '"}';
            }
        },
        //点击tab栏切换
        tabClick(i) {
            this.tab = i + 1;
            this.getId=this.arr[i];
            console.log(this.getId,'getId')
            let self = this;
            $.ajax({
                type: 'post',
                url: "https://testsupplier.51fanbei.com/goods/h5/list",
                data:{
                    categoryId:this.arr[i],
                    pageNo:1
                },
                success: function (data) {
                    let productList=data;
                    console.log(productList,'productList');
                    self.detailDes=productList.data;
                    console.log( self.detailDes,'detailDes')

                },
                error: function () {
                    requestMsg('哎呀，出错了！')
                }
            })
       
        }
    }
})
