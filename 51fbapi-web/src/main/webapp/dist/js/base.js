"use strict";function requestMsg(t){layer.open({content:t,skin:"msg",time:2})}function getUrl(t){for(var r=location.search.substring(1).split("&"),n=0;n<r.length;n++)if(t==r[n].split("=")[0])return r[n].split("=")[1];return""}function getInfo(){var url=decodeURIComponent(location.toString()),paraArr=url.toString().split("_appInfo=");return paraArr.length>1?eval("("+paraArr[1]+")"):""}function toDecimal2(t){var r=parseFloat(t);if(isNaN(r))return!1;var n=Math.round(100*t)/100,e=n.toString(),a=e.indexOf(".");for(a<0&&(a=e.length,e+=".");e.length<=a+2;)e+="0";return e}function getBlatFrom(){var t=navigator.userAgent,r=t.indexOf("Android")>-1||t.indexOf("Adr")>-1,n=!!t.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);return r?1:n?2:0}function formatDate(t){return t=new Date(t/1e3),"20"+t.getYear()+"-"+(t.getMonth()+1)+"-"+t.getDate()}
//# sourceMappingURL=../_srcmap/js/base.js.map
