let vm = new Vue({
    el:'#dynamicBill',
    data:{
        content:'',
        contentOne:''
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
                        window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
                    }
                     
                    
                }
                    
                
            })
        },
        changeBillDay(){
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getOutDayList',
                success:function(data){
                    self.contentOne = eval('(' + data + ')');
                    console.log(self.contentOne);
                     //window.location.href='changebillDay';//没有逾期账单跳修改账单日
                   /* if(self.contentOne.success==false){
                       window.location.href='cunpaidBill';//有逾期账单跳账单未还清
                   } */
                }
                    
                
            })
        }
    }
})