"use strict";function buyNow(){var n=$(".paymoney").val();window.location.href='/fanbei-web/opennative?name=APP_TRADE_PAY&params={"tradeAmount":"'+n+'","tradeId":"'+id+'","tradeName":"'+name+'"}'}function identification(){if(1==isShowMention||2==isShowMention)window.location.href='/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_FACE"}';else if(3==isShowMention){var n=$(".idNumber").val(),e=$(".realName").val();window.location.href='/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_BIND_CARD","idNumber":"'+n+'","realName":"'+e+'"}'}else window.location.href=5==isShowMention?'/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_PROMOTE_EXTRA"}':'/fanbei-web/opennative?name=APP_TRADE_PROMOTE&params={"action":"DO_PROMOTE_BASIC"}'}function btn(){$(".btn").show();var n=$(".paymoney").val();0<Number(n)&&Number(n)<=Number(canUseAmount)?$(".btn").css("background","#FE963B").removeAttr("disabled","false"):$(".btn").css("background","gray").attr("disabled","true")}var isLogin=$(".isLogin").val(),canUseAmount=$(".canUseAmount").val(),name=$(".name").val(),id=$(".id").val(),isShowMention=$(".isShowMention").val();"0"==isShowMention?$(".pro-word").hide():$(".pro-word").show(),"no"==isLogin&&(window.location.href="/fanbei-web/opennative?name=APP_LOGIN"),$(document).ready(function(){$(".paymoney").trigger("focus")});
//# sourceMappingURL=../../_srcmap/js/user/initTradeInfo.js.map
