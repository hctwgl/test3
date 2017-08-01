"use strict";function play(){var e=ul.offsetTop;e>-50?(e+=-3,ul.style.top=e+"px"):ul.style.top=0}var roll=document.getElementById("roll"),ul=roll.children[0],lis=ul.children,timer=null;timer=setInterval(play,15),screen.onmouseover=function(){clearInterval(timer)},screen.onmouseout=function(){timer=setInterval(play,15)};
//# sourceMappingURL=../../_srcmap/activity/07/rankingList.js.map
