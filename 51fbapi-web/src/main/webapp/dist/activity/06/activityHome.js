"use strict";function alaShareData(){var e={appLogin:"N",type:"share",shareAppTitle:"年中盛宴，错过这次等一年",shareAppContent:"分期免息“购”优惠，嗨购全球高佣好货，你要的攻略在这里！",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:"https://app.51fanbei.com/fanbei-web/activity/feastRaidersShare",isSubmit:"N",sharePage:"feastRaidersShare"};return JSON.stringify(e)}function statistics(e){$.ajax({url:"/fanbei-web/qualityGoodsStatistics",type:"post",data:{goodsId:e},success:function(t){console.log(e),console.log(t)}})}function addModel(e,t){for(var a="",o=0;o<e.length;o++){var s=toDecimal2(e[o].saleAmount),i=toDecimal2(e[o].rebateAmount),n=t+'&params={"goodsId":"'+e[o].goodsId+'"}',r='<div class="price rebate">\n                  <span>￥'+s+"</span>\n                  <span><i>返</i>￥"+i+"</span>\n              </div>";if(1==e[o].goodsType){var l=toDecimal2(e[o].nperMap.amount);if(1==e[o].nperMap.isFree){toDecimal2(e[o].nperMap.freeAmount)}var c=toDecimal2(e[o].nperMap.totalAmount);r='<div class="price stages">\n                 <p style="color:#f98011"><i class="monthCorner"></i>￥'+l+"×"+e[o].nperMap.nper+"</p>\n                 <p>抢购价：￥"+c+"</p>\n               </div>"}a+='<li class="clearfix"><a onclick="statistics('+e[o].goodsId+')" href='+n+"><img src="+e[o].goodsIcon+'><div class="boutiqueHomeContent clearfix"><p class="title" style="-webkit-box-orient: vertical;">'+e[o].goodName+"</p>"+r+"<button>马上抢</button></div></a></li>"}return a}var finished=0,page=1;window.onload=function(){$.ajax({url:"/fanbei-web/mainActivityInfo",type:"post",dataType:"JSON",success:function(e){if(e.success){console.log(e);for(var t=e.data.notifyUrl,a=e.data.mainActivityList,o=0;o<a.length;o++){var s=a[o].activityUrl,i=a[o].sort;a[o].goodType,$("#selectedHome li").attr("data-type");i?$("#selectedHome li").eq(o).find("a").attr("href",s):requestMsg("会场不存在")}var n=e.data.goodTitle;$("#goodTitle").append(n);var r=e.data.qualityGoodsList;$("#qualityGoodsList").append(addModel(r,t)),finished=0}else requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})},$(function(){function e(e){var t=0,a=0,o=0,s=0;e>0&&(t=Math.floor(e/86400),a=Math.floor(e/3600)-24*t,o=Math.floor(e/60)-24*t*60-60*a,s=Math.floor(e)-24*t*60*60-60*a*60-60*o),o<=9&&(o="0"+o),s<=9&&(s="0"+s),$("#day_show").html(t+"天"),$("#hour_show").html('<s id="h"></s>'+a+"时"),$("#minute_show").html("<s></s>"+o+"分"),$("#second_show").html("<s></s>"+s+"秒")}var t=new Date("June 30,2017 23:59:59"),a=t.valueOf(),o=new Date,s=o.valueOf(),i=a-s,n=parseInt(i/1e3);!function(t){e(t),t--,window.setInterval(function(){e(t),t--},1e3)}(n)});
//# sourceMappingURL=../../_srcmap/activity/06/activityHome.js.map
