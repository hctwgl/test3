
//转化代码
var activityId = getUrl("activityId");//获取活动Id
var userName='';//获取用户名
//获取页面名称传到登录页
var currentUrl=window.location.href;
var index=currentUrl.lastIndexOf('/');
var urlName=currentUrl.slice(index+1);
urlName=urlName.replace(/\?/g,'&');
var num;//卡片数量
var typeFrom=getUrl("typeFrom");//渠道类型
var typeFromNum=getUrl("typeFromNum");//渠道类型数
$(function(){
    if(typeFrom=='Jrtt'||typeFrom=='Jmtt'){
        $('.companyWord02').show();
    }else{
        $('.companyWord01').show();
    }
});
//获取数据
let vm = new Vue({
    el: '#ggNewAdjm',
    data: {
        content: {}
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GGShare/initHomePage",
                data:{activityId:activityId},
                success: function (data) {
                    $('.positionImg').fadeOut(4000);
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    self.$nextTick(function () {
                        for(var k=0;k<self.content.boluomeCouponList.length;k++){
                            if(self.content.boluomeCouponList[k].isHas=='Y'){
                                $('.coupon').eq(k).addClass('changeGray');
                            }
                        }
                    })
                    self.$nextTick(function () {
                        var cont = $(".cont1").html();
                        $(".cont2").html(cont);
                        wordMove();//左右移动动画
                    })
                    self.$nextTick(function () {
                        //判断蒙版
                        for(var j=0;j<self.content.itemsList.length;j++){//是否可赠送
                            num=self.content.itemsList[j].num;
                            if(num>=2){
                                $('.card').eq(j).find('.num').css('display','block');
                                //$('.presentCard').attr('present','Y');
                            }
                        }
                    })
                }
            });
            //点击加埋点
            $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/ggNewAdjm?activityId=1&type=new_ini&typeFrom='+typeFrom+'&typeFromNum='+typeFromNum},
                success:function (data) {
                    console.log(data)
                }
            });
        },
        //点击参与人数进入排行榜
        joinAmountClick:function(){
            window.location.href='ggrankingList?activityId='+activityId;
        },
        //点击优惠券
        couponClick:function(index){
            $('#mobile').focus();
        },
        //点击卡片
        cardClick:function(){
            $(window).scrollTop(0);
            $('#mobile').focus();
        },
        //点击获取终极大奖
        finalPrize:function(){
            $(window).scrollTop(0);
            $('#mobile').focus();
        },
        presentClick:function(){
            $(window).scrollTop(0);
            $('#mobile').focus();
        },
        demandClick:function(){
            $(window).scrollTop(0);
            $('#mobile').focus();
        },
        ruleClick:function(){
            $('.alertRule').css('display','block');
            $('.mask').css('display','block');
        },
        close:function(){
            $('.alertPresent').css('display','none');
            $('.mask').css('display','none');
            $('.title').css('display','none');
            $('.alertRule').css('display','none');
            $('.mask').css('display','none');
            $('.alertFinalPrize').css('display','none');
        },
        fixImgUrl:function(i){
            return "http://f.51fanbei.com/h5/app/activity/10/ggNewCard0"+i+".png";
        }
    }
})
//左右移动动画
var speed = 20;
function wordMove(){
    var left = $(".personAmount").scrollLeft();
    if(left >= $(".cont1").width()){
        left = 0;
    }else{
        left++;
    }
    $(".personAmount").scrollLeft(left);
    setTimeout("wordMove()",speed);
}


