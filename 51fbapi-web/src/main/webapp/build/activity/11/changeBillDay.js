let startTime='',endTime='';
//增加开始日期
function addStartDate(){
    // dtpicker组件适用于弹出日期选择器
    var dtpicker = new mui.DtPicker({
        type: "date", //设置日历初始视图模式
        labels: ['年', '月', '日'] //设置默认标签区域提示语
    });

    dtpicker.show(function(items) {
        startTime=items.y.value + "-"+ items.m.value + "-"+ items.d.value;
        var dateStr = items.y.value + "-" + items.m.value + "-" + items.d.value + " ";
        $('.startTime').text(dateStr);
        dtpicker.dispose();
    })
}

//增加结束日期
/* function addEndDate(){
    // dtpicker组件适用于弹出日期选择器
    var dtpicker = new mui.DtPicker({
        type: "date", //设置日历初始视图模式
        labels: ['年', '月', '日'] //设置默认标签区域提示语
    });

    dtpicker.show(function(items) {
        endTime = items.y.value + "-" + items.m.value + "-" + items.d.value;
        var dateStr = items.y.value + "-" + items.m.value + "-" + items.d.value + " ";
        $('.endTime').text(dateStr);
        dtpicker.dispose();
    })
} */

function addEndDate(){
    // dtpicker组件适用于弹出日期选择器
    var dtpicker = new mui.DtPicker({
        type: "Int", //设置日历初始视图模式
        picker:2
    });

    dtpicker.show(function(items) {
        endTime = items.y.value + "-" + items.m.value + "-" + items.d.value;
        var dateStr = items.y.value + "-" + items.m.value + "-" + items.d.value + " ";
        $('.endTime').text(dateStr);
        dtpicker.dispose();
    })
}


//选择开始结束时间 
$(".dateOne").click(function(){
     addStartDate(); 
});

 $(".dateTwo").click(function(){
    addEndDate();
}); 

//返回
$('.return').click(function(){
    window.location.href=paraArr+".html";
});
//完成
$('.finish').click(function(){
    if(Number(endTime)>0&&Number(endTime)<Number(startTime)){
        requestMsg('结束时间不能小于开始时间')
    }else{
        window.location.href=paraArr+".html?startTime="+startTime+'&endTime='+endTime+'&stateStatus='+stateStatus;
    }
});


/* let vm = new Vue({
    el:'#changbillDay',
    data:{},
    created:{},
    methods:{

    }
}) */