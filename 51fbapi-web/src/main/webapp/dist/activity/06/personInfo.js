
"use strict";function button(){var e=$("#cont1").val(),t=$("#cont2").val(),s=$("#cont3").val();/^1[34578]\d{9}$/.test(t)?$.ajax({url:"/fanbei-web/submitContract",type:"POST",dataType:"JSON",data:{name:e,mobilePhone:t,address:s},success:function(e){e.success?(requestMsg("提交成功"),location.href="game"):requestMsg("参数异常")},error:function(){requestMsg("请求失败")}}):requestMsg("手机格式不正确，请重新输入！")}function img(){var e=getUrl("url"),t='<img src="'+e+'">';$(".prizeImg").append(t)}$("#city-picker").cityPicker(function(){}),img();

//# sourceMappingURL=../../_srcmap/activity/06/personInfo.js.map
