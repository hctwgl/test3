"use strict";function alaShareData(){var e={appLogin:"N",type:"share",shareAppTitle:"OPPO R11预约返利300元",shareAppContent:"OPPO R11全明星首发，疯陪到底！0元预约享12期分期免息，更有超级返利300元！有，且只在51返呗 GO>>>",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:domainName+"/fanbei-web/activity/oppoR11?oppoR11Share=oppoR11Share",isSubmit:"N",sharePage:"oppoR11"};return JSON.stringify(e)}function addMobileListModel(e,o){for(var a="",s=0;s<e.length;s++){var n=e[s].goodsIcon,t=e[s].goodsName,i=e[s].saleAmount,r=e[s].remark,p="";if(2==getBlatFrom()){var d=getInfo().appVersion;d&&d<365&&(p=o+'&params={"goodsId":"91625"}')}else p=o+'&params={"privateGoodsId":"'+e[s].goodsId+'"}';a+="<li>\n            <a href='"+p+"'>\n              <img src=\""+n+'">\n              <div class="mobileListContent">\n                <p><i></i>'+t+"</p>\n                <span>¥"+i+"</span>\n                <span>"+r+"</span>\n              </div>\n            </a>\n          </li>"}return a}var userName="";getInfo().userName&&(userName=getInfo().userName);var domainName=domainName(),oppoR11Share=getUrl("oppoR11Share");"oppoR11Share"==oppoR11Share&&($(".goRegister").removeClass("hide"),$(".banner").addClass("hide"),$(".mobileList").addClass("hide"),$(".mobileListImg").removeClass("hide")),window.onload=function(){$.ajax({url:"/app/activity/reserveActivityInfo",type:"POST",dataType:"JSON",data:{userName:userName},success:function(e){"Y"==e.data.isHaveReservationRecord&&$("#btn").attr("src","https://fs.51fanbei.com/h5/app/activity/06/oppo2_2.png")},error:function(){requestMsg("请求失败")}})},$(".mask").click(function(){$(".mask").css("display","none"),$(".orderSuccess").css("display","none"),window.location.reload()}),$(".close").click(function(){$(".mask").css("display","none"),$(".orderSuccess").css("display","none"),window.location.reload()}),$(function(){function e(e){var o=0,a=0,s=0,n=0;e>0&&(o=Math.floor(e/86400),a=Math.floor(e/3600)-24*o,s=Math.floor(e/60)-24*o*60-60*a,n=Math.floor(e)-24*o*60*60-60*a*60-60*s),s<=9&&(s="0"+s),n<=9&&(n="0"+n),$("#day_show").html(o+"天"),$("#hour_show").html('<s id="h"></s>'+a+"时"),$("#minute_show").html("<s></s>"+s+"分"),$("#second_show").html("<s></s>"+n+"秒")}var o=new Date("June 22,2017 10:00:00"),a=o.valueOf(),s=new Date,n=s.valueOf(),t=a-n,i=parseInt(t/1e3);!function(o){e(o),o--,window.setInterval(function(){e(o),o--},1e3)}(i)}),new Vue({el:"#oppoR11",methods:{btnBox:function(){$.ajax({url:"/app/activity/reserveActivityGoods",dataType:"json",data:{userName:userName},type:"post",success:function(e){e.success?($(".mask").css("display","block"),$(".orderSuccess").css("display","block")):e.url?location.href=e.url:requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})},mobilePopup:function(){$(".popupBox").removeClass("hide"),$(".popup").removeClass("hide")},close:function(){$(".popupBox").addClass("hide"),$(".popup").addClass("hide")}}});var page=0;window.onload=function(){page++,console.log(page),$.ajax({url:"/app/activity/getSelfSupportGoodsInfo",type:"POST",dataType:"JSON",data:{pageNo:page},success:function(e){$("#mobileList").append(addMobileListModel(e.data.goodsList,e.data.notifyUrl))},error:function(){requestMsg("请求失败")}})},alert(getUrl1("id"));
//# sourceMappingURL=../../_srcmap/activity/06/oppoR11.js.map
