"use strict";function requestMsg(r){layer.open({content:r,skin:"msg",time:2})}function getUrl(r){for(var t=location.search.substring(1).split("&"),n=0;n<t.length;n++)if(r==t[n].split("=")[0])return t[n].split("=")[1];return""}function getInfo(){var url=decodeURIComponent(location.toString()),paraArr=url.toString().split("_appInfo=");return paraArr.length>1?eval("("+paraArr[1]+")"):""}function toDecimal2(r){var t=parseFloat(r);if(isNaN(t))return!1;var n=Math.round(100*r)/100,e=n.toString(),i=e.indexOf(".");for(i<0&&(i=e.length,e+=".");e.length<=i+2;)e+="0";return e}function getBlatFrom(){var r=navigator.userAgent,t=r.indexOf("Android")>-1||r.indexOf("Adr")>-1,n=!!r.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);return t?1:n?2:0}
//# sourceMappingURL=../_srcmap/js/base.js.map
