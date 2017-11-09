"use strict";var vm=new Vue({el:"#dynamicBill",data:{},created:function(){this.logData()},methods:{logData:function(){$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getUserOutDay",success:function(e){console.log(e),window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN"}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/dynamicBill.js.map
