"use strict";var couponArr=[],sendAjax=!0,ward=[parseInt(5*Math.random()+1),parseInt(5*Math.random()+6),parseInt(5*Math.random()+11)],touch=0;$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/redRainActivity?type=pv"},success:function(t){console.log(t)}}),$(document).ready(function(){var t=parseInt($(".content").css("width"))-70,a=function a(){var n=parseInt(2*Math.random()+1),e=parseInt(60*Math.random()+50),i=parseInt(Math.random()*t),c=parseInt(90*Math.random()-45)+"deg";o++,$(".content").append("<li class='li"+o+"' ></li>"),$(".li"+o).css({left:i}),$(".li"+o).css({width:e,height:e+30,transform:"rotate("+c+")",background:"url(https://f.51fanbei.com/h5/app/activity/11/redRain"+n+".png) no-repeat center center","background-size":"100%","-ms-transform":"rotate("+c+")","-moz-transform":"rotate("+c+")","-webkit-transform":"rotate("+c+")","-o-transform":"rotate("+c+")"}),$(".li"+o).animate({top:$(window).height()+20},4e3,function(){this.remove()}),$(".li"+o).one("touchstart",function(){touch++;var t=this;if(ward.indexOf(touch)>=0&&couponArr.length<3&&sendAjax){sendAjax=!1;var a=(new Date).getTime();$.ajax({url:"/fanbei-web/redRain/applyHit?time="+a,type:"post",success:function(a){sendAjax=!0,a=JSON.parse(a),console.log(a),a.success?(couponArr.push(a.data),$(t).html('<div style="transform: none;color:white;font-size: .16rem;position: absolute;right:-.1rem;top:0rem;">+1</div>'),t.style.backgroundImage="url(https://f.51fanbei.com/h5/app/activity/11/redRain4.png)",s+=1,$(".noWard").hide(),$(".ward").show(),$(".redNum span:nth-child(2)").text("中红包数量："+s)):t.style.backgroundImage="url(https://f.51fanbei.com/h5/app/activity/11/redRain3.png)"},error:function(){t.style.backgroundImage="url(https://f.51fanbei.com/h5/app/activity/11/redRain3.png)"}})}else t.style.backgroundImage="url(https://f.51fanbei.com/h5/app/activity/11/redRain3.png)"}),r>0&&setTimeout(a,300)},n=function t(){if(--r>0)$(".redNum span:nth-child(1)").text(r),setTimeout(t,1e3);else{$(".redNum span:nth-child(1)").text(0);for(var a="",n=0;n<couponArr.length;n++)a+='<div class="wardCoupon">\n                          <span class="wardMoney"><span>￥</span>'+couponArr[n].amount+'</span>\n                          <span class="wardTxt">'+couponArr[n].couponName+"</span>\n                      </div>";$(".wardContent").html(a),$(".gameWard").show()}},e=6,r=21;!function t(){e--,e>0?($(".backward span").html(e),setTimeout(t,1e3)):($(".backward").remove(),n())}();var o=0,s=0;setTimeout(a,5e3)});
//# sourceMappingURL=../../_srcmap/activity/11/redRainActivity.js.map
