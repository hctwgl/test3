"use strict";function _classCallCheck(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}var _createClass=function(){function t(t,e){for(var s=0;s<e.length;s++){var n=e[s];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(t,n.key,n)}}return function(e,s,n){return s&&t(e.prototype,s),n&&t(e,n),e}}(),goodsList=eval("("+$("#goodsList").val()+")"),notifyUrl=$("#notifyUrl").val(),finished=0,addModel=function(t,e,s){var n="",i="";if(t.length>0){for(var a=0;a<t.length;a++){var o=toDecimal2(t[a].saleAmount),l=o.split("."),r=l[0],d=l[1],c='<p class="fs_28 fsc_red" style="padding-top: .3rem;padding-bottom: .25rem">\n                    <span>￥'+r+'</span><span class="fs_22">.'+d+"</span>";if(1==t[a].goodsType){var p=" <span>￥"+t[a].nperMap.amount+'</span><span class="fs_22">×'+t[a].nperMap.nper+"期</span>";1==t[a].nperMap.isFree&&(p=" <span>￥"+t[a].nperMap.freeAmount+'</span><span class="fs_22">×'+t[a].nperMap.nper+"期</span>"),n='<div class="goodsListModel_mainContent_rebate_wrap">\n                    <div class="goodsListModel_mainContent_rebate clearfix">\n                        <span class="goodsListModel_rebate fl fs_22 bgc_orange fsc_f tac">月供</span>\n                        <p class="fl fs_24 fsc_orange">\n                            '+p+"                       \n                        </p>\n                    </div>\n                </div>",c=' <p class="fs_26 fsc_red">\n                       <span>￥'+r+'</span>\n                       <span class="fs_20">.'+d+"</span>"}i+='<li class="goodsListModel_item">\n                    <a href=\''+(notifyUrl+'&params={"goodsId":"'+t[a].goodsId+'"}')+"'>\n                        <img src=\" "+t[a].goodsIcon+'" class="mainContent_img">\n                        <div class="goodsListModel_mainContent_wrap">\n                            <p class="fs_26 fsc_1">'+t[a].name+"</p>\n                            "+c+'          \n                                <span class="fs_24"><i class="ba">返</i>￥'+t[a].rebateAmount+"</span>\n                            </p>\n                        </div>\n                        "+n+"\n                    </a>\n                </li>"}finished=0}else i='<div class="nullPrompt"><img src="http://51fanbei.oss-cn-hangzhou.aliyuncs.com/h5/common/images/040101wuyouhui.png"><span style="margin-bottom: 2rem" class="fsc_6">暂无商品</span></div>';1==s?e.append(i):e.html(i)};addModel(goodsList,$("#initGoods"));var windowW=$(window).outerWidth(),page=1,Swipe=function(){function t(e){_classCallCheck(this,t),this.container=e,this.element=this.container.children[0],this.distance=0,this.length=this.element.children.length,this.speed=200,this.element.addEventListener("touchstart",this),this.element.addEventListener("touchmove",this)}return _createClass(t,[{key:"handleEvent",value:function(t){switch(t.type){case"touchstart":this.onTouchStart(t);break;case"touchmove":this.onTouchMove(t)}}},{key:"onTouchStart",value:function(t){this.isScrolling=!1,this.deltaX=0,this.start={pageX:t.touches[0].pageX,pageY:t.touches[0].pageY},this.element.style.MozTransitionDuration=this.element.style.webkitTransitionDuration=this.speed+"ms",this.startDistance=this.distance,t.stopPropagation()}},{key:"onTouchMove",value:function(t){t.touches.length>1||t.scale&&1!==t.scale||(this.deltaX=t.touches[0].pageX-this.start.pageX,Math.abs(this.deltaX)>Math.abs(t.touches[0].pageY-this.start.pageY)?this.isScrolling=!0:this.isScrolling=!1,this.isScrolling&&(this.distance=this.startDistance-this.deltaX,this.distance<0?this.distance=0:this.distance>this.element.clientWidth-windowW&&(this.distance=this.element.clientWidth-windowW),this.element.style.left=-this.distance+"px",t.stopPropagation()))}}]),t}();new Swipe(document.getElementById("navWrap")),$(function(){var ulW=0,liWArr=[];$(".nav li").each(function(t){var e=$(this).outerWidth();liWArr.push({index:t,width:e,offsetLeft:ulW}),ulW+=e}),ulW<windowW?$(".nav").css("width",windowW+"px"):$(".nav").css("width",ulW+5+"px");var typeCurrentNum=$("#typeCurrent").val(),modelIdNum=getUrl("modelId");$(".nav li").on("click",function(e){e.preventDefault();var i=$(this).index();$(this).find("span").addClass("current"),$(this).siblings().find("span").removeClass("current");var categoryObj=eval("("+$("#categoryList").val()+")");typeCurrentNum=categoryObj[i].type;var ulOffsetLeft=$(".nav").offset().left,thisLiOffsetUl=liWArr[i].offsetLeft,thisLiOffsetDiv=thisLiOffsetUl+ulOffsetLeft,offsetLeft=(windowW-liWArr[i].width)/2-thisLiOffsetDiv,offset=ulOffsetLeft+offsetLeft;offset>0?offset=0:ulW<windowW?offset=0:offset<windowW-ulW&&(offset=windowW-ulW),$(".nav").css({left:offset+"px"}),$(".swiper-slide").hide(),$("div[data-type="+typeCurrentNum+"]").show();var isUl=$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent");isUl.find("li").length<=0&&$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:1,type:typeCurrentNum},success:function(t){t.success?addModel(t.data.goodsList,isUl):requestMsg(t.msg)},error:function(){requestMsg("请求失败")}})}),$(window).on("scroll",function(){if(0==finished){var t=$(this).scrollTop();$(document).height()-$(this).height()<=t+400&&(page++,finished=1,$.ajax({url:"/app/goods/categoryGoodsList",type:"POST",dataType:"JSON",data:{modelId:modelIdNum,pageNo:page,type:typeCurrentNum},success:function(t){if(t.success)if(""==t.data.goodsList){$("div[data-type="+typeCurrentNum+"]").append('<div class="loadOver"><span>没有更多了...</span></div>')}else{var e=t.data.goodsList;addModel(e,$("div[data-type="+typeCurrentNum+"] .goodsListModel_mainContent"),1),finished=0}else requestMsg(t.msg)},error:function(){requestMsg("请求失败")}}))}})});
//# sourceMappingURL=../../_srcmap/js/goods/goodsListModel.js.map
