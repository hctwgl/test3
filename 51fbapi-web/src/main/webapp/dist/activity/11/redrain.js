"use strict";function yes(t,e,n){t.removeClass("on off notyet"),t.addClass("off"),t.find(".status").text("红包雨已结束")}function no(t,e,n){t.removeClass("on off notyet"),t.addClass("notyet"),t.find(".status").text(n)}function not(t,e,n){t.removeClass("on off notyet"),t.addClass("on"),t.find(".status").text(n)}var on=function(t,e,n){switch(e){case"yes":return void yes(t,e,n);case"no":return void no(t,e,n);case"not":return void not(t,e,n);default:return}},firstrain={el:$(".fir"),reset:"第一场红包雨",dot:10,exec:on,status:null},secondrain={el:$(".sec"),reset:"第二场红包雨",dot:14,exec:on,status:null},thirdrain={el:$(".thi"),reset:"第三场红包雨",dot:20,exec:on,status:null},order=new Array(0);order.push(firstrain,secondrain,thirdrain);var checkstatus=function(t){var e=t.dot||null,n=localtime.getHours(),o=localtime.getMinutes(),s=localtime.getSeconds();return n<e||n===e&&0===o&&s<=20?"no":"yes"},checkorder=function(t){var e=new Array(0);return t.forEach(function(t){e.push(checkstatus(t))}),e},adjuststatus=function(t){var e=0,n=t;return n.forEach(function(t){"no"===t&&e++}),e>0&&n.splice(n.indexOf("no"),1,"not"),n},execstatsu=function(t,e){t.forEach(function(t,n){t.status=e[n],t.exec(t.el,t.status,t.reset)})},counttime=function t(e){var n=null,o=null,s=localtime.getHours(),r=localtime.getMinutes(),a=localtime.getSeconds();e.forEach(function(t){"not"===t.status&&(n=t.dot)}),o=n?1e3*(60-a+60*(59-r)+3600*(n-1-s)):1e3*(60-a+60*(59-r)+3600*(33-s));var u=setInterval(function(){var t=new Date(o).getHours()>=8?new Date(o).getHours()-8:new Date(o).getHours()+16,e=new Date(o).getMinutes(),n=new Date(o).getSeconds();$(".hour").text(t),$(".minute").text(e),$(".second").text(n),o-=1e3},1e3);setTimeout(function(){clearInterval(u),$(".hour").text("0"),$(".minute").text("0"),$(".second").text("0"),setTimeout(function(){window.localtime=new Date;var n=adjuststatus(checkorder(e));execstatsu(e,n),t(e)},2e4)},o)};window.onload=function(){window.localtime=new Date;var t=adjuststatus(checkorder(order));execstatsu(order,t),counttime(order)};
//# sourceMappingURL=../../_srcmap/activity/11/redrain.js.map
