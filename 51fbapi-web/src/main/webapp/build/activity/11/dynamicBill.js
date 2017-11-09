let vm = new Vue({
    el:'#dynamicBill',
    data:{
        content:'',
    },
    created:function(){
        this.logData();
    },
    methods:{
        logData(){
            let self=this;
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getUserOutDay',
                success:function(data){
                    self.content = eval('(' + data + ')');
                    console.log(self.content);


                    if(self.content.success==false){
                        // window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
                    }
                     
                    
                }
                    
                
            })
        }
    }
})