
var subjectId=getUrl('subjectId');
var currentPage=1;
var List;
$(function(){
  getData();
  //滚动条事件
  $(window).scroll(function() {   

     if($(document).scrollTop() >= $(document).height() - $(window).height()) {
       //alert("滚动条已经到达底部为");   
       $('.load').animate({'opacity':'1'},1000,function(){
          getData();
       }) 
     } else if(List.length==0){
                    $('.load').remove();
                    $('.finish').css('opacity',1);
                  }

  });

})


function getData(){
  $.ajax({
            url:'/fanbei-web/subjectGoodsInfo',
            data:{'subjectId':subjectId,'currentPage':currentPage},
            dataType:'JSON',
            type:'get',
            success:function(data){
                  List=data.data.subjectGoodsList;
                  //console.log(List)
                  for(var i=0;i<List.length;i++){
                    var str='<li><div class="productImg"><img src="'+List[i].goodsIcon+'"></div><div class="productRight"><p class="productDes">'+List[i].goodName+'</p><p class="productPrice">￥'+List[i].saleAmount+'</p><p class="fan">返</p><p class="fanPrice">￥'+List[i].rebateAmount+'</p><a class="buyNow" href="'+data.data.notifyUrl+'&params={%22goodsId%22:%22'+List[i].goodsId+'%22}'
+'"}'+'"}'+'">马上抢</a></div></li>'
                    $('#productList').append(str);
                  }
            },
            error:function(){
                  requestMsg("请求失败");
            }
        })
  currentPage++; 
}

