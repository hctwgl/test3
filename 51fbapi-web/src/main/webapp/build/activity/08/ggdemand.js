

//获取数据
$(function(){

     $.ajax({
                url: "/H5GGShare/ggAskForItems",
                type: 'GET',
                dataType: 'JSON',
                data: {
                    itemsId:1,
                    userName:15839790051

                },
                success: function (data) {
                    console.log(data)
                    if(data.success){

                        var light="";//点亮人数
                        var pic="";//banner图片
                        var friend="";//赠送者名字
                        var join="";//参与人数
                        join+="<span class='join'>"+data.data.fakeJoin+"</span>";
                        $('.join').html(join);
                        friend+='<i class="friend">'+data.data.friend+'</i>';
                        $('.friend').html(friend);
                        pic+= '<img src='+data.data.itemsDo.iconUrl+' alt="" class="banner-img">';
                        $('.banner').html(pic);
                        light+='<span class="light">'+data.data.fakeFinal+'</span>';
                        $('.light').html(light);

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
                    itemsId:1,
                    friendId:68885
                   
                },
                success: function (data) {
                    console.log(data)
                    if(data.success){
                        var loginUrl = data.loginUrl;
                        alert(loginUrl);
                        if(loginUrl != undefined && loginUrl != '') {
                            window.location.href = loginUrl;
                           
                        } else {

                        }
                    }else{
                        requestMsg(data.msg);
                    }
                       
                }
            })
    })

    //点击我要索要卡片
    $('.demandCard').click(function(){
             $.ajax({
                url: "/H5GGShare/lightItems",
                type: 'GET',
                dataType: 'JSON',
                data: {
                   activityId:1
                },
                success: function (data) {
                    console.log(data)
                    if(data.success){
                        var loginUrl = data.loginUrl;
                        if(loginUrl != undefined && loginUrl != '') {
                            window.location.href = loginUrl;
                           
                        } else {

                        }
                    }else{
                        requestMsg(data.msg);
                    }
                       
                }
            })
    })



})