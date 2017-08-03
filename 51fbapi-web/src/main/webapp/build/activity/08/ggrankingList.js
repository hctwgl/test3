//  //列表信息无缝滚动
// var $uList = $(".container ul");
// var timer = null;
// //触摸情动定时器
// $uList.hover(function () {
//     clearInterval(timer);
// }, function () { //离开启动定时器
//     timer = setInterval(function () {
//         scrollList($uList);
//     }, 1101);
// }).trigger("mouseleave"); //自动触发触摸事件
// //滚动动画
// function scrollList(obj) {
//     //获得当前<li>的高度
//     var scrollHeight = $(".container ul li:first").height();
//     //滚动出一个<li>的高度
//     $uList.animate({
//         marginTop: -scrollHeight
//     }, 1100,"linear", function () {
//         //动画结束后，将当前<ul>marginTop置为初始值0状态，再将第一个<li>拼接到末尾。
//         $uList.css({
//             marginTop: 0
//         }).find("li:first").appendTo($uList);
//     });
// };

(function ($) {
    $.fn.scrollTop = function (options) {
        var defaults = {
            speed: 30
        }
        var opts = $.extend(defaults, options);
        this.each(function () {
            var $timer;
            var scroll_top = 0;
            var obj = $(this);
            var $height = obj.find("ul").height();
            obj.find("ul").clone().appendTo(obj);
            obj.hover(function () {
                clearInterval($timer);
            }, function () {
                $timer = setInterval(function () {
                    scroll_top++;
                    if (scroll_top > $height) {
                        scroll_top = 0;
                    }
                    obj.find("ul").first().css("margin-top", -scroll_top);
                }, opts.speed);
            }).trigger("mouseleave");
        })
    }
})(jQuery)


$(function () {
    $(".container").scrollTop({
        speed: 300 //数值越大 速度越慢
    });
})
