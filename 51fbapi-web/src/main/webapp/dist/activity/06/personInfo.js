"use strict";function button(){var e=$("#cont1").val(),t=$("#cont2").val(),a=$("#cont3").val();$.ajax({url:"/fanbei-web/submitContract",type:"POST",dataType:"JSON",data:{name:e,mobilePhone:t,address:a,_appInfo:'{"userName":"13955556666"}'},success:function(e){e.success?(requestMsg("提交成功"),location.href="game"):requestMsg("参数异常")},error:function(){requestMsg("请求失败")}})}$("#city-picker").cityPicker(function(){});
//# sourceMappingURL=../../_srcmap/activity/06/personInfo.js.map
