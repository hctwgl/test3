"use strict";function requestMsg(t){layer.open({content:t,skin:"msg",time:2})}function getUrl(t){for(var n=location.search.substring(1).split("&"),e=0;e<n.length;e++)if(t==n[e].split("=")[0])return n[e].split("=")[1];return""}function getInfo(){var url=decodeURIComponent(location.toString()),paraArr=url.toString().split("_appInfo=");return paraArr.length>1?eval("("+paraArr[1]+")"):""}function toDecimal2(t){var n=parseFloat(t);if(isNaN(n))return!1;var e=Math.round(100*t)/100,r=e.toString(),o=r.indexOf(".");for(o<0&&(o=r.length,r+=".");r.length<=o+2;)r+="0";return r}function getBlatFrom(){var t=navigator.userAgent,n=t.indexOf("Android")>-1||t.indexOf("Adr")>-1,e=!!t.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);return n?1:e?2:0}function formatDate(t){return t=new Date(parseInt(t)),t.getFullYear()+"-"+(t.getMonth()+1)+"-"+t.getDate()}function getCookie(t){var n=document.cookie,e=n.indexOf(t);if(-1!=e){e+=t.length+1;var r=n.indexOf(";",e);-1==r&&(r=n.length);var o=unescape(n.substring(e,r))}return o}function domainName(){return window.location.protocol+"//"+window.location.host}function txtFix(t,n){var e=0;if(t.length<=n)return t;for(var r=0;r<t.length;r++){var o=t.charAt(r);if(encodeURI(o).length>2?e+=1:e+=.5,e>=n){var i=e==n?r+1:r;return t.substr(0,i)}}}function formateTelNum(t){if(t){var n=t.length;if(n<11)t=t;else if(n>=11){var e=t.substring(3,7);t=t.replace(e,"****")}return t}return""}function formatStr(t){return t=t.replace(/<br\/>/g,"\n")}function loading(){$("body").append(function(){var t="";return t+='<div class="loadingMask"><i class="loading"></i></div>'}())}function promptMsg(t){var n=document.createElement("div");$(n).css({position:"fixed",top:"50%",left:"50%",transform:"translate(-50%,-50%)","max-width":"50%",padding:".2rem .4rem",color:"white",background:"rgba(0,0,0,0.7)","border-radius":" .3rem",display:"none"}),$(n).html(t),$("body").append($(n)),$(n).fadeIn(),setTimeout(function(){$(n).fadeOut(function(){$(n).remove()})},1e3)}window.lazy=function(t,n,e){var r,o,i=[],a=function(e){var o=e.getBoundingClientRect();return(o.top>=0&&o.left>=0&&o.top)<=(t.innerHeight||n.documentElement.clientHeight)+parseInt(r)},u=function(){for(var t=i.length;t--;){var n=i[t];a(n)&&(n.src=n.getAttribute("lazy-src"),i.splice(t,1))}},l=function(){clearTimeout(o),o=setTimeout(u,0)};return{init:function(e){var o=n.querySelectorAll("[lazy-src]");r=e||0;for(var a=0;a<o.length;a++)i.push(o[a]);l(),n.addEventListener?t.addEventListener("scroll",l,!1):t.attachEvent("onscroll",l)},render:l}}(window,document);
//# sourceMappingURL=../_srcmap/js/base.js.map
