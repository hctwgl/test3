"use strict";function _classCallCheck(t,a){if(!(t instanceof a))throw new TypeError("Cannot call a class as a function")}function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"51返呗618购物狂欢节攻略来啦！",shareAppContent:"分期免息“购”优惠，嗨购全球高佣好货，你要的攻略在这里！",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:apihost+"/fanbei-web/activity/gameShare",isSubmit:"Y",sharePage:"gameShare"};return JSON.stringify(t)}function dataInit(){$.ajax({url:"/fanbei-web/initGame.htm",type:"post",success:function success(data){if(data=eval("("+data+")"),console.log(data),data.success){isLogin=data.data.isLogin,clientRate=data.data.clientRate||100,$("#chance").html("您还有"+data.data.chanceCount+"次机会"),chance=data.data.chanceCodes?data.data.chanceCodes.split(","):[],data.data.item1Count>0&&($("#toys").find("img").eq(0).attr("src","https://fs.51fanbei.com/h5/app/activity/06/ni_boll1.png").css("width","61%"),$("#toys").find("span").eq(0).html(data.data.item1Count)),data.data.item2Count>0&&($("#toys").find("img").eq(1).attr("src","https://fs.51fanbei.com/h5/app/activity/06/ni_boll2.png").css("width","61%"),$("#toys").find("span").eq(1).html(data.data.item2Count)),data.data.item3Count>0&&($("#toys").find("img").eq(2).attr("src","https://fs.51fanbei.com/h5/app/activity/06/ni_boll3.png").css("width","61%"),$("#toys").find("span").eq(2).html(data.data.item3Count)),data.data.item4Count>0&&($("#toys").find("img").eq(3).attr("src","https://fs.51fanbei.com/h5/app/activity/06/ni_boll4.png").css("width","61%"),$("#toys").find("span").eq(3).html(data.data.item4Count)),data.data.item5Count>0&&($("#toys").find("img").eq(4).attr("src","https://fs.51fanbei.com/h5/app/activity/06/ni_boll5.png").css("width","61%"),$("#toys").find("span").eq(4).html(data.data.item5Count));for(var con="",i=0;i<data.data.awardList.length;i++)con+='<li>\n                <div class="personImg" style="background-image:url(\''+data.data.awardList[i].avatar+"')\"></div>\n                <h2><span>"+data.data.awardList[i].userName+"</span><span>"+data.data.awardList[i].msg+"</span></h2></li>";$(".awardList").html(con);for(var _con="",_i=0;_i<data.data.entityAwardList.length;_i++)_con+='<li>\n                <div class="personImg" style="background-image:url(\''+data.data.entityAwardList[_i].avatar+"')\"></div>\n                <h2><span>"+data.data.entityAwardList[_i].userName+"</span><span>"+data.data.entityAwardList[_i].msg+"</span></h2></li>";if($(".entityAwardList").html(_con),"Y"==data.data.isFinish&&($("#allToy").find("h3").html("五娃已集齐，请静等开奖！"),$("#getPrize h4").html("618活动抽奖获奖名单"),data.data.gmtOpen>0))if($("#getPrize h6").hide(),data.data.entityAwardList.length>0&&$("#dollRoll").show(),"N"==data.data.isAward){if(data.data.gmtOpen>0){var time=0;window.setInterval(function(){time+=1e3;var t=void 0,a=void 0,e=void 0,i=void 0,n=void 0,s=void 0,o=data.data.gmtOpen-data.data.gmtCurrent-time,l=parseInt(o/1e3),d=parseInt(l/3600),r=parseInt(l%3600/60),c=l%3600%60;d<10?(t="0",a=d):(d=d.toString(),t=d.slice(0,1),a=d.slice(1,2)),r<10?(e="0",i=r):(r=r.toString(),e=r.slice(0,1),i=r.slice(1,2)),c<10?(n="0",s=c):(c=c.toString(),n=c.slice(0,1),s=c.slice(1,2)),$(".countDown").find("span").eq(0).html(t),$(".countDown").find("span").eq(1).html(a),$(".countDown").find("span").eq(3).html(e),$(".countDown").find("span").eq(4).html(i),$(".countDown").find("span").eq(6).html(n),$(".countDown").find("span").eq(7).html(s)},1e3),$("#getPrize h5").html("开奖倒计时"),$(".countDown").show()}}else"G"==data.data.awardInfo.type?($("#getPrize h4").html("恭喜您获得幸运大奖！"),$("#getPrize h5").html("奖项已发送至:我的-抵用券")):"E"==data.data.awardInfo.type&&($("#getPrize h4").html("恭喜您获得实物大奖！"),"Y"==data.data.isSubmitContacts?$("#getPrize h5").html("奖项已发送至您的收货地址"):$("#getPrize h5").html('<a href="personinfo?url='+data.data.awardInfo.awardIcon+'">请提交资料</a>'))}else alert("初始化失败")}})}function AutoScroll(t,a){$(t).find("ul:first").animate({marginTop:a},500,function(){$(this).css({marginTop:"0"}).find("li:first").appendTo(this)})}function goUp(){$("#shadow").css("display","block"),$("#getPrize").slideDown(),$("#allToy").css("z-index",300),$("#allToy .gotoTop").css("transform","rotate(180deg)")}function goDown(){$("#shadow").css("display","none"),$("#getPrize").slideUp(),$("#allToy").css("z-index",150),$("#allToy .gotoTop").css("transform","rotate(0deg)")}var _createClass=function(){function t(t,a){for(var e=0;e<a.length;e++){var i=a[e];i.enumerable=i.enumerable||!1,i.configurable=!0,"value"in i&&(i.writable=!0),Object.defineProperty(t,i.key,i)}}return function(a,e,i){return e&&t(a.prototype,e),i&&t(a,i),a}}(),userName="";getInfo().userName&&(userName=getInfo().userName);var chance=[],isLogin=void 0,isShow=void 0,clientRate=void 0,apihost=getCookie("apihost");dataInit();var game=function(){function game(t,a){_classCallCheck(this,game),this.init={num:-t,time:a},this.num=-t,this.time=a,$("#playBtn").click(this.claw.bind(this))}return _createClass(game,[{key:"start",value:function(){var t=this;this.reset(),this.doll(),clearInterval(this.startMove),this.startMove=setInterval(function(){t.num>=0&&(t.num=-8.25,$("#scroll").animate({marginLeft:t.num+"rem"},0,"linear")),t.num=(Math.round(1e3*t.num)+1650)/1e3,$("#scroll").animate({marginLeft:t.num+"rem"},1e3,"linear")},1e3),clearTimeout(this.Countdown),this.Countdown=setTimeout(function(){"No"!=isShow&&t.alertMsg("end")},2e4),setTimeout(function(){$(".modelPic").hide()},1e3)}},{key:"reset",value:function(){$("#scroll").animate({marginLeft:this.init.num+"rem"},0,"linear"),$("#claw").css("backgroundImage","url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw1.png)"),this.num=this.init.num,this.time=this.init.time,clearInterval(this.timeStart),this.timeStart=setInterval(function(){this.time<=0?this.time=0:this.time-=1,$("#time").html(this.time+"秒")}.bind(this),1e3)}},{key:"doll",value:function(){for(var t=[1,2,3,4,5,1,2,3,4,5],a="",e=0;e<t.length;e++)a+='<span data-prop="'+t[e]+'" class="doll"><div class="doll-main" style="background-image: url(\'https://fs.51fanbei.com/h5/app/activity/06/ni_boll'+t[e]+".png')\"></div></span>";$("#scroll").html(a)}},{key:"alertMsg",value:function alertMsg(state,item){isShow="No";var data={result:"N",code:chance[1]};"end"==state&&$(".getState").html("抓取失败"),"claw"==state&&(data={result:"Y",item:item,code:chance[1]},$(".getState").html("抓取成功")),$.ajax({url:"/fanbei-web/submitGameResult",type:"post",data:data,success:function success(data){data=eval("("+data+")"),$(".ad").hide(),"Y"==data.data.lotteryResult?"CASH"==data.data.awardType?($(".getCashPrize").html("获得"+data.data.amount+"元现金").show(),$(".getCashCoupon").show()):($(".limitMoney").html(data.data.limitAmount),$(".limitDate").html(formatDate(data.data.gmtEnd)),$(".getMoney").html(data.data.amount),$(".getCashCoupon").show(),$(".getCouponPrize").show()):$(".jushuo").show(),$(".tryAgain").html("再抓一次"),$("#alert").show(),$("#shadow").show(),$(".tryAgain").click(function(){$("#shadow").hide(),$(".alert").hide()}),dataInit()}}),$("#startBtn").show(),$(".modelPic").show()}},{key:"claw",value:function(){var t=this,a=$("#claw").offset().left;$(".button").attr("disabled","disabled"),$("#claw").animate({top:"-.5rem"},1500,function(){$(".doll").each(function(){var e=$(this),i=e.offset().left;if(i>a-10&&i<a+35){var n=e.attr("data-prop");Math.floor(100*Math.random()+1)<clientRate&&($("#claw").css("backgroundImage","url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw2.png)"),e.find(".doll-main").css({position:"absolute",left:"2.47rem"}).animate({top:"-2.2rem"},800,function(){$(".doll[data-prop="+n+"]").css("visibility","hidden"),t.alertMsg("claw",n)}))}}),$("#claw").animate({top:"-2rem"},1e3,function(){$(".button").removeAttr("disabled")})})}}]),game}(),sixGame=new game(16.5,20);sixGame.doll(),$("#startBtn").click(function(){isShow="yes",$(".play").animate({top:"7.1rem"},200,function(){$(".play").animate({top:"7.08rem"},150)}),"Y"==isLogin?chance.length<=1||chance[1].length<5?($(".ad").hide(),$(".getState").html("机会用完啦").show(),$(".tryAgain").html("分享增加1次机会").click(function(){window.location.href='/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"年中抓娃娃,让你一次玩个爽","shareAppContent":"51返呗年中狂欢，全球好货折上折，iPhone 7+精美电器+上万礼券等你拿~","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"'+apihost+'/fanbei-web/activity/gameShare","isSubmit":"Y","sharePage":"gameShare"}'}),$("#alert").show(),$("#shadow").show()):(sixGame.start(),$(this).hide(),console.log(chance)):window.location.href="/fanbei-web/opennative?name=APP_LOGIN"}),$("#shadow").click(function(){$(this).hide(),$(".alert").hide()}),$("#rule").click(function(){$("#rules").css("display","block"),$("#shadow").show()}),$(".closeRules").click(function(){$("#rules").css("display","none"),$("#shadow").hide()}),$(document).ready(function(){setInterval('AutoScroll(".roll01","-1rem")',1e3),setInterval('AutoScroll(".roll02","-.55rem")',1e3)}),$(function(){var t=1;$("#allToy .gotoTop").click(function(){1==t?(goUp(),t=0):(goDown(),t=1)}),$("#shadow").click(function(){goDown(),t=1})});
//# sourceMappingURL=../../_srcmap/activity/06/game.js.map
