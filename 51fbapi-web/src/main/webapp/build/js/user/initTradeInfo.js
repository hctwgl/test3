   var bid = getUrl("bid");
var isLogin=$('#isLogin').val();
   let vm=new Vue({
    el:'#businessCode',
    data:{
        content:{}
    },
    methods:{

     buyNow(){
         var amount=$('.paymoney').text();
         window.location.href='/fanbei-web/opennative?name=APP_PAY'+'&params={"amount":"'+amount+'"}';
         //window.location.href='http://www.baidu.com';
        
        }

   }

   })
