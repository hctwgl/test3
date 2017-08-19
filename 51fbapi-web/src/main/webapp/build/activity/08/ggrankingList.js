//获取页面名称传到登录页
var currentUrl = window.location.href;
//var currentUrl = "http://ggrankingList?activityId=1";
var index01=currentUrl.indexOf("?");
var str=currentUrl.substring(index01+1);//获取?后面的字符串
var arr=[];
arr=str.split("&");//获取?后面以&分隔的字符串
var activityId=arr[0].slice(arr[0].indexOf("=")+1);//获取arr数组里面的具体值
console.log(activityId) 

/*  //列表信息无缝滚动
var $uList = $(".container ul");
var timer = null;

//触摸情动定时器
$uList.hover(function () {
    clearInterval(timer);
}, function () { //离开启动定时器
    timer = setInterval(function () {
        scrollList($uList);
    }, 1000);
}).trigger("mouseleave"); //自动触发触摸事件

//滚动动画
function scrollList(obj) {
    //获得当前<li>的高度
    var scrollHeight = $(".container ul li:first").height();
    //滚动出一个<li>的高度
    $uList.animate({
        marginTop: -scrollHeight
    }, 1100,"linear", function () {
        //动画结束后，将当前<ul>marginTop置为初始值0状态，再将第一个<li>拼接到末尾。
        $uList.css({
            marginTop: 0
        }).find("li:first").appendTo($uList);
    });
};  */


//获取数据
$(function () {
    $.ajax({
        type: 'get',
        url: "/H5GGShare/listRank",
        dataType: 'JSON',
        data: {
            activityId:activityId
        },
        success: function (data) {
            if (data.success) {
                var rankList = data.data.rankList;
                var html = "";
                var Number="";
                //从rankingList这个数组
                for (var i = 0; i < rankList.length; i++) {
                    html += ['<li class="list">',
                        '<span class="list-left">' + rankList[i].userName + '</span>',
                        '<span class="list-center">' + rankList[i].totalRebate + '</span>',
                        '<span class="list-right">' + rankList[i].inviteRebate + '</span>',
                        '</li>'
                    ].join("");
                }
                $('.numberList').html(html)
                console.log(data);
                //从data.data这个对象 获取到高额返利的人数
                Number+="<i class='number'>"+data.data.rebateNumber+"</i>";
                console.log(Number);
                $('.number').html(Number);
               
            }
        }
    })
})