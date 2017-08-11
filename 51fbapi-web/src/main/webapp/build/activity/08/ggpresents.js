// var userItemsId = getUrl("30");

// var userName=15839790051;
// if(getInfo().userName){
//     userName=getInfo().userName;
// };

//获取页面名称传到登录页
var currentUrl = window.location.href;
var index = currentUrl.lastIndexOf('/');
var urlName = currentUrl.slice(index + 1);
console.log(urlName)

//获取数据
$(function () {

    $.ajax({
        url: "/H5GGShare/ggSendItems",
        type: 'GET',
        dataType: 'JSON',
        data: {
            userItemsId: 30,
            userName: 15839790051

        },
        success: function (data) {
            console.log(data)
            if (data.success) {
                var light = ""; //点亮人数
                var pic = ""; //banner图片
                var friend = ""; //赠送者名字
                var join = ""; //参与人数
                var combo="";//e顿健康套餐
                join += "<span class='join'>" + data.data.fakeJoin + "</span>";
                $('.join').html(join);
                friend += '<i class="friend">' + data.data.friend + '</i>';
                $('.friend').html(friend);
                pic += '<img src=' + data.data.itemsDo.iconUrl + ' alt="" class="banner-img">';
                $('.banner').html(pic);
                light += '<span class="light">' + data.data.fakeFinal + '</span>';
                $('.light').html(light);
                combo+='<span class="combo">'+data.data.itemsDo.name+'</span>';
                $('.combo').html(combo);

            } else {
                requestMsg(data.msg);
            }

        }
    })

    ScrollImgLeft();
    //文字轮播
    function ScrollImgLeft() {
        var speed = 50;
        var MyMar = null;
        var scroll_begin = document.getElementById("scroll_begin");
        var scroll_end = document.getElementById("scroll_end");
        var scroll_div = document.getElementById("scroll_div");
        scroll_end.innerHTML = scroll_begin.innerHTML;

        function Marquee() {
            if (scroll_end.offsetWidth - scroll_div.scrollLeft <= 0)
                scroll_div.scrollLeft -= scroll_begin.offsetWidth;
            else
                scroll_div.scrollLeft++;
        }
        MyMar = setInterval(Marquee, speed);
        scroll_div.onmouseover = function () {　　　　　　　
            clearInterval(MyMar);　　　　　
        }
        scroll_div.onmouseout = function () {　　　　　　　
            MyMar = setInterval(Marquee, speed);　　　　　　　　　
        }
    }

    //点击我要赠送卡片
    $('.presentCard').click(function () {
        $.ajax({
            url: "/H5GGShare/pickUpItems",
            type: 'GET',
            dataType: 'JSON',
            data: {
                userItemsId: 30,
                userName: 15839790051

            },
            success: function (outputData) {
                console.log(outputData)
                if (outputData.success) {
                    var loginUrl = "";
                    try {
                        loginUrl = outputData.data.loginUrl;
                    } catch (error) {
                        //  ignore
                    }
                    if (loginUrl != undefined && loginUrl != '') {
                        // 未登录，跳转登录界面
                        //window.location.href =loginUrl;
                        window.location.href = "gglogin?urlName=" + urlName;
                    }
                    requestMsg(outputData.msg);
                    console.log(outputData.msg);
                }
            }
        })
    })

    //点击我要索要卡片
    $('.demandCard').click(function () {
        $.ajax({
            url: "/H5GGShare/lightItems",
            type: 'GET',
            dataType: 'JSON',
            data: {
                activityId: 1
                //userName:15839790051
            },
            success: function (outputData) {
                console.log(outputData)
                if (outputData.success) {
                    var loginUrl = "";
                    try {
                        loginUrl = outputData.data.loginUrl;
                    } catch (error) {
                        // ignore
                    }
                    if (loginUrl != undefined && loginUrl != '') {
                        // 未登录，跳转登录界面
                        //window.location.href =loginUrl;
                        window.location.href = "gglogin?urlName=" + urlName;
                    }
                    //  requestMsg(outputData.msg);
                }

            }
        })
    })


})