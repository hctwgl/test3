

var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

//获取数据
$(function(){

     $.ajax({
                url: "/H5GGShare/ggSendItems",
                type: 'GET',
                dataType: 'JSON',
                data: {
                    

                },
                success: function (data) {
                    console.log(data)
                    if(data.success){
                    //  window.location.href ="ggIndexShare";
                    }else{
                        requestMsg(data.msg);
                    }
                       
                }
            })


    //点击我要赠送卡片
    $('.presentCard').click(function(){
             $.ajax({
                url: "/H5GGShare/pickUpItems",
                type: 'GET',
                dataType: 'JSON',
                data: {
                   
                },
                success: function (data) {
                    console.log(data)
                    if(data.success){
                    //  window.location.href ="ggIndexShare";
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
                    //  window.location.href ="ggIndexShare";
                    }else{
                        requestMsg(data.msg);
                    }
                       
                }
            })
    })



})


