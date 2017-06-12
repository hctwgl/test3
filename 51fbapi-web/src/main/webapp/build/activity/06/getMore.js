var paramUrl=window.location.href;
alert(paramUrl)
var modelId='';
var subjectId='';
var currentPage=1;
$(function(){
	
    var vm = new Vue({
      el: '#productList',
      data: {
      	List:''
      },
      created:function(){
         $.ajax({
         	url:'/fanbei-web/partActivityInfo',
		    data:{'modelId':modelId,'subjectId':subjectId,'currentPage':currentPage},
		    dataType:'JSON',
		    type:'POST',
		    success:function(data){
                console.log(data)
                this.List=data;
		    },
		    error:function(){
                requestMsg("请求失败");
		    }
         })
      }

    })
    
})
