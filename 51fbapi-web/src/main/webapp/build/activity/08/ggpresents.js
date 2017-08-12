
//获取页面名称传到登录页
var currentUrl = window.location.href;
var index=currentUrl.lastIndexOf('/');
var urlName=currentUrl.slice(index+1);

//var currentUrl = "htttp://192.168.96.210/fanbei-web/activity/ggpresents?loginSource=Z&activityId=1&userItemsId=32&from=singlemessage&isappinstalled=1";
var index01=currentUrl.indexOf("?");
var str=currentUrl.substring(index01+1);//获取?后面的字符串
var arr=[];
arr=str.split("&");//获取?后面以&分隔的字符串
var activityId=arr[1].slice(arr[1].indexOf("=")+1);//获取arr数组里面的具体值
var userItemsId=arr[2].slice(arr[2].indexOf("=")+1);
console.log(activityId)
console.log(userItemsId)
$(function () {

    $.ajax({
        url: "/H5GGShare/ggSendItems",
        type: 'GET',
        dataType: 'JSON',
        data: {
            userItemsId:userItemsId
        },
        success: function (data) {
            console.log(data)
            if (data.success) {
                var light = ""; //点亮人数
                var pic = ""; //banner图片
                var friend = ""; //赠送者名字
                var join = ""; //参与人数
                var combo="";//e顿健康套餐
                var detail="";//活动规则详情
                join += "<span class='join'>" + data.data.fakeJoin + "</span>";
                $('.join').html(join);
                friend += '<i class="friend">' + data.data.friend + '</i>';
                $('.friend').html(friend);
                pic += '<img src=' + data.data.resourceDo.value + ' alt="" class="banner-img">';
                $('.banner').html(pic);
                light += '<span class="light">' + data.data.fakeFinal + '</span>';
                $('.light').html(light);
                combo+='<span class="combo">'+data.data.itemsDo.name+'</span>';
                $('.combo').html(combo);
                detail+='<p class="ruleCont">'+data.data.description+'</p>'
                $('.ruleCont').html(detail);

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

    //点击规则弹框 弹出规则详情
    $('.rules').click(function(){
        $('.mask').show();
        $('.alertRule').show();
    })
    $('.mask').click(function(){
        $('.mask').hide();
        $('.alertRule').hide();
    })

    //点击我要赠送卡片(领走卡片)
    $('.presentCard').click(function () {
        var word=$(this).html();
        $.ajax({
            url: "/H5GGShare/pickUpItems",
            type: 'GET',
            dataType: 'JSON',
            data: {
                userItemsId:userItemsId

            },
            success: function (outputData) {
                console.log(outputData)
                if (outputData.success) {
                    if(outputData.msg=="没有登录"){
                       window.location.href = "gglogin?word=Z"+"&&urlName=" + urlName;
                    }else{
    
                     requestMsg(outputData.msg);
                      console.log(outputData.msg)
                    } 
                }
            }
        })
    })

    //点击我要索要卡片(点亮)
    $('.demandCard').click(function () {
        var word=$(this).html();
        $.ajax({
            url: "/H5GGShare/lightItems",
            type: 'GET',
            dataType: 'JSON',
            data: {
                activityId:activityId
            },
            success: function (outputData) {
                console.log(outputData)
                if (outputData.success) {
                    if(outputData.msg=="没有登录"){
                       window.location.href = "gglogin?urlName=" + urlName; 
                    }else{
                       var userName = outputData.data.userName;
                       window.location.href = "ggIndexShare?userName="+userName;
                    }
                }
                
            }
        })
    })


})

//截取字符串方法
function getUrlParam(url) {
    var param = new Object(); 
    if (url.indexOf("?") != -1) { 
        var str = url.substr(1); 
        var strs=[];
        strs = str.split("&"); 
        for(var i = 0; i < strs.length; i ++) { 
            param[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
        } 
    } 
    return param; 

}

