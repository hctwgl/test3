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


//获取数据
$(function () {
    $.ajax({
        type: 'get',
        url: "/H5GGShare/listBank",
        dataType: 'JSON',
        data: {
            activityId: 1
        },
        success: function (data) {
            if (data.success) {
                var bankList = data.data.bankList;
                var html = "";
                for (var i = 0; i < bankList.length; i++) {
                    html += ['<li class="list">',
                        '<span class="list-left">' + bankList[i].userName + '</span>',
                        '<span class="list-center">' + bankList[i].totalRebate + '</span>',
                        '<span class="list-right">' + bankList[i].inviteRebate + '</span>',
                        '</li>'
                    ].join("");
                }
                $('.numberList').html(html)
            }
        }
    })
})