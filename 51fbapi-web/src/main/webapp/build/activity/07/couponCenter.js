var liLength=$('.navList').find('li').length;
var tabWidth=0;
var liWidth=0;
var ulWidth=0;
if(liLength<2){
     $('#tabNav').hide();
}else if(liLength>=2&&liLength<=5){
     tabWidth=$('#tabNav').width();
     $('.navList').find('li').eq(0).width();
     console.log(liWidth)
     //console.log(tabWidth)
     liWidth=tabWidth/liLength;
     //console.log(liWidth)
     $('.navList').find('li').width(liWidth);
     liClick();
}else if(liLength>5){
     tabWidth=$('#tabNav').width();
     liWidth=tabWidth/5;
     ulWidth=liWidth*liLength;
     $('.navList').find('li').width(liWidth);
     $('.navList').width(ulWidth);
     liClick();
}
//tab栏点击事件
function liClick(){
     $('.navList').find('li').click(function(){
         var index=$(this).index();
         //console.log(index);
         $(this).addClass('border').siblings().removeClass('border');
         $('.contList').find('li').eq(index).show().siblings().hide();         
     })   
}
//获取数据
let vm = new Vue({
    el: '#content',
    data: {
        content: {},
        isActive: true
    },
    created: function () {
        this.logData();
    },
    methods: {
        logData() {
            //获取页面初始化信息
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/couponCategoryInfo",
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);

                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        }/*,
        //点击tab栏切换
        isshow: function () {
            this.isActive = !this.isActive;
        },
        buyNow(id){
            // window.location.href = '/fanbei-web/opennative?params={"goodsId":"' + id + '"}'
            window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"' + id + '"}'
        }*/
    }
})
