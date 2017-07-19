"use strict";var bid=getUrl("bid"),isLogin=$("#isLogin").val(),vm=new Vue({el:"#businessCode",data:{content:{}},methods:{buyNow:function(){var e=$(".paymoney").text();window.location.href='/fanbei-web/opennative?name=APP_PAY&params={"amount":"'+e+'"}'}}});
//# sourceMappingURL=../../_srcmap/js/user/initTradeInfo.js.map
