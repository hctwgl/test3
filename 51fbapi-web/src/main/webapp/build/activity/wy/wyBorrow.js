/**
 * Created by nizhiwei-labtop on 2017/7/18.
 */
$(".first").each(function() {
    var img = $(this);
    img.load(function () {
        $(".loadingMask").fadeOut();
        console.log(2)
    });
    setTimeout(function () {
        $(".loadingMask").fadeOut();
    },1000)
});
let vue=new Vue({
    el:'#vueCon',
    data:{
        banner:{
            imageUrl:'https://img.51fanbei.com/h5/app/activity/wy/bbanner.png',
            content:'../../app/user/channelRegister?channelCode=wwyy&pointCode=wwyy'
        },
        content:{
            imgUrl:'https://img.51fanbei.com/h5/app/activity/05/mayMovie_01_2.jpg'
        },
        barShow:true
    },
    created:function () {
        this.logData();

    },
    methods: {
        sp(a,b){
            let data=a.split(',');
            return data[b]
        },
        swiper(){
            let title=[this.content.tabList[0].name,this.content.tabList[1].name,this.content.tabList[2].name,this.content.tabList[3].name];
            let mySwiper = new Swiper ('.swiper-container', {
                loop: true,
                autoHeight: true,
                // 如果需要分页器
                pagination: '.swiper-pagination',
                paginationClickable :true,
                paginationBulletRender: function (swiper, index, className) {
                    return '<span class="' + className + ' bullet">' + title[index] + '</span>';
                }
            });
        },
        handleScroll (){
            let win=jQuery(window).scrollTop();
            if(win>=207){
                jQuery('#navWrap').addClass('fixTop');
            }else{
                jQuery('#navWrap').removeClass('fixTop');
            }
        },
        maidian(data){
            //数据统计
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/wyBorrow?type='+data},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        logData(){
            let self=this;
            $.ajax({
                url:'/borrow/loanShop',
                type:'post',
                success:function (data) {
                    self.content = eval('(' + data + ')');
                    self.content = self.content.data;
                    self.content.tabList[0].loanShopList.unshift({
                    iconUrl:"https://img.51fanbei.com/h5/app/activity/wy/bicon.png",
                    linkUrl:"../../app/user/channelRegister?channelCode=wwyy&pointCode=wwyy",
                    lsmIntro:"最高可借20000元，日息低至0.5元",
                    lsmName:"51返呗-极速贷",
                    lsmNo:"51fanbei",
                    marketPoint:"205983,本月放款人数"});
                    console.log(self.content);
                    if(self.content.scrollbar.content==''){
                        self.barShow=false
                    }
                    self.$nextTick(function(){
                        self.swiper();
                        if(getBlatFrom()==2){
                            window.addEventListener('touchstart', self.handleScroll);
                            window.addEventListener('touchmove', self.handleScroll);
                            window.addEventListener('touchend', self.handleScroll);
                        }else{
                            window.addEventListener('scroll', self.handleScroll);
                        }
                    })
                }
            });
        }
    }
});
