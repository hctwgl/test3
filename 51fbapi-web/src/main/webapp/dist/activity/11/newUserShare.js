"use strict";var vm=new Vue({el:"#newUserShare",data:{content:{},ruleShow:!1},created:function(){this.logData()},methods:{logData:function logData(){var self=this;$.ajax({type:"post",url:"/activity/freshmanShare/homePage",success:function success(data){console.log(data),self.content=eval("("+data.data+")"),console.log(self.content),self.$nextTick(function(){$(".first").each(function(){$(this).load(function(){$(".loadingMask").fadeOut()}),setTimeout(function(){$(".loadingMask").fadeOut()},1e3)}),$(".loadingMask").fadeOut(),$("img.lazy").lazyload({placeholder:"https://f.51fanbei.com/h5/common/images/bitmap1.png",effect:"fadeIn",threshold:200})})},error:function(){requestMsg("哎呀，出错了！")}})},activityDetailClick:function(){this.ruleShow=!0,$(".alertRule").animate({left:"15.4%"},600)},closeClick:function(){this.ruleShow=!1,$(".alertRule").animate({left:"140%"},600)},buyNowClick:function(){window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www"}}});
//# sourceMappingURL=../../_srcmap/activity/11/newUserShare.js.map
