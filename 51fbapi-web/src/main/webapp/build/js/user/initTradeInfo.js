var isLogin=$('.isLogin').val();
var canUseAmount=$('.canUseAmount').val();
var name=$('.name').val();
var id=$('.id').val();
var isShowMention=$('.isShowMention').val();
if(isShowMention=='0'){
    $('.pro-word').hide();
}else{
    $('.pro-word').show();
}
if(isLogin=='no'){
    window.location.href='/fanbei-web/opennative?name=APP_LOGIN';
}
$(document).ready(function(){
    $(".paymoney").trigger('focus');
})
function buyNow(){
      var amount=$('.paymoney').val();
         window.location.href='/fanbei-web/opennative?name=APP_TRADE_PAY&params={"tradeAmount":"'+amount+'","tradeId":"'+id+'","tradeName":"'+name+'"}';
}
function identification(){
    if(isShowMention==1||isShowMention==2){
        window.location.href='/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_FACE"}';
    }else if(isShowMention==3){
        var idNumber=$('.idNumber').val();
        var realName=$('.realName').val();
        window.location.href='/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_BIND_CARD","idNumber":"'+idNumber+'","realName":"'+realName+'"}';
    }else if(isShowMention==5){
        window.location.href='/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_PROMOTE_EXTRA"}';
    }else{
        window.location.href='/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_PROMOTE_BASIC"}';
    }
}
function btn() {
    $('.btn').show();
    var amount=$('.paymoney').val();
    if( 0<Number(amount) && Number(amount)<=Number(canUseAmount) ){
        $('.btn').css('background','#FE963B').removeAttr('disabled','false');
    }else{
        $('.btn').css('background','gray').attr('disabled','true');
    }
}