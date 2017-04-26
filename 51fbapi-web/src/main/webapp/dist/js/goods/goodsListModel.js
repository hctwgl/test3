"use strict";function Swipe(t){this.container=t,this.element=this.container.children[0],this.distance=0,this.length=this.element.children.length,this.speed=200,this.element.addEventListener("touchstart",this),this.element.addEventListener("touchmove",this)}var typeCurrentNum=$("#typeCurrent").val(),modelIdNum=getUrl("modelId"),category=$("#categoryList").val(),categoryObj=eval("("+category+")"),notifyUrl=$("#notifyUrl").val(),windowW=$(window).outerWidth(),liWArr=[],ulW=0,finished=0,page=1;Swipe.prototype={handleEvent:function(t){switch(t.type){case"touchstart":this.onTouchStart(t);break;case"touchmove":this.onTouchMove(t)}},onTouchStart:function(t){this.isScrolling=!1,this.deltaX=0,this.start={pageX:t.touches[0].pageX,pageY:t.touches[0].pageY},this.element.style.MozTransitionDuration=this.element.style.webkitTransitionDuration=this.speed+"ms",this.startDistance=this.distance,t.stopPropagation()},onTouchMove:function(t){t.touches.length>1||t.scale&&1!==t.scale||(this.deltaX=t.touches[0].pageX-this.start.pageX,Math.abs(this.deltaX)>Math.abs(t.touches[0].pageY-this.start.pageY)?this.isScrolling=!0:this.isScrolling=!1,this.isScrolling&&(this.distance=this.startDistance-this.deltaX,this.distance<0?this.distance=0:this.distance>this.element.clientWidth-windowW&&(this.distance=this.element.clientWidth-windowW),this.element.style.left=-this.distance+"px",t.stopPropagation()))}},new Swipe(document.getElementById("navWrap"));var addModel=function(t){for(var e="",s=0;s<t.length;s++){var i=toDecimal2(t[s].priceAmount),a=i.split("."),n=a[0],o=a[1],d=toDecimal2(t[s].rebateAmount),l=d.split("."),r=l[0],c=l[1];e+='<li class="goodsListModel_item"><a href=" '+(notifyUrl+"&params={'goodsId':'"+t[s].goodsId+"'}")+' "><img src=" '+t[s].goodsIcon+' " class="mainContent_img"><div class="goodsListModel_mainContent_wrap"><p class="fs_26 fsc_1">'+t[s].name+'</p><p class="fs_26 fsc_red"><span>￥'+n+'</span><span class="fs_24">.'+o+'</span></p></div><div class="goodsListModel_mainContent_rebate_wrap"><div class="goodsListModel_mainContent_rebate clearfix"><span class="goodsListModel_rebate fl fs_24 bgc_orange fsc_f tac">返</span><p class="fl fs_24 fsc_orange"><span>￥'+r+'</span><span class="fs_22">.'+c+"</span></p></div></div></a></li>"}return e};$(function(){$(".nav li").each(function(t){var e=$(this).outerWidth();liWArr.push({index:t,width:e,offsetLeft:ulW}),ulW+=e}),ulW<windowW?$(".nav").css("width",windowW+"px"):$(".nav").css("width",ulW+5+"px"),$(".nav li").on("click",function(t){t.preventDefault();var e=$(this).index();$(this).find("span").addClass("current"),$(this).siblings().find("span").removeClass("current"),typeCurrentNum=categoryObj[e].type;var s=$(".nav").offset().left,i=liWArr[e].offsetLeft,a=i+s,n=(windowW-liWArr[e].width)/2-a,o=s+n;o>0?o=0:ulW<windowW?o=0:o<windowW-ulW&&(o=windowW-ulW),$(".nav").css({left:o+"px"}),$(".swiper-slide").hide(),$("div[data-type="+typeCurrentNum+"]").show();var d=$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent");d.find("li").length<=0&&$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:1,type:typeCurrentNum},success:function(t){if(t.success){var e="",s=t.data.goodsList;s.length>0?(e=addModel(s),finished=0):e='<div class="nullPrompt"><img src="http://51fanbei.oss-cn-hangzhou.aliyuncs.com/h5/common/images/040101wuyouhui.png"><span style="margin-bottom: 2rem" class="fsc_6">暂无商品</span></div>',d.html(e)}else requestMsg(t.msg)},error:function(){requestMsg("请求失败")}})}),$(window).on("scroll",function(){if(0==finished){var t=$(this).scrollTop();$(document).height()-$(this).height()<=t+400&&(page++,finished=1,$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:page,type:typeCurrentNum},success:function(t){if(t.success)if(""==t.data.goodsList){$("div[data-type="+typeCurrentNum+"]").append('<div class="loadOver"><span>没有更多了...</span></div>')}else{var e=t.data.goodsList;$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent").append(addModel(e)),finished=0}else requestMsg(t.msg)},error:function(){requestMsg("请求失败")}}))}})});
//# sourceMappingURL=../../_srcmap/js/goods/goodsListModel.js.map
