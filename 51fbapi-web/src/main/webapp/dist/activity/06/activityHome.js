"use strict";function alaShareData(){var e={appLogin:"N",type:"share",shareAppTitle:"年中盛宴，错过这次等一年",shareAppContent:"分期免息“购”优惠，嗨购全球高佣好货，你要的攻略在这里！",shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:"https://app.51fanbei.com/fanbei-web/activity/feastRaidersShare",isSubmit:"N",sharePage:"feastRaidersShare"};return JSON.stringify(e)}function addModel(e,a){for(var t="",o=0;o<e.length;o++){var s=toDecimal2(e[o].saleAmount),i=toDecimal2(e[o].rebateAmount),n=a+'&params={"goodsId":"'+e[o].goodsId+'"}',r='<div class="price rebate">\n                  <span>￥'+s+"</span>\n                  <span><i>返</i>￥"+i+"</span>\n              </div>";if(1==e[o].goodsType){var l=toDecimal2(e[o].nperMap.amount),p=toDecimal2(e[o].nperMap.totalAmount);r='<div class="price stages">\n                 <p style="color:#f98011"><i class="monthCorner"></i>￥'+l+"×"+e[o].nperMap.nper+"</p>\n                 <p>抢购价：￥"+p+"</p>\n               </div>"}t+='<li class="clearfix"><a href='+n+"><img src="+e[o].goodsIcon+'><div class="boutiqueHomeContent clearfix"><p class="title" style="-webkit-box-orient: vertical;">'+e[o].goodName+"</p>"+r+"<button>马上抢</button></div></a></li>"}return t}var finished=0,page=1;window.onload=function(){$.ajax({url:"/fanbei-web/mainActivityInfo",type:"post",dataType:"JSON",success:function(e){if(e.success){console.log(e);for(var a=e.data.notifyUrl,t=e.data.mainActivityList,o=0;o<t.length;o++){var s=t[o].activityUrl,i=t[o].sort;t[o].goodType,$("#selectedHome li").attr("data-type");i?$("#selectedHome li").eq(o).find("a").attr("href",s):requestMsg("会场不存在")}var n=e.data.goodTitle;$("#goodTitle").append(n);var r=e.data.qualityGoodsList;$("#qualityGoodsList").append(addModel(r,a)),finished=0}else requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})},$(function(){function e(e){var a=0,t=0,o=0,s=0;e>0&&(a=Math.floor(e/86400),t=Math.floor(e/3600)-24*a,o=Math.floor(e/60)-24*a*60-60*t,s=Math.floor(e)-24*a*60*60-60*t*60-60*o),o<=9&&(o="0"+o),s<=9&&(s="0"+s),$("#day_show").html(a+"天"),$("#hour_show").html('<s id="h"></s>'+t+"时"),$("#minute_show").html("<s></s>"+o+"分"),$("#second_show").html("<s></s>"+s+"秒")}var a=new Date("June 30,2017 00:00:00"),t=a.valueOf(),o=new Date,s=o.valueOf(),i=t-s,n=parseInt(i/1e3);!function(a){e(a),a--,window.setInterval(function(){e(a),a--},1e3)}(n)});
//# sourceMappingURL=../../_srcmap/activity/06/activityHome.js.map
