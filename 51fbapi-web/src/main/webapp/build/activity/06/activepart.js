
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

      //导航栏高亮
          menu.find('a').click(function(){
             $(this).addClass('cur').siblings().removeClass('cur');
             console.log(22222)
          })

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
              console.log(1)
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


      // 精品推荐Model
      function addModel(goodsList) {
      
        var html = '';
        for (var i = 0; i < goodsList.length; i++) {
      
          var saleAmount = toDecimal2(goodsList[i].saleAmount);  // 售价
          var rebateAmount = toDecimal2(goodsList[i].rebateAmount);  // 返利
          var goodsUrl = goodsList[i].goodsUrl;  // 淘宝商品链接
      
          var urlHost = window.location.host;
          var goodsUrl = urlHost + '&params={"goodsId":"'+goodsList[i].goodsId+'"}';
      
          html +='<li class="boot-1">'
                      +'<a href='+goodsUrl+'>'
                          +'<img src='+goodsList[i].goodsIcon+'>'
                          +'<div class="b-1-r">'
                              +'<p class="discount">'+goodsList[i].goodName+'</p>'
                              +'<p class="price">'
                                  +'<u>￥'+saleAmount+'</u>'
                                  +'<span><i>返</i>￥'+rebateAmount+'</span>'
                              +'</p>'
                              +'<p class="back">'
                                  +'<u class="return">返￥'+'</u>'+rebateAmount+'<u>￥'+'</u>1.20*2'
                              +'</p>'
                              +'<p class="rob">马上抢</p>'
                          +'</div>'
                      +'</a>'
                +'</li>';
          }
          return html;
      };


      //ajax后台数据获取
      $.ajax({
          url:'/fanbei-web/partActivityInfo',
          dataType:'json',
          type:'post',
          data:{'modelId':'33'},
          success: function(returnData){
              console.log(returnData);
              if (returnData.success) {
                // 会场链接
                let mainActivityList = returnData.data["mainActivityList"];
                for (var i = 0; i < mainActivityList.length; i++) {
                  let activityUrl = mainActivityList[i].activityUrl;
                  let sort = mainActivityList[i].sort;
        
                  let dataType = $("#selectedHome li").attr("data-type");
                  if (dataType = sort) {
                    $("#selectedHome li").eq(i).find('a').attr("href",activityUrl);
                  } else {
                    requestMsg("会场不存在");
                  }
                }
        
                // 精品title
                let goodTitle = returnData.data["goodTitle"];
                $("#goodTitle").append(goodTitle);
        
                // 精品列表
                let qualityGoodsList = returnData.data["qualityGoodsList"];
                $("#qualityGoodsList").append(addModel(qualityGoodsList));
        
                finished=0
        
              }else{
                requestMsg(returnData.msg);
              }
         },
        error: function(){
          requestMsg("请求失败");
        }
    });


    });
  







