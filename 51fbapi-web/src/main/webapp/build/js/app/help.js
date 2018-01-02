/*
* @Author: Jacky
* @Date:   2017-08-08 14:11:41
*/

let finished = 0;//防止多次请求ajax

/**
 * 
 * @param {*} tab [first, second, third, fourth]
 * @param {*} li 
 */
let gettab = () => {
    return getUrl('tab')
}
let getlis = () => {
    return getUrl('lis').split(',').map((a)=>{return a.trim()})
}
let initCollapse = (tab, lis)=> {
    if(tab) {
        let k = $('#'+tab).index();
        $('#'+tab).addClass('current')
        $('#'+tab).siblings().removeClass("current");
        $('#'+tab).find('i').addClass("bg");
        $('#'+tab).siblings().find('i').removeClass("bg");

        $(".main_wrap").find("ul").eq(k).removeClass("hide");
        $(".main_wrap").find('ul').eq(k).siblings().addClass("hide");
    }
    
    if(lis.length) {
        lis.forEach((a)=>{
            let li = $('.main_wrap li').filter((b,k)=>{
                return $(k).data().tit === a
            })
            $(li).find("i").toggleClass("arrowUp");
            $(li).find('.itemContent').toggleClass("hide");
        })
    }

    // var str = ''
    // $('.main_wrap #fourthList li').each((index, item)=>{
    //     str += `${$(item).find('.title').text().trim()} ${$(item).data().tit}
    //     `
    // })
    // console.log(str)
}

// 导航tab切换
$(function(){
    initCollapse(gettab(), getlis())

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

    $("#thirdList li").on('click',function (e) {
        e.preventDefault();
        var k = $(this).index();
        console.log(k);
        $(this).find("i").toggleClass("arrowUp");
        $("#thirdList").find(".itemContent3").eq(k).toggleClass("hide");
    })

    $("#fourthList li").on('click',function (e) {
        e.preventDefault();
        var k = $(this).index();
        console.log(k);
        $(this).find("i").toggleClass("arrowUp");
        $("#fourthList").find(".itemContent4").eq(k).toggleClass("hide");
    })

    $(".tel").click(function () {
        window.location.href="tel://4000025151"
    })
    $(".kf").click(function () {
        window.location.href="/fanbei-web/opennative?name=APP_CONTACT_CUSTOMER"
    })
});
