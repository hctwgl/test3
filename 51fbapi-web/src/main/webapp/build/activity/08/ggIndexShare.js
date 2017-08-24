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
        content: {},
        finalPrizeMask:''
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
                    console.log(typeof(self.content.boluomeCouponList[0]))
                    self.$nextTick(function () {
                        var cont = $(".cont1").html();
                        $(".cont2").html(cont);
                        wordMove();//左右移动动画
                    })
                    //首页轮播
                    self.$nextTick(function () {
                        var i = 0;
                        var liWidth=6.25+'rem';
                        var clone = $(".banner .bannerList li").first().clone();//克隆第一张图片
                        $(".banner .bannerList").append(clone);//复制到列表最后
                        var size = self.content.bannerList.length+1;
                        var ulWidth=size*7.5+'rem';
                        $(".banner .bannerList li").width(liWidth);
                        $(".banner .bannerList").width(ulWidth);
                        for (var j = 0; j < size-1; j++) {
                            $(".banner .num").append("<li></li>");
                        }
                        $(".banner .num li").first().addClass("on");
                        setInterval(function () { i++; move();},4000);
                        function move() {
                            if (i == size) {
                                $(".banner .bannerList").animate({left: '0'}, 0);
                                $(".banner .bannerList").find('li').eq(i - 1).css('opacity', 0);
                                i = 1;
                                $(".banner .bannerList").find('li').eq(i).css('opacity', 1);
                            }
                            if (i == -1) {
                                $(".banner .bannerList").css({left: -(size - 1) * 7.5 + 'rem'});
                                $(".banner .bannerList").find('li').eq(i - 1).css('opacity', 0);
                                $(".banner .bannerList").find('li').eq(i).css('opacity', 1);
                                i = size - 2;
                            }
                            $(".banner .bannerList").stop().animate({left: -i * 7.5 + 'rem'}, 1000);
                            $(".banner .bannerList").find('li').eq(i - 1).css('opacity', 0);
                            $(".banner .bannerList").find('li').eq(i).css('opacity', 1);
                        }
                    //判断蒙版
                      for(var j=0;j<self.content.itemsList.length;j++){//是否可赠送
                          num=self.content.itemsList[j].num;
                          if(num>=2){
                              $('.card').eq(j).find('.num').css('display','block');
                              $('.presentCard').attr('present','Y');
                          }                
                      }
                       for(var j=0;j<self.content.itemsList.length;j++){//是否可领取终极大奖
                            num=self.content.itemsList[j].num;
                            if(num==0){   
                                self.finalPrizeMask=true;
                                break;
                            }
                        }
                    })
                }
            })
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
        couponClick:function(e){
            userName=getCookie('userName');
            if(userName){
                var sceneId=e.sceneId;
                $.ajax({
                    url: "/H5GGShare/pickBoluomeCouponWeb",
                    type: "POST",
                    dataType: "JSON",
                    data: {'sceneId':sceneId},
                    success: function (returnData){
                        //console.log(returnData)
                        if(returnData.success){
                            requestMsg(returnData.msg);
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
                window.location.href="gglogin?urlName="+urlName;
            }else{
                    if(!self.finalPrizeMask){
                        $.ajax({
                            type: 'get',
                            url: '/H5GGShare/pickUpSuperPrize',
                            data:{'activityId':activityId},
                            dataType:'JSON',
                            success: function (returnData) {
                                console.log(returnData)
                                if(returnData.success){
                                    //requestMsg(returnData.msg);
                                    $('.mask').css('display','block');
                                    $('.alertFinalPrize').css('display','block');
                                    for(var j=0;j<self.content.itemsList.length;j++){
                                        num=self.content.itemsList[j].num;
                                        if(num==0){
                                            return ""
                                        }else{
                                            $('.card').eq(j).find('.num').html('x'+(num-1));
                                            if(num-1==0){
                                                $('.card').eq(j).find('.gray').css('display','block');
                                                $('.card').eq(j).find('.num').css('display','none');
                                                self.finalPrizeMask=true;
                                            }else if(num-1==1){
                                                $('.card').eq(j).find('.num').css('display','none');
                                            }
                                        }
                                    }
                                }else{
                                    requestMsg(returnData.msg);
                                }
                            },
                            error: function(){
                                requestMsg("请求失败");
                            }
                        })
                    }
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