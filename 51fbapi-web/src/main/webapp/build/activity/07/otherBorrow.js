/**
 * Created by nizhiwei-labtop on 2017/7/18.
 */

let vue=new Vue({
    el:'#vueCon',
    data:{
        content:{
            imgUrl:'https://fs.51fanbei.com/h5/app/activity/05/mayMovie_01_2.jpg'
        }
    },
    created:function () {
        this.logData();

    },
    methods: {
        imgSwiper(){
            let mySwiper = new Swiper ('.banner', {
                loop: true,

            });
        },
        swiper(){
            let title=['热门','低息','最新','极速'];
            let mySwiper = new Swiper ('.swiper-container', {
                loop: true,
                // 如果需要分页器
                pagination: '.swiper-pagination',
                paginationClickable :true,
                paginationBulletRender: function (swiper, index, className) {
                    return '<span class="' + className + '">' + title[index] + '</span>';
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
