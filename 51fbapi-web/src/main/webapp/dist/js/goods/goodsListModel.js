"use strict";function _classCallCheck(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function handleScroll(){jQuery(window).scrollTop()>=207?jQuery("#navWrap").addClass("fixTop"):jQuery("#navWrap").removeClass("fixTop")}function couponClick(e,t,s,n){var i=e,a=t.index(),o=s,l=n;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:i},success:function(e){if(console.log(e),e.success)requestMsg("优惠劵领取成功"),o==l&&$(".coupon").eq(a).addClass("couponclose");else{var t=e.data.status;"USER_NOT_EXIST"==t&&(window.location.href=e.url),"OVER"==t&&requestMsg(e.msg),"COUPON_NOT_EXIST"==t&&requestMsg(e.msg),"MORE_THAN"==t&&requestMsg(e.msg)}},error:function(){requestMsg("哎呀，出错了！")}})}var _createClass=function(){function e(e,t){for(var s=0;s<t.length;s++){var n=t[s];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(e,n.key,n)}}return function(t,s,n){return s&&e(t.prototype,s),n&&e(t,n),t}}(),goodsList1=$("#goodsList").val(),goodsList=eval("("+goodsList1+")"),notifyUrl=$("#notifyUrl").val(),finished=0,addModel=function(e,t,s){var n="";if(e.length>0){for(var i=0;i<e.length;i++){var a="",o=toDecimal2(e[i].saleAmount);if(1==e[i].goodsType){var l=" <span>¥"+e[i].nperMap.freeAmount+'</span><span class="fs_22">起</span>';0==e[i].nperMap.isFree&&(l=" <span>¥"+e[i].nperMap.amount+'</span><span class="fs_22">起</span>'),a='<div class="goodsListModel_mainContent_rebate clearfix">\n                        <span class="goodsListModel_rebate fl fs_22 fsc_f tac">月供</span>\n                        <p class="fl fs_24">\n                            '+l+"\n                        </p>                \n                     </div>"}n+='<li class="goodsListModel_item">\n                    <a href=\''+(notifyUrl+'&params={"goodsId":"'+e[i].goodsId+'"}')+"'>\n                        <img src=\" "+e[i].goodsIcon+'" class="mainContent_img">\n                        <div class="goodsListModel_mainContent_wrap">\n                            <p class="fs_26 fsc_1">'+e[i].name+"</p>\n                            <p>                   \n                            <span>¥"+o+'</span>\n                                <span class="fsc_red fr"><i class="ba"></i>¥'+e[i].rebateAmount+"</span>\n                            </p>\n                        </div>\n                        "+a+"\n                    </a>\n                </li>"}finished=0}else n='<div class="nullPrompt">\n            <img src="http://51fanbei.oss-cn-hangzhou.aliyuncs.com/h5/common/images/040101wuyouhui.png">\n            <span style="margin-bottom: 2rem" class="fsc_6">暂无商品</span>\n            </div>';1==s?t.append(n):t.html(n)};addModel(goodsList,$("#initGoods"));var windowW=$(window).outerWidth(),page=1;2==getBlatFrom()?(window.addEventListener("touchstart",handleScroll),window.addEventListener("touchmove",handleScroll),window.addEventListener("touchend",handleScroll)):window.addEventListener("scroll",handleScroll);var Swipe=function(){function e(t){_classCallCheck(this,e),this.container=t,this.element=this.container.children[0],this.distance=0,this.length=this.element.children.length,this.speed=200,this.element.addEventListener("touchstart",this),this.element.addEventListener("touchmove",this)}return _createClass(e,[{key:"handleEvent",value:function(e){switch(e.type){case"touchstart":this.onTouchStart(e);break;case"touchmove":this.onTouchMove(e)}}},{key:"onTouchStart",value:function(e){this.isScrolling=!1,this.deltaX=0,this.start={pageX:e.touches[0].pageX,pageY:e.touches[0].pageY},this.element.style.MozTransitionDuration=this.element.style.webkitTransitionDuration=this.speed+"ms",this.startDistance=this.distance,e.stopPropagation()}},{key:"onTouchMove",value:function(e){e.touches.length>1||e.scale&&1!==e.scale||(this.deltaX=e.touches[0].pageX-this.start.pageX,Math.abs(this.deltaX)>Math.abs(e.touches[0].pageY-this.start.pageY)?this.isScrolling=!0:this.isScrolling=!1,this.isScrolling&&(this.distance=this.startDistance-this.deltaX,this.distance<0?this.distance=0:this.distance>this.element.clientWidth-windowW&&(this.distance=this.element.clientWidth-windowW),this.element.style.left=-this.distance+"px",e.stopPropagation()))}}]),e}();new Swipe(document.getElementById("navWrap")),$(function(){var ulW=0,liWArr=[];$(".nav li").each(function(e){var t=$(this).outerWidth();liWArr.push({index:e,width:t,offsetLeft:ulW}),ulW+=t}),ulW<windowW?$(".nav").css("width",windowW+"px"):$(".nav").css("width",ulW+5+"px");var typeCurrentNum=$("#typeCurrent").val(),modelIdNum=getUrl("modelId");$(".nav li").on("click",function(e){e.preventDefault();var i=$(this).index();$(this).find("span").addClass("current"),$(this).siblings().find("span").removeClass("current");var categoryObj=eval("("+$("#categoryList").val()+")");typeCurrentNum=categoryObj[i].type;var ulOffsetLeft=$(".nav").offset().left,thisLiOffsetUl=liWArr[i].offsetLeft,thisLiOffsetDiv=thisLiOffsetUl+ulOffsetLeft,offsetLeft=(windowW-liWArr[i].width)/2-thisLiOffsetDiv,offset=ulOffsetLeft+offsetLeft;offset>0?offset=0:ulW<windowW?offset=0:offset<windowW-ulW&&(offset=windowW-ulW),$(".nav").css({left:offset+"px"}),$(".swiper-slide").hide(),$("div[data-type="+typeCurrentNum+"]").show();var isUl=$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent");isUl.find("li").length<=0&&$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:1,type:typeCurrentNum},success:function(e){e.success?addModel(e.data.goodsList,isUl):requestMsg(e.msg)},error:function(){requestMsg("请求失败")}})}),$(window).on("scroll",function(){if(0==finished){var e=$(this).scrollTop();$(document).height()-$(this).height()<=e+400&&(page++,finished=1,$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:page,type:typeCurrentNum},success:function(e){if(e.success)if(""==e.data.goodsList){$("div[data-type="+typeCurrentNum+"]").append('<div class="loadOver"><span>没有更多了...</span></div>')}else{var t=e.data.goodsList;addModel(t,$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent"),1),finished=0}else requestMsg(e.msg)},error:function(){requestMsg("请求失败")}}))}})}),$(function(){$(".couponWrap").width(($(".coupon").width()+$(".kong").width())*$(".coupon").length),$(".kong").eq($(".coupon").length-1).hide()});
//# sourceMappingURL=../../_srcmap/js/goods/goodsListModel.js.map
