"use strict";var vm=new Vue({el:"#dynamicBill",data:{content:""},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/changeOutDay/getUserOutDay",success:function success(data){self.content=eval("("+data+")"),console.log(self.content),self.content.success}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/dynamicBill.js.map
