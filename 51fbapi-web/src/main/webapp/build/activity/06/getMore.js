$(function(){
	$.ajax({
		url:'/fanbei-web/partActivityInfo',
		data:'{'modelId':33}',
		dataType:'JSON',
		type:'POST',
		success:function(data){
            console.log(data)
		},
		error:function(){
            requestMsg("请求失败");
		}
	})

})

new Vue({
  el: '#productList',
  data: {},
  methods:{
    
  }

})