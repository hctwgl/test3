//索要卡片弹窗动画
var touch = true;           //touch=true为开启触摸滑动
var slideNub;               //轮播图片数量
var protocol = window.location.protocol;//域名
var host = window.location.host;
var domainName = protocol+'//'+host;
var itemsId;//索要的卡片主键
var activityId=getUrl("activityId");//获取活动Id
var userName = "";//获取用户名
if(getInfo().userName){
  userName=getInfo().userName;
};

var name;//卡片名称
$(function() {
    $('.demandCard').click(function () {
        $.ajax({
            type: 'get',
            url: "/H5GG/askForItems",
            data: {activityId: activityId},
            success: function (returnData) {
                var returnData = eval('(' + returnData + ')').data;
                //console.log(returnData)
                if (returnData.loginUrl) {
                    location.href = returnData.loginUrl;
                } else {
                    $('body').addClass('overflowChange');
                    $('html').addClass('overflowChange');
                    $('.imgList').empty();
                    $('.alertPresent').css('display', 'block');
                    $('.mask').css('display', 'block');
                    $('.demandTitle').css('display', 'block');
                    $('.sureDemand').show();
                    $('.surePresent').hide();
                    var presentCardList = returnData.itemsList;
                    var str = '';
                    for (var j = 0; j < presentCardList.length; j++) {
                        str += '<div class="img" name="'+presentCardList[j].name+'" rid="' + presentCardList[j].rid + '"><img src="' + presentCardList[j].iconUrl + '"></div>';
                    }
                }
                $('.imgList').append(str);
                slideNub = $(".imgList .img").size();//获取轮播图片数量
                getData(slideNub)
            },
            error: function () {
                requestMsg("请求失败");
            }
        })
    })

    //确定索要
    $('.sureDemand').click(function () {
        itemsId = $('.img.img3').attr('rid');
        name=$('.img.img3').attr('name');
        if (itemsId && itemsId != '') {
            var dat='{"shareAppTitle":"消费有返利 领取51元大奖！","shareAppContent":"你的好友向你索要一张'+name+'卡片，快赠送给他/她吧~","shareAppImage":"http://f.51fanbei.com/h5/app/activity/08/ggShare.png","shareAppUrl":"' + domainName + '/fanbei-web/activity/ggdemand?loginSource=S&activityId='+activityId+'&userName='+userName+'&itemsId=' + itemsId + '","isSubmit":"Y","sharePage":"ggdemand"}';
            var base64 = BASE64.encoder(dat);
            //console.log(base64)
            window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params='+base64;
        } else {
            requestMsg("索要失败")
        }
    })
    $('.close').click(function(){
        $('body').removeClass('overflowChange');
        $('html').removeClass('overflowChange');
    })
    $('.mask').click(function(){
        $('body').removeClass('overflowChange');
        $('html').removeClass('overflowChange');
    })

})
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
            left();
            _end=0;
        }else if(_end > 100){
            right();
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
        'shareAppContent': "你的好友向你索要一张"+name+"卡片，快赠送给他/她吧~",  // 分享的内容
        "shareAppImage": "http://f.51fanbei.com/h5/app/activity/08/ggShare.png",  // 分享右边小图
        "shareAppUrl": domainName+"/fanbei-web/activity/ggdemand?loginSource=S&activityId="+activityId+"&userName="+userName+"&itemsId="+itemsId,  // 分享后的链接
        "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
        "sharePage": "ggdemand" // 分享的页面
    };
    var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
    return dataStr
}