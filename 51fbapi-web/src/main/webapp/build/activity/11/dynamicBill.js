let vm = new Vue({
    el:'#dynamicBill',
    data:{

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
                    console.log(data);
                     window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
                    
                }
                    
                
            })
        }
    }
})