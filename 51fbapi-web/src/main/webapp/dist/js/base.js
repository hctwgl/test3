"use strict";function requestMsg(t){layer.open({content:t,skin:"msg",time:2})}function getUrl(t){for(var n=location.search.substring(1).split("&"),r=0;r<n.length;r++)if(t==n[r].split("=")[0])return n[r].split("=")[1];return""}function getInfo(){var url=decodeURIComponent(location.toString()),paraArr=url.toString().split("_appInfo=");return paraArr.length>1?eval("("+paraArr[1]+")"):""}function toDecimal2(t){var n=parseFloat(t);if(isNaN(n))return!1;var r=Math.round(100*t)/100,e=r.toString(),o=e.indexOf(".");for(o<0&&(o=e.length,e+=".");e.length<=o+2;)e+="0";return e}function getBlatFrom(){var t=navigator.userAgent,n=t.indexOf("Android")>-1||t.indexOf("Adr")>-1,r=!!t.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);return n?1:r?2:0}function formatDate(t){return t=new Date(parseInt(t)),t.getFullYear()+"-"+(t.getMonth()+1)+"-"+t.getDate()}function getCookie(t){var n=document.cookie,r=n.indexOf(t);if(-1!=r){r+=t.length+1;var e=n.indexOf(";",r);-1==e&&(e=n.length);var o=unescape(n.substring(r,e))}return o}function domainName(){return window.location.protocol+"//"+window.location.host}function txtFix(t,n){return function(t,n){var r=0;if(t.length<=n)return t;for(var e=0;e<t.length;e++){var o=t.charAt(e);if(encodeURI(o).length>2?r+=1:r+=.5,r>=n){var i=r==n?e+1:e;return t.substr(0,i)}}}(t,n)}window.lazy=function(t,n,r){var e,o,i=[],a=function(r){var o=r.getBoundingClientRect();return(o.top>=0&&o.left>=0&&o.top)<=(t.innerHeight||n.documentElement.clientHeight)+parseInt(e)},u=function(){for(var t=i.length;t--;){var n=i[t];a(n)&&(n.src=n.getAttribute("lazy-src"),i.splice(t,1))}},l=function(){clearTimeout(o),o=setTimeout(u,0)};return{init:function(r){var o=n.querySelectorAll("[lazy-src]");e=r||0;for(var a=0;a<o.length;a++)i.push(o[a]);l(),n.addEventListener?t.addEventListener("scroll",l,!1):t.attachEvent("onscroll",l)},render:l}}(window,document);
//# sourceMappingURL=../_srcmap/js/base.js.map
