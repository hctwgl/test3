"use strict";function alaShareData(){var t={appLogin:"Y",type:"share",shareAppTitle:"年中盛宴，错过这次等一年",shareAppContent:"分期免息“购”优惠，嗨购全球高佣好货，你要的攻略在这里！",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:window.location.host+"/fanbei-web/activity/feastRaidersShare",isSubmit:"Y",sharePage:"selectedHome"};return JSON.stringify(t)}function statistics(t){$.ajax({url:"/fanbei-web/qualityGoodsStatistics",type:"post",data:{goodsId:t},success:function(t){console.log(t)}})}function addModel(t,e){for(var a="",o=0;o<t.length;o++){var s=toDecimal2(t[o].saleAmount),i=toDecimal2(t[o].rebateAmount),n=e+'&params={"goodsId":"'+t[o].goodsId+'"}',r='<div class="price rebate">\n                    <span>￥'+s+"</span>\n                    <span><i>返</i>￥"+i+"</span></div>";if(1==t[o].goodsType){var l=toDecimal2(t[o].nperMap.amount),c=toDecimal2(t[o].nperMap.totalAmount);r='<div class="price stages">\n                   <p style="color:#f98011"><i class="monthCorner"></i>￥'+l+"×"+t[o].nperMap.nper+"</p>\n                   <p>抢购价：￥"+c+"</p>\n                 </div>"}a+='<li class="clearfix"><a onclick="statistics('+t[o].goodsId+')" href='+n+"><img src="+t[o].goodsIcon+'><div class="boutiqueHomeContent clearfix"><p class="title" style="-webkit-box-orient: vertical;">'+t[o].goodName+"</p>"+r+"<button>马上抢</button></div></a></li>"}return a}var finished=0,page=1;window.onload=function(){$.ajax({url:"/fanbei-web/mainActivityInfo",type:"post",dataType:"JSON",success:function(t){if(t.success){console.log(t);for(var e=t.data.notifyUrl,a=t.data.mainActivityList,o=0;o<a.length;o++){var s=a[o].activityUrl,i=a[o].sort;a[o].goodType,$("#selectedHome li").attr("data-type");i?$("#selectedHome li").eq(o).find("a").attr("href",s):requestMsg("会场不存在")}var n=t.data.goodTitle;$("#goodTitle").append(n);var r=t.data.qualityGoodsList;$("#qualityGoodsList").append(addModel(r,e)),finished=0}else requestMsg(t.msg)},error:function(){requestMsg("请求失败")}})},$(function(){function t(t){var e=0,a=0,o=0,s=0;t>0&&(e=Math.floor(t/86400),a=Math.floor(t/3600)-24*e,o=Math.floor(t/60)-24*e*60-60*a,s=Math.floor(t)-24*e*60*60-60*a*60-60*o),o<=9&&(o="0"+o),s<=9&&(s="0"+s),$("#day_show").html(e+"天"),$("#hour_show").html('<s id="h"></s>'+a+"时"),$("#minute_show").html("<s></s>"+o+"分"),$("#second_show").html("<s></s>"+s+"秒")}var e=new Date("June 30,2017 00:00:00"),a=e.valueOf(),o=new Date,s=o.valueOf(),i=a-s,n=parseInt(i/1e3);!function(e){t(e),e--,window.setInterval(function(){t(e),e--},1e3)}(n)});
//# sourceMappingURL=../../_srcmap/activity/06/activityHome.js.map
