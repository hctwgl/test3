/* 'use strict';
var activityId = getUrl("activityId");//获取活动Id
var userName=getCookie('userName');//获取用户名 */

//获取页面名称传到登录页
var currentUrl = window.location.href;
//var currentUrl = "http://192.168.96.210/fanbei-web/activity/ggdemand?loginSource=S&activityId=1&userName=15839790051&itemsId=3&from=singlemessage&isappinstalled=1";
var index=currentUrl.lastIndexOf('/');
var index01=currentUrl.indexOf("?");
var urlName=currentUrl.slice(index+1,index01);
var str=currentUrl.substring(index01+1);//获取?后面的字符串
var arr=[];
arr=str.split("&");//获取?后面以&分隔的字符串
var itemsId=arr[3].slice(arr[3].indexOf("=")+1);//获取arr数组里面的具体值
var userName=arr[2].slice(arr[2].indexOf("=")+1);
var activityId=arr[1].slice(arr[1].indexOf("=")+1);
console.log(itemsId)
console.log(userName)
console.log(activityId)



//获取数据
$(function(){

     $.ajax({
                url: "/H5GGShare/ggAskForItems",
                type: 'GET',
                dataType: 'JSON',
                data: {
                    itemsId:itemsId,
                    friendName:userName//friendName==userName
                },
                success: function (data) {
                    console.log(data);
                    
                    if(data.success){
                        var light="";//点亮人数
                        var pic="";//banner图片
                        var friend="";//赠送者名字
                        var join="";//参与人数
                        var combo="";//e顿健康套餐
                        var detail="";//活动规则详情
                        join+="<span class='join'>"+data.data.fakeJoin+"</span>";
                        $('.join').html(join);
                        friend+='<i class="friend">'+data.data.friend+'</i>';
                        $('.friend').html(friend);
                        pic+= '<img src='+data.data.resourceDo.value+' alt="" class="banner-img">';
                        $('.banner').html(pic);
                        light+='<span class="light">'+data.data.fakeFinal+'</span>';
                        $('.light').html(light);
                        combo+='<span class="combo">'+data.data.itemsDo.name+'</span>';
                        $('.combo').html(combo);
                        detail+='<p class="ruleCont">'+data.data.description+'</p>'
                        $('.ruleCont').html(detail);

                        ScrollImgLeft();//文字轮播
                    }else{
                        requestMsg(data.msg);
                    }


                       
                }
                
            })


    //点击赠送好友
    $('.presentCard').click(function(){
             $.ajax({
                url: "/H5GGShare/sendToFriend",
                type: 'GET',
                dataType: 'JSON',
                data: {
                    itemsId:itemsId,
                    friendName:userName//friendName==userName
                   
                },
                success: function (outputData) {
                    console.log(outputData)
                    if (outputData.success) {
                            if(outputData.msg=="没有登录"){
                                window.location.href = "gglogin?word=Z"+"&&urlName=" + urlName; 
                            }else{
                                requestMsg(outputData.msg)
                            }
                    }
                }
            })
    })

    //点击我也要点亮
    $('.demandCard').click(function(){
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
                            // alert(urlName)
                            window.location.href = "gglogin?urlName=" + urlName; 
                            
                        }else{
                            var userName = outputData.data.userName;
                            window.location.href = "ggIndexShare?userName="+userName;
                        }
                    }
                }
            })
    })


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

})

//截取字符串方法
/* function getUrlParam(url) {
    var param = new Object(); 
    if (url.indexOf("?") != -1) { 
        var str = url.substr(1); 
        strs = str.split("&"); 
        for(var i = 0; i < strs.length; i ++) { 
            param[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
        } 
    } 
    return param; 
} */