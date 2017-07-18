   var bid = getUrl("d0pmttf8U7MI/D4DoHN7Brw");
   let vm=new Vue({
    el:'#businessCode',
    data:{
        content:{}
    },
    created:function () {
        this.logData();
    },
    methods:{
        logData(){
            //获取页面初始化信息
            let self=this;
            $.ajax({
                    type: 'post',
                    url: '/fanbei-web/initTradeInfo',
                    data:{'bid':bid},
                    success:function(data) {   
                        console.log(data);
                       self.content = eval('(' + data + ')');                      
                       self.content = self.content.data;  
                      console.log(self.content)

                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
        }
    }
   })
