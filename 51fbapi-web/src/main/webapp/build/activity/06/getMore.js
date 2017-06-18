
var subjectId=getUrl('subjectId');
var currentPage=1;
var totalSize=0;
var total;
$(function(){
  getData();
  //滚动条事件
  $(window).scroll(function() {   

     if($(document).scrollTop() >= $(document).height() - $(window).height()) {
       //alert("滚动条已经到达底部为");   
       $('.load').animate({'opacity':'1'},200,function(){
          getData();
       }) 
     } else if(totalSize==total){
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
            type:'POST',
            success:function(data){
                  var List=data.data.subjectGoodsList;
                  total=data.data.totalCount;
                  console.log(data)
                  for(var i=0;i<List.length;i++){
                    var str;
                    var type=List[i].goodsType;
                    if(type==0){
                      str='<li><div class="productImg"><img src="'+List[i].goodsIcon+'"></div><div class="productRight"><p class="productDes" style="-webkit-box-orient: vertical;">'+List[i].goodName+'</p><p class="productPrice">￥'+List[i].saleAmount+'</p><p class="fan">返</p><p class="fanPrice">￥'+List[i].rebateAmount+'</p><a class="buyNow" href="'+data.data.notifyUrl+'&params={%22goodsId%22:%22'+List[i].goodsId+'%22}'
+'"}'+'"}'+'">马上抢</a></div></li>'
                    } else{
                      str='<li><div class="productImg"><img src="'+List[i].goodsIcon+'"></div><div class="productRight"><p class="productDes" style="-webkit-box-orient: vertical;">'+List[i].goodName+'</p><p class="monthPrice"><i class="monthCorner"></i>￥'+List[i].nperMap.amount+' x '+List[i].nperMap.nper+'</p><p class="buyPrice">￥'+List[i].nperMap.totalAmount+'</p><p class="buyNow">马上抢</p></div></li>'
                    }                    
                    $('#productList').append(str);
                  }
                  totalSize+=List.length;
                  if(totalSize<20 || totalSize==total){
                    $('.load').remove();
                    $('.finish').css('opacity',1);
                  }
                  console.log(totalSize);
            },
            error:function(){
                  requestMsg("请求失败");
            }
        })
  currentPage++; 
}

