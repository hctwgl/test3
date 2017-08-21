var userName = "";//获取用户名
if(getInfo().userName){
    userName=getInfo().userName;
};
//赠送卡片弹窗动画
var touch = true;           //touch=true为开启触摸滑动
var slideNub;               //轮播图片数量
// var domainName = domainName();//域名
var protocol = window.location.protocol;
var host = window.location.host;
var domainName = protocol+'//'+host;
var cardRid;//赠送的卡片主键
var activityId=getUrl("activityId");//获取活动Id
var name;//卡片名称
var itemsListRid;
var itemsId;
var userItemsList;
var numClick01;
var numClick02;
$(function(){
    $('.presentCard').click(function(){
            $.ajax({
                type: 'get',
                url: "/H5GG/sendItems",
                data:{'activityId':activityId},
                success: function (returnData) {
                   returnData = eval('(' + returnData + ')');
                    console.log(returnData)
                    if(returnData.data.loginUrl){
                        location.href = returnData.data.loginUrl;
                    }else{
                        if($('.presentCard').attr('present')=='Y'){
                            $('body').addClass('overflowChange');
                            $('html').addClass('overflowChange');
                            $('.imgList').empty();
                            $('.alertPresent').css('display','block');
                            $('.mask').css('display','block');
                            $('.presentTitle').css('display','block');
                            $('.surePresent').show();
                            $('.sureDemand').hide();
                            var presentCardList=returnData.data.itemsList;
                            userItemsList=returnData.data.userItemsList;
                            var str='';

                            for(var j=0;j<presentCardList.length;j++){//判断终极大奖蒙版
                                if(presentCardList[j].num>=2){
                                    str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><img class="garyCard" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><img class="cardBlur" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><p class="num">x'+(presentCardList[j].num-1)+'</p>'+ '</div>';
                                }else{
                                    str+='<div class="img" numClick="'+presentCardList[j].num+'" name="'+presentCardList[j].name+'" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><img class="garyCard" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png"><img class="cardBlur" src="http://f.51fanbei.com/h5/app/activity/08/gg0000'+presentCardList[j].rid+'.png">'+ '</div>';
                                }

                                //console.log(num)
                            }//判断终极大奖蒙版
                            $('.imgList').append(str);
                            slideNub = $(".imgList .img").size();//获取轮播图片数量
                            getData(slideNub);
                            numClick02=$('.img.img3').attr('numClick');
                            if(numClick02>=2){
                                $('.img.img3').find('.garyCard').css('display','none');
                            }
                            $('.presentTitle span').eq(1).html($('.img.img3').attr('name'));
                            $('.img').click(function(){
                                var index=$(this).index();
                                $('.presentTitle span').eq(1).html($('.img').eq(index).attr('name'));
                                numClick01=$(this).attr('numClick');
                                if(numClick01<2){
                                    $('.surePresent').css('background','#B3B3B3');
                                }else{
                                    $('.surePresent').css('background','#fb9659');
                                    $(this).find('.garyCard').css('display','none');
                                }
                            })
                        }else{
                            requestMsg("抱歉，你暂时没有可以赠送的卡片")
                        }

                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            })
    })

    //确定赠送
    $('.surePresent').click(function(){
        var arr=[];
        name = $('.img.img3').attr('name');
        itemsListRid=$('.img.img3').attr('rid');
        numClick02=$('.img.img3').attr('numClick');
        if(numClick02<2){
            $(this).css('background','#B3B3B3');
        } else{
            for(var i=0;i<userItemsList.length;i++){
                itemsId=userItemsList[i].itemsId;
                if(itemsId==itemsListRid){
                    arr.push(userItemsList[i].rid);
                }
            }
            cardRid=arr[0];
            if(cardRid&&cardRid!=''){
                $.ajax({
                    type: 'get',
                    url: "/H5GG/doSendItems",
                    data:{'userItemsId':cardRid},
                    success: function (returnData) {
                        returnData = eval('(' + returnData + ')');
                        if(returnData.success){
                            var dat='{"shareAppTitle":"消费有返利 领取51元大奖！","shareAppContent":"你的好友赠送了一张'+name+'卡片给你，快领走吧~","shareAppImage":"http://f.51fanbei.com/h5/app/activity/08/ggShare.png","shareAppUrl":"'+domainName+'/fanbei-web/activity/ggpresents?loginSource=Z&userName='+userName+'&activityId='+activityId+'&userItemsId='+cardRid+'","isSubmit":"Y","sharePage":"ggpresents"}';
                            var base64 = BASE64.encoder(dat);
                            //console.log(base64)
                            window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params='+base64;
                        }
                    },
                    error:function(){
                        requestMsg('请求失败');
                    }
                })
            }
        }

    })
    $('.mask').click(function(){
        $('body').removeClass('overflowChange');
        $('html').removeClass('overflowChange');
        $('.surePresent').css('background','#fb9659');
    })
});

function getData(slideNub){
    for(var i=0;i<slideNub;i++){
        $(".imgList .img:eq("+i+")").attr("data-slide-imgId",i);
    }
    //根据轮播图片数量设定图片位置对应的class
    if(slideNub==1){
        for(var i=0;i<slideNub;i++){
            $(".imgList .img:eq("+i+")").addClass("img3");
        }
    }
    if(slideNub==2){
        for(var i=0;i<slideNub;i++){
            $(".imgList .img:eq("+i+")").addClass("img"+(i+3));
        }
    }
    if(slideNub==3){
        for(var i=0;i<slideNub;i++){
            $(".imgList .img:eq("+i+")").addClass("img"+(i+2));
        }
    }
    if(slideNub>3&&slideNub<6){
        for(var i=0;i<slideNub;i++){
            $(".imgList .img:eq("+i+")").addClass("img"+(i+1));
        }
    }
    if(slideNub>=6){
        for(var i=0;i<slideNub;i++){
            if(i<5){
                $(".imgList .img:eq("+i+")").addClass("img"+(i+1));
            }else{
                $(".imgList .img:eq("+i+")").addClass("img5");
            }
        }
    }
    if(touch){
        k_touch();
    }
    imgClickFy();
}
//右滑动
function right(){
    var fy = new Array();
    for(var i=0;i<slideNub;i++){
        fy[i]=$(".imgList .img[data-slide-imgId="+i+"]").attr("class");
    }
    for(var i=0;i<slideNub;i++){
        if(i==0){
            $(".imgList .img[data-slide-imgId="+i+"]").attr("class",fy[slideNub-1]);
        }else{
            $(".imgList .img[data-slide-imgId="+i+"]").attr("class",fy[i-1]);
        }
    }
    imgClickFy();
}
//左滑动
function left(){

    var fy = new Array();
    for(var i=0;i<slideNub;i++){
        fy[i]=$(".imgList .img[data-slide-imgId="+i+"]").attr("class");
    }
    for(var i=0;i<slideNub;i++){
        if(i==(slideNub-1)){
            $(".imgList .img[data-slide-imgId="+i+"]").attr("class",fy[0]);
        }else{
            $(".imgList .img[data-slide-imgId="+i+"]").attr("class",fy[i+1]);
        }
    }
    imgClickFy();
}
//轮播图片左右图片点击翻页
function imgClickFy(){
    $(".imgList .img").removeAttr("onclick");
    $(".imgList .img2").attr("onclick","left()");
    $(".imgList .img4").attr("onclick","right()");
}
//触摸滑动模块
function k_touch() {
    var _start = 0, _end = 0, _content = document.getElementById("slide");
    _content.addEventListener("touchstart", touchStart, false);
    _content.addEventListener("touchmove", touchMove, false);
    _content.addEventListener("touchend", touchEnd, false);
    function touchStart(event) {
        var touch = event.targetTouches[0];
        _start = touch.pageX;
    }
    function touchMove(event) {
        var touch = event.targetTouches[0];
        _end = (_start - touch.pageX);
    }
    function touchEnd(event) {
        if (_end < -100) {
            //left();
            _end=0;
        }else if(_end > 100){
            //right();
            _end=0;
        }
    }
}

// app调用web的方法
function alaShareData(){
    var dataObj = { // 分享内容
        "appLogin": "Y", // 是否需要登录，Y需要，N不需要
        "type": "share", // 此页面的类型
        "shareAppTitle": "消费有返利 领取51元大奖！",  // 分享的title
        'shareAppContent': "你的好友赠送了一张"+name+"卡片给你，快领走吧~",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/08/ggShare.png",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggpresents?loginSource=Z&userName="+userName+"&activityId="+activityId+"&userItemsId="+cardRid,  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggpresents" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr;
};

