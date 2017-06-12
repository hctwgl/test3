
$(function(){
      //导航栏点击滑动到对应部分
      $(".menu li").each(function(index,e){
            e.index = index;
             // console.log(index);
            //注册点击事件
            $(e).click(function(){
                  var that=this.index;
                  var height=$("#new").outerHeight();
                  var t= $("#new").offset().top;
                  var H= height*that+t;
                  $('body').animate({scrollTop:H+'px'},500);
            });
      });

      var theme = $("#theme");//导航栏上部的距离
      var menu=$('#menu');//导航栏对象
      var win=$(window);//窗口对象
      var sc=$('body');//document文档对象
      

      var new_top=$('#new').offset().top;
      var com_top=$('#com').offset().top;
      var play_top=$('#play').offset().top;
      var ele_top=$('#ele').offset().top;
      var big_top=$('#big').offset().top;
      var top1 ;

      win.scroll(function(){
          //当导航栏滑动到顶部的时候 导航栏固定
          top1 = win.scrollTop();
           if(top1>=new_top){
                menu.addClass('fixed');
           }else{
                menu.removeClass('fixed');
           }
           //导航条滚动 导航栏高亮
           if(top1>=big_top){
            console.log(1);
              $('#m_big').find('a').addClass('cur').parent('li').siblings().find('a').removeClass('cur');
              return;
           }else if(top1>=ele_top){
              console.log(2)
              $('#m_ele').find('a').addClass('cur').parent('li').siblings().find('a').removeClass('cur');
            return;
           }else if(top1>=play_top){
            console.log(3);
              $('#m_play').find('a').addClass('cur').parent('li').siblings().find('a').removeClass('cur');
            return;
           }else if(top1>=com_top){
            console.log(4)
              $('#m_com').find('a').addClass('cur').parent('li').siblings().find('a').removeClass('cur');
            return;
           }else if(top1>=new_top){
            console.log(5)
              $('#m_new').find('a').addClass('cur').parent('li').siblings().find('a').removeClass('cur');
              return;
           }

      })

       //导航栏高亮
          menu.find('a').click(function(){
             $(this).addClass('cur').siblings().removeClass('cur');
          })

      //ajax后台数据获取
      $.ajax({
          url:'/fanbei-web/partActivityInfo',
          dataType:'json',
          type:'post',
          data:{'modelId':'15958192236'},
          success:function(data){
                console.log(data);   
          }
      })




});




