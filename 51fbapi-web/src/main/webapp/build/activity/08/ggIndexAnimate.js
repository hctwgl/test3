//1.首页顶部栏动画-------------------------
//（1）:定速度
var speed = 20;
var cont = $(".cont1").html();
$(".cont2").html(cont);
//（2）:创建方法定时执行
function wordMove(){
    var left = $("#personAmount").scrollLeft();
    if(left >= $(".cont1").width()){
        left = 0;
    }else{
        left++;
    }
    $("#personAmount").scrollLeft(left);
    setTimeout("wordMove()",speed);
}
//wordMove();页面获取数据后再执行

//2.首页轮播图动画
var i = 0;
var liWidth=6.25+'rem';
var clone = $(".banner .bannerList li").first().clone();//克隆第一张图片
$(".banner .bannerList").append(clone);//复制到列表最后
var size = $(".banner .bannerList li").length;
var ulWidth=size*6.25+'rem';
$(".banner .bannerList li").width(liWidth);
$(".banner .bannerList").width(ulWidth);
for (var j = 0; j < size-1; j++) {
    $(".banner .num").append("<li></li>");
}
$(".banner .num li").first().addClass("on");
/*自动轮播*/
setInterval(function () { i++; move();},1500);
/*移动事件*/
function move() {
    if (i == size) {
        $(".banner .bannerList").css({ left: 0 });
        i = 1;
    }
    if (i == -1) {
        $(".banner .bannerList").css({ left: -(size - 1) * 6.25+'rem' });
        i = size - 2;
    }
    $(".banner .bannerList").stop().animate({ left: -i * 6.25+'rem'}, 500);

    if (i == size - 1) {
        $(".banner .num li").eq(0).addClass("on").siblings().removeClass("on");
    } else {
        $(".banner .num li").eq(i).addClass("on").siblings().removeClass("on");
    }
}

//3.赠送或索要卡片弹窗里动画
var touch = true;           //touch=true为开启触摸滑动
var slideNub;               //轮播图片数量
$(function(){
    slideNub = $(".imgList .img").size();             //获取轮播图片数量
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
})
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
