"use strict";$(".btn").click(function(){var t=$(".blank-in").val(),a=String(CryptoJS.MD5(t));$.ajax({url:"/H5GGShare/boluomeActivityForgetPwd",type:"POST",dataType:"JSON",data:{password:a},success:function(t){console.log(t),t.success?window.location.href="data.url":requestMsg(t.msg)}})});
//# sourceMappingURL=../../_srcmap/activity/08/ggForgetP.js.map
