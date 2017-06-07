
$(function(){
      //导航栏点击滑动到对应部分
      $(".menu li").each(function(index,e){
            e.index=index;
             // console.log(index);
            //注册点击事件
            $(e).click(function(){
                  // console.log("1111");
                  var that=this.index;
                  var height=$("#new").outerHeight();
                  var t=$("#new").offset().top;
                  var H=height*that+t;
                  $('body').animate({scrollTop:H+'px'},500);

            });
      });

});

//当导航栏滑动到顶部的时候 导航栏固定
$(function(){
        var t=$('#theme').height();//导航栏上部的高度
        var menu=$('.menu');//导航栏对象
        var win=$(window);//窗口对象
        var sc=$(document);//document文档对象
        win.scroll(function(){
            console.log(t);
             if(sc.scrollTop()>=t){
                  menu.addClass('fixed');

             }else{
                  menu.removeClass('fixed');

             }

        })
})

 //鼠标滑动对应导航栏高亮
 $(function(){
        var new_top=$('#new').offset().top;
        var com_top=$('#com').offset().top;
        var play_top=$('#play').offset().top;
        var ele_top=$('#ele').offset().top;
        var big_top=$('#big').offset().top;

        $(window).scroll(function(){
             var scrolH=$(this).scrollTop();
             if(scroH>=new_top){
                   $('.new').addClass('cur');
             }else if(scroH>=com_top){
                   $('.com').addClass('cur');
             }else if(scroH>=play_top){
                   $('.play').addClass('cur');
             }else if(scroH>=ele_top){
                   $('.ele').addClass('cur');
             }else if(scroH>=big_top){
                  $('.big').addClass('cur');
             }
        })

 })




