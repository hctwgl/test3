//列表信息无缝滚动
    var $uList = $("#rankingList ul");
    var timer = null;
    //触摸清空定时器
    $uList.hover(function () {
        clearInterval(timer);
    }, function () {//离开启动定时器
        timer = setInterval(function () {
            scrollList($uList);
        }, 1500);
    }).trigger("mouseleave"); //自动触发触摸事件
    //滚动动画
    function scrollList(obj) {
        //获得当前<li>的高度
        var scrollHeight = $("#rankingList ul li:first").height();
        //滚动出一个<li>的高度
        $uList.stop(200).animate({ marginTop: -scrollHeight }, 1100, function () {
            //动画结束后，将当前<ul>marginTop置为初始值0状态，再将第一个<li>拼接到末尾。
            $uList.css({ marginTop: 0 }).find("li:first").appendTo($uList);
        });
    };