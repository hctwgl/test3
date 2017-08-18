
//3.赠送或索要卡片弹窗里动画
/*var touch = true;           //touch=true为开启触摸滑动
var slideNub;               //轮播图片数量
var domainName = domainName();
var cardRid;*/
$(function(){
    //赠送卡片
    /*$('.presentCard').click(function(){
        $.ajax({
            type: 'get',
            url: "/H5GG/sendItems",
            data:{activityId:1,userName:15839790051},
            success: function (returnData) {
                console.log(returnData)
                if(returnData.data.loginUrl){
                    location.href = returnData.data.loginUrl;
                }else{
                    $('.imgList').empty();
                    var presentCardList=returnData.data.itemsList;
                    var userItemsList=returnData.data.userItemsList;
                    var num=0;
                    var str='';
                    var itemsListRid;
                    var itemsId;
                    for(var j=0;j<presentCardList.length;j++){//判断终极大奖蒙版
                        num+=presentCardList[j].num;
                        //console.log(num)
                        if(num==0 || num==presentCardList.length){
                            requestMsg(returnData.msg)
                        }else{

                            $('.alertPresent').css('display','block');
                            $('.mask').css('display','block');
                            $('.presentTitle').css('display','block');
                            $('.sure').html('确定赠送');
                            if(presentCardList[j].num>=2){
                                str+='<div class="img" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'">'+ '<p class="num">'+(presentCardList[j].num-1)+'</p></div>';
                            }else{
                                str+='<div class="img" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"><p class="cardMask"></p>'+ '</div>';
                            }
                        }
                    }//判断终极大奖蒙版
                    $('.imgList').append(str);
                    slideNub = $(".imgList .img").size();//获取轮播图片数量
                    getData(slideNub)
                    //确定赠送
                    $('.sure').click(function(){
                        itemsListRid=$('.img.img3').attr('rid');
                        for(var i=0;i<userItemsList.length;i++){
                            itemsId=userItemsList[i].itemsId;
                            if(itemsId==itemsListRid){
                                var arr=[];
                                arr.push(userItemsList[i].rid);
                            }
                        }
                        cardRid=arr[0];
                        console.log(cardRid)
                        if(cardRid&&cardRid!=''){
                            window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"消费有返利 领取88.88元现金红包！","shareAppContent":"你的好友分享了一张卡片给你，快去查看吧~","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"'+domainName+'/activity/ggIndexShare?cardRid'+cardRid+'","isSubmit":"Y","sharePage":"ggIndexShare"}';
                        }else{
                            window.location.href="ggIndex";
                        }
                    })
                }
            },
            error: function(){
                requestMsg("请求失败");
            }
        })
    })*/
    //索要卡片
    /*$('.demandCard').click(function(){
        $.ajax({
            type: 'get',
            url: "/H5GG/askForItems",
            data:{activityId:1,userName:15839790051},
            success: function (returnData) {
                var returnData=eval('(' + returnData + ')').data;
                console.log(returnData)
                if(returnData.loginUrl){
                    location.href = returnData.loginUrl;
                }else{
                    $('.imgList').empty();
                    var presentCardList=returnData.itemsList;
                    var str='';
                    for(var j=0;j<presentCardList.length;j++){
                        $('.alertPresent').css('display','block');
                        $('.mask').css('display','block');
                        $('.demandTitle').css('display','block');
                        $('.sure').html('确定索要');
                        str+='<div class="img" rid="'+presentCardList[j].rid+'"><img src="'+presentCardList[j].iconUrl+'"></div>';
                        }
                    }
                    $('.imgList').append(str);
                    slideNub = $(".imgList .img").size();//获取轮播图片数量
                    getData(slideNub)
                    //确定索要
                    $('.sure').click(function(){
                        cardRid=$('.img.img3').attr('rid');
                        console.log(cardRid)
                        if(cardRid&&cardRid!=''){
                            window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"消费有返利 领取88.88元现金红包！","shareAppContent":"你的好友分享了一张卡片给你，快去查看吧~","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"'+domainName+'/activity/ggIndexShare?cardRid'+cardRid+'","isSubmit":"Y","sharePage":"ggIndexShare"}';
                        }else{
                            window.location.href="ggIndex";
                        }

                    })
            },
            error: function(){
                requestMsg("请求失败");
            }
        })
    })*/


})
/*function getData(slideNub){
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
    "shareAppTitle": "消费有返利 领取88.88元现金红包！",  // 分享的title
    'shareAppContent': "你的好友分享了一张卡片给你，快去查看吧~",  // 分享的内容
    "shareAppImage": "https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",  // 分享右边小图
    "shareAppUrl": domainName+"/activity/ggIndexShare?cardRid="+cardRid,  // 分享后的链接
    "isSubmit": "Y", // 是否需要向后台提交数据，Y需要，N不需要
    "sharePage": "ggIndexShare" // 分享的页面
  };
  var dataStr = JSON.stringify(dataObj);  // obj对象转换成json对象
  return dataStr;
};*/
