"use strict";var addModel=function(s){for(var a="",e=0;e<s.length;e++){var o=toDecimal2(s[e].saleAmount),t=o.split("."),n=t[0],d=t[1],i=toDecimal2(s[e].rebateAmount),l=i.split("."),p=l[0],r=l[1];a+='<li class="goodsListModel_item"><a href='+(notifyUrl+'&params={"goodsId":"'+s[e].goodsId+'"}')+'><img src=" '+s[e].goodsIcon+' " class="mainContent_img"><div class="goodsListModel_mainContent_wrap"><p class="fs_26 fsc_1">'+s[e].name+'</p><p class="fs_26 fsc_red"><span>￥'+n+'</span><span class="fs_24">.'+d+'</span></p></div><div class="goodsListModel_mainContent_rebate_wrap"><div class="goodsListModel_mainContent_rebate clearfix"><span class="goodsListModel_rebate fl fs_24 bgc_orange fsc_f tac">返</span><p class="fl fs_24 fsc_orange"><span>￥'+p+'</span><span class="fs_22">.'+r+"</span></p></div></div></a></li>"}return a};$(function(){$(window).on("scroll",function(){if(0==finished){var s=$(this).scrollTop();$(document).height()-$(this).height()<=s+400&&(page++,finished=1,$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:page,type:typeCurrentNum},success:function(s){if(s.success)if(""==s.data.goodsList){$("div[data-type="+typeCurrentNum+"]").append('<div class="loadOver"><span>没有更多了...</span></div>')}else{var a=s.data.goodsList;$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").append(addModel(a)),finished=0}else requestMsg(s.msg)},error:function(){requestMsg("请求失败")}}))}})});
//# sourceMappingURL=../../_srcmap/activity/06/activityHome.js.map
