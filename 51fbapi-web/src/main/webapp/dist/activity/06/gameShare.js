"use strict";function _classCallCheck(t,a){if(!(t instanceof a))throw new TypeError("Cannot call a class as a function")}function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"年中抓娃娃,让你一次玩个爽！",shareAppContent:"51返呗年中狂欢，全球好货折上折，iPhone 7+精美电器+上万礼券等你拿~",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:"https://app.51fanbei.com/fanbei-web/activity/gameShare?recommendCode="+recommendCode,isSubmit:"Y",sharePage:"gameShare"};return JSON.stringify(t)}function dataInit(){$.ajax({url:"/fanbei-web/initGame.htm",type:"post",success:function success(data){if(data=eval("("+data+")"),console.log(data),data.success){if(isLogin=data.data.isLogin,chanceCount=data.data.chanceCount,clientRate=data.data.clientRate||100,$("#chance").html("您还有"+data.data.chanceCount+"次机会"),data.data.chanceCodes){chance=data.data.chanceCodes.split(",");for(var i=0;i<chance.length;i++)""!=chance[i]&&null!=chance[i]&&void 0!=_typeof(chance[i])||(chance.splice(i,1),i-=1)}else chance=[];for(var con="",_i=0;_i<data.data.awardList.length;_i++)con+='<li>\n                <div class="personImg" style="background-image:url(\''+data.data.awardList[_i].avatar+"')\"></div>\n                <h2><span>"+data.data.awardList[_i].userName+"</span><span>"+data.data.awardList[_i].msg+"</span></h2></li>";$(".awardList").html(con);for(var _con="",_i2=0;_i2<data.data.entityAwardList.length;_i2++)_con+='<li>\n                <div class="personImg" style="background-image:url(\''+data.data.entityAwardList[_i2].avatar+"')\"></div>\n                <h2><span>"+data.data.entityAwardList[_i2].userName+"</span><span>"+data.data.entityAwardList[_i2].msg+"</span></h2></li>";if($(".entityAwardList").html(_con),data.data.entityAwardList.length>0&&($("#getPrize h6").hide(),$("#getPrize h4").html("618活动抽奖获奖名单"),$("#dollRoll").show()),"Y"==data.data.isFinish)if($("#allToy").find("h3").html("五娃已集齐，请静等开奖！"),"N"==data.data.isAward){if(data.data.gmtOpen>0){var time=0,hours01=void 0,hours02=void 0,minutes01=void 0,minutes02=void 0,seconds01=void 0,seconds02=void 0,starTime=setInterval(function(){time+=1e3;var t=data.data.gmtOpen-data.data.gmtCurrent-time,a=parseInt(t/1e3),e=parseInt(a/3600),n=parseInt(a%3600/60),i=a%3600%60;e<10?(hours01="0",hours02=e):(e=e.toString(),hours01=e.slice(0,1),hours02=e.slice(1,2)),n<10?(minutes01="0",minutes02=n):(n=n.toString(),minutes01=n.slice(0,1),minutes02=n.slice(1,2)),i<10?(seconds01="0",seconds02=i):(i=i.toString(),seconds01=i.slice(0,1),seconds02=i.slice(1,2)),$(".countDown").find("span").eq(0).html(hours01),$(".countDown").find("span").eq(1).html(hours02),$(".countDown").find("span").eq(3).html(minutes01),$(".countDown").find("span").eq(4).html(minutes02),$(".countDown").find("span").eq(6).html(seconds01),$(".countDown").find("span").eq(7).html(seconds02)},1e3);hours01<=0&&hours02<=0&&minutes01<=0&&minutes02<=0&&seconds01<=0&&seconds02<=0?(clearInterval(starTime),$("#getPrize h5").html("正在抽取大奖中...")):(data.data.gmtOpen>14977512e5&&$("#getPrize h4").html("很遗憾，您暂未中奖，请静等下一波！"),$("#getPrize h5").html("开奖倒计时"),$(".countDown").show())}}else"G"==data.data.awardInfo.type?($("#getPrize h4").html("恭喜您获得幸运大奖！"),$("#getPrize h5").html("奖项已发送至:我的-抵用券")):"E"==data.data.awardInfo.type&&($("#getPrize h4").html("恭喜您获得实物大奖！"),"Y"==data.data.awardInfo.isSubmitContacts?$("#getPrize h5").html("奖项已发送至您的收货地址"):$("#getPrize h5").html('<a href="personInfo?url='+data.data.awardInfo.awardIcon+'">请提交资料</a>'))}else requestMsg("初始化失败")}})}function AutoScroll(t,a){$(t).find("ul:first").animate({marginTop:a},500,function(){$(this).css({marginTop:"0"}).find("li:first").appendTo(this)})}function goUp(){$("#shadow").css("display","block"),$("#getPrize").slideDown(),$("#allToy").css("z-index",300),$("#allToy .gotoTop").css("transform","rotate(180deg)")}function goDown(){$("#shadow").css("display","none"),$("#getPrize").slideUp(),$("#allToy").css("z-index",150),$("#allToy .gotoTop").css("transform","rotate(0deg)")}var _createClass=function(){function t(t,a){for(var e=0;e<a.length;e++){var n=a[e];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(t,n.key,n)}}return function(a,e,n){return e&&t(a.prototype,e),n&&t(a,n),a}}(),_typeof="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol&&t!==Symbol.prototype?"symbol":typeof t},chance=[],isLogin=void 0,isShow=void 0,clientRate=void 0,chanceCount=void 0,recommendCode=getUrl("recommendCode");$(".button a").attr("href","http://testapp.51fanbei.com/app/user/channelRegister?channelCode=Xpyq1&pointCode=Xpyq2"),dataInit();var game=function(){function game(t,a){_classCallCheck(this,game),this.init={num:-t,time:a},this.num=-t,this.time=a,$("#playBtn").click(this.claw.bind(this))}return _createClass(game,[{key:"start",value:function(){var t=this;this.reset(),this.doll(),clearInterval(this.startMove),this.startMove=setInterval(function(){t.num>=0&&(t.num=-8.25,$("#scroll").animate({marginLeft:t.num+"rem"},0,"linear")),t.num=(Math.round(1e3*t.num)+1650)/1e3,$("#scroll").animate({marginLeft:t.num+"rem"},1e3,"linear")},1e3),clearTimeout(this.Countdown),this.Countdown=setTimeout(function(){"No"!=isShow&&t.alertMsg("end")},2e4),setTimeout(function(){$(".modelPic").hide()},1e3)}},{key:"reset",value:function(){$("#scroll").animate({marginLeft:this.init.num+"rem"},0,"linear"),$("#claw").css("backgroundImage","url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw1.png)"),this.num=this.init.num,this.time=this.init.time,clearInterval(this.timeStart),this.timeStart=setInterval(function(){this.time<=0?this.time=0:this.time-=1,$("#time").html(this.time+"秒")}.bind(this),1e3)}},{key:"doll",value:function(){for(var t=[1,2,3,4,5,1,2,3,4,5],a="",e=0;e<t.length;e++)a+='<span data-prop="'+t[e]+'" class="doll"><div class="doll-main" style="background-image: url(\'https://fs.51fanbei.com/h5/app/activity/06/ni_boll'+t[e]+".png')\"></div></span>";$("#scroll").html(a)}},{key:"alertMsg",value:function alertMsg(state,item){isShow="No";var data={result:"N",code:chance[0]};"end"==state&&$(".getState").html("抓取失败"),"claw"==state&&(data={result:"Y",item:item,code:chance[0]},$(".getState").html("抓取成功")),$.ajax({url:"/fanbei-web/submitGameResult",type:"post",data:data,success:function success(data){data=eval("("+data+")"),$(".ad").hide(),"Y"==data.data.lotteryResult?"CASH"==data.data.awardType?($(".getCashPrize").html("获得"+data.data.amount+"元现金").show(),$(".getCashCoupon").show()):($(".limitMoney").html(data.data.limitAmount),$(".limitDate").html(formatDate(data.data.gmtEnd)),$(".getMoney").html(data.data.amount),$(".getCashCoupon").show(),$(".getCouponPrize").show()):$(".jushuo").show(),$(".tryAgain").html("再抓一次"),$("#alert").show(),$("#shadow").show(),$(".tryAgain").click(function(){$("#shadow").hide(),$(".alert").hide()}),dataInit()}}),$("#startBtn").show(),$(".modelPic").show()}},{key:"claw",value:function(){var t=this,a=$("#claw").offset().left;$(".button").attr("disabled","disabled"),$("#claw").animate({top:"-.5rem"},1500,function(){$(".doll").each(function(){var e=$(this),n=e.offset().left;if(n>a-10&&n<a+35){var i=e.attr("data-prop");Math.floor(100*Math.random()+1)<clientRate&&($("#claw").css("backgroundImage","url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw2.png)"),e.find(".doll-main").css({position:"absolute",left:"2.47rem"}).animate({top:"-2.2rem"},800,function(){$(".doll[data-prop="+i+"]").css("visibility","hidden"),t.alertMsg("claw",i)}))}}),$("#claw").animate({top:"-2rem"},1e3,function(){$(".button").removeAttr("disabled")})})}}]),game}(),sixGame=new game(16.5,20);sixGame.doll(),$("#startBtn").click(function(){isShow="yes",$(".play").animate({top:"7.1rem"},200,function(){$(".play").animate({top:"7.08rem"},150)}),"Y"==isLogin?chanceCount<1?($(".ad").hide(),$(".getState").html("机会用完啦").show(),$(".tryAgain").html("分享增加1次机会").click(function(){window.location.href='/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"年中抓娃娃,让你一次玩个爽","shareAppContent":"51返呗年中狂欢，全球好货折上折，iPhone 7+精美电器+上万礼券等你拿~","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"https://app.51fanbei.com/fanbei-web/activity/gameShare?recommendCode='+recommendCode+'","isSubmit":"Y","sharePage":"gameShare"}'}),$("#alert").show(),$("#shadow").show()):(sixGame.start(),$(this).hide(),console.log(chance)):window.location.href="/fanbei-web/opennative?name=APP_LOGIN"}),$("#shadow").click(function(){$(this).hide(),$(".alert").hide()}),$("#rule").click(function(){$("#rules").css("display","block"),$("#shadow").show()}),$(".closeRules").click(function(){$("#rules").css("display","none"),$("#shadow").hide()}),$(document).ready(function(){setInterval('AutoScroll(".roll01","-1rem")',1e3),setInterval('AutoScroll(".roll02","-.55rem")',1e3)}),$(function(){var t=1;$("#allToy .gotoTop").click(function(){1==t?(goUp(),t=0):(goDown(),t=1)}),$("#shadow").click(function(){goDown(),t=1})});
//# sourceMappingURL=../../_srcmap/activity/06/gameShare.js.map
