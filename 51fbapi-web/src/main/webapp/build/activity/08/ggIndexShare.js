'use strict';
var activityId = getUrl("activityId");//获取活动Id
var userName='';//获取用户名
//获取页面名称传到登录页
var currentUrl=window.location.href;
var index=currentUrl.lastIndexOf('/');
var urlName=currentUrl.slice(index+1);
urlName=urlName.replace(/\?/g,'&');
var num;//卡片数量
//获取数据
let vm = new Vue({
    el: '#ggIndexShare',
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
                    console.log(self.content.superPrizeStatus);
                    self.$nextTick(function () {
                        for(var k=0;k<self.content.boluomeCouponList.length;k++){
                            //console.log(self.content.boluomeCouponList[k].isHas)
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
                        self.imgSwiper();//首页轮播
                    //判断蒙版
                      for(var j=0;j<self.content.itemsList.length;j++){//是否可赠送
                          num=self.content.itemsList[j].num;
                          if(num>=2){
                              $('.card').eq(j).find('.num').css('display','block');
                              $('.presentCard').attr('present','Y');
                          }                
                      }
                    })
                }
            })
        },
        //轮播
        imgSwiper(){
            let mySwiper = new Swiper ('.banner', {
                loop: true,
                speed:1000,
                autoplay :2000,
                autoplayDisableOnInteraction : false
            });
        },
        //点击参与人数进入排行榜
        joinAmountClick:function(){
            window.location.href='ggrankingList?activityId='+activityId;
        },
        //点击轮播图
        bannerClick:function(){
            userName=getCookie('userName');
            if(userName=='' || !userName){
                window.location.href="gglogin?urlName="+urlName;
            }else{
                window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
        },
        //点击优惠券
        couponClick:function(index){
            userName=getCookie('userName');
            let self = this;
            if(userName){
                var sceneId=self.content.boluomeCouponList[index].sceneId;
                $.ajax({
                    url: "/H5GGShare/pickBoluomeCouponWeb",
                    type: "POST",
                    dataType: "JSON",
                    data: {'sceneId':sceneId},
                    success: function (returnData){
                        //console.log(returnData)
                        if(returnData.success){
                            if(self.content.boluomeCouponList[index].isHas=='N'){
                                requestMsg(returnData.msg);
                                $('.coupon').eq(index).addClass('changeGray');
                            }else{
                                requestMsg(returnData.msg);
                            }
                        }else{
                            requestMsg(returnData.msg);
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                });
            }else{
                window.location.href="gglogin?urlName="+urlName;
            }
        },
        //点击卡片
        cardClick:function(){
            userName=getCookie('userName');
            if(userName=='' || !userName){
                window.location.href="gglogin?urlName="+urlName;
            }else{
                window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
        },
        //点击获取终极大奖
        finalPrize:function(){
            let self = this;
            userName=getCookie('userName');
            if(userName=='' || !userName){
                window.location.href="gglogin?urlName="+urlName;//未登录
            }else{
                    $.ajax({
                            type: 'get',
                            url: '/H5GGShare/pickUpSuperPrize',
                            data:{'activityId':activityId},
                            dataType:'JSON',
                            success: function (returnData) {
                                console.log(returnData);
                                if(returnData.success){
                                    //requestMsg(returnData.msg);
                                    $('.mask').css('display','block');
                                    $('.alertFinalPrize').css('display','block');
                                    self.content.superPrizeStatus='YN';
                                    for(var j=0;j<self.content.itemsList.length;j++){
                                        num=self.content.itemsList[j].num;
                                        if(num==0){
                                            return ""
                                        }else{
                                            $('.card').eq(j).find('.num').html('x'+(num-1));
                                            if(num-1==0){
                                                $('.card').eq(j).find('.gray').css('display','block');
                                                $('.card').eq(j).find('.num').css('display','none');
                                            }else if(num-1==1){
                                                $('.card').eq(j).find('.num').css('display','none')
                                            }
                                        }
                                    }
                                }else{
                                    if(self.content.superPrizeStatus=='N'){
                                        requestMsg(returnData.msg);//已登录缺少卡片
                                    }else if(self.content.superPrizeStatus=='YN'){
                                        requestMsg(returnData.msg);//已领取
                                    }
                                }
                            },
                            error: function(){
                                requestMsg("请求失败");
                            }
                     })
            }
        },
        presentClick:function(){
            userName=getCookie('userName');
            if(userName=='' || !userName){
                window.location.href="gglogin?urlName="+urlName;
            }else{
                window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
        },
        demandClick:function(){
            userName=getCookie('userName');
            if(userName=='' || !userName){
                window.location.href="gglogin?urlName="+urlName;
            }else{
                window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
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
            return "http://f.51fanbei.com/h5/app/activity/08/gg000"+i+".png";
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