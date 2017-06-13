
var subjectId=getUrl('subjectId');
var currentPage=1;
      var vm = new Vue({
      el: '#productList',
      data: {
      	List:''
      },
      created:function(){
      	var self=this;
        $.ajax({
         	  url:'/fanbei-web/subjectGoodsInfo',
		        data:{'subjectId':subjectId,'currentPage':currentPage},
		        dataType:'JSON',
		        type:'get',
		        success:function(data){
                  self.List=data.data.subjectGoodsList;
                  console.log(self.List)
		        },
		        error:function(){
                  requestMsg("请求失败");
		        }
        })
      }
    })

