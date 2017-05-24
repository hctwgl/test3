"use strict";var userName="";getInfo().userName&&(userName=getInfo().userName),$.ajax({url:"pickBoluomeCoupon",data:{sceneId:"8166",userName:userName},type:"post",success:function success(data){data=eval("("+data+")"),data.success?requestMsg("领劵成功"):data.url?location.href=data.url:requestMsg(data.msg)}});
//# sourceMappingURL=../../_srcmap/activity/05/demo.js.map
