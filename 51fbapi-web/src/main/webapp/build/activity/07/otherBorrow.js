/**
 * Created by nizhiwei-labtop on 2017/7/18.
 */

let vue=new Vue({
    el:'#vueCon',
    data:{
        content:{
            imgUrl:'https://fs.51fanbei.com/h5/app/activity/05/mayMovie_01_2.jpg'
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
        imgSwiper(){
            let mySwiper = new Swiper ('.banner', {
                loop: true,
                pagination: '.img-pagination',

            });
        },
        swiper(){
            let title=[this.content.tabList[0].name,this.content.tabList[1].name,this.content.tabList[2].name,this.content.tabList[3].name];
            let mySwiper = new Swiper ('.swiper-container', {
                loop: true,
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
        logData(){
            let self=this;
            $.ajax({
                url:'/borrow/loanShop',
                type:'post',
                success:function (data) {
                    self.content = eval('(' + data + ')');
                    self.content = self.content.data;
                    console.log(self.content);
                    self.$nextTick(function(){
                        self.imgSwiper();
                        self.swiper();
                        console.log(self.divTop);
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
