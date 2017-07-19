var isLogin=$('.isLogin').val();
var canUseAmount=$('.canUseAmount').val();
var name=$('.name').val();
var id=$('.id').val();
var isShowMention=$('.isShowMention').val();
if(isShowMention=='yes'){
    $('.pro-word').show();
}else{
    $('.pro-word').hide();
}
if(isLogin=='no'){
    window.location.href='/fanbei-web/opennative?name=APP_LOGIN';
}
function buyNow(){
      var amount=$('.paymoney').val();
         window.location.href='/fanbei-web/opennative?name=APP_PAY&params={"tradeAmount":"'+amount+'"&tradeId":"'+id+'"&tradeName":"'+name+'"}';
}
