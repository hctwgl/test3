/*
* @Author: Jacky
* @Date:   2017-08-08 14:11:41
*/

let finished = 0;//防止多次请求ajax

// 导航tab切换
$(function(){
    // 点击导航事件
    $(".nav li").on('click',function(e){
        e.preventDefault();
        var k = $(this).index();
        $(this).addClass("current");
        $(this).siblings().removeClass("current");
        $(this).find('i').addClass("bg");
        $(this).siblings().find('i').removeClass("bg");

        $(".main_wrap").find("ul").eq(k).removeClass("hide");
        $(".main_wrap").find('ul').eq(k).siblings().addClass("hide");
    });

    $("#firstList li").on('click',function (e) {
        e.preventDefault();
        var k = $(this).index();
        console.log(k);
        $(this).find("i").toggleClass("arrowUp");
        $("#firstList").find(".itemContent1").eq(k).toggleClass("hide");
    })

    $("#secondList li").on('click',function (e) {
        e.preventDefault();
        var k = $(this).index();
        console.log(k);
        $(this).find("i").toggleClass("arrowUp");
        $("#secondList").find(".itemContent2").eq(k).toggleClass("hide");
    })

    $(".tel").click(function () {
        window.location.href="tel://4000025151"
    })
    $(".kf").click(function () {
        window.location.href="/fanbei-web/opennative?name=APP_CONTACT_CUSTOMER"
    })
});
