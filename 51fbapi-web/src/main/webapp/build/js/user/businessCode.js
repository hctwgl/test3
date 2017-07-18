   var bid = getUrl("bid");
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
                    data:{bid:'d0pmtf8U7Ml/D4DoHN7Brw=='},
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
