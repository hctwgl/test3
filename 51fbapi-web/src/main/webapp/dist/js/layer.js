"use strict";var _typeof="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e};!function(e){var t=document,n="getElementsByClassName",i=function(e){return t.querySelectorAll(e)},s={type:0,shade:!0,shadeClose:!0,fixed:!0,anim:"scale"},l={extend:function(e){var t=JSON.parse(JSON.stringify(s));for(var n in e)t[n]=e[n];return t},timer:{},end:{}};l.touch=function(e,t){e.addEventListener("click",function(e){t.call(this,e)},!1)};var o=0,a=["layui-m-layer"],r=function(e){var t=this;t.config=l.extend(e),t.view()};r.prototype.view=function(){var e=this,s=e.config,l=t.createElement("div");e.id=l.id=a[0]+o,l.setAttribute("class",a[0]+" "+a[0]+(s.type||0)),l.setAttribute("index",o);var r=function(){var e="object"==_typeof(s.title);return s.title?'<h3 style="'+(e?s.title[1]:"")+'">'+(e?s.title[0]:s.title)+"</h3>":""}(),c=function(){"string"==typeof s.btn&&(s.btn=[s.btn]);var e,t=(s.btn||[]).length;return 0!==t&&s.btn?(e='<span yes type="1">'+s.btn[0]+"</span>",2===t&&(e='<span no type="0">'+s.btn[1]+"</span>"+e),'<div class="layui-m-layerbtn">'+e+"</div>"):""}();if(s.fixed||(s.top=s.hasOwnProperty("top")?s.top:100,s.style=s.style||"",s.style+=" top:"+(t.body.scrollTop+s.top)+"px"),2===s.type&&(s.content='<i></i><i class="layui-m-layerload"></i><i></i><p>'+(s.content||"")+"</p>"),s.skin&&(s.anim="up"),"msg"===s.skin&&(s.shade=!1),l.innerHTML=(s.shade?"<div "+("string"==typeof s.shade?'style="'+s.shade+'"':"")+' class="layui-m-layershade"></div>':"")+'<div class="layui-m-layermain" '+(s.fixed?"":'style="position:static;"')+'><div class="layui-m-layersection"><div class="layui-m-layerchild '+(s.skin?"layui-m-layer-"+s.skin+" ":"")+(s.className?s.className:"")+" "+(s.anim?"layui-m-anim-"+s.anim:"")+'" '+(s.style?'style="'+s.style+'"':"")+">"+r+'<div class="layui-m-layercont">'+s.content+"</div>"+c+"</div></div></div>",!s.type||2===s.type){var y=t[n](a[0]+s.type);y.length>=1&&layer.close(y[0].getAttribute("index"))}document.body.appendChild(l);var d=e.elem=i("#"+e.id)[0];s.success&&s.success(d),e.index=o++,e.action(s,d)},r.prototype.action=function(e,t){var i=this;e.time&&(l.timer[i.index]=setTimeout(function(){layer.close(i.index)},1e3*e.time));var s=function(){0==this.getAttribute("type")?(e.no&&e.no(),layer.close(i.index)):e.yes?e.yes(i.index):layer.close(i.index)};if(e.btn)for(var o=t[n]("layui-m-layerbtn")[0].children,a=o.length,r=0;a>r;r++)l.touch(o[r],s);if(e.shade&&e.shadeClose){var c=t[n]("layui-m-layershade")[0];l.touch(c,function(){layer.close(i.index,e.end)})}e.end&&(l.end[i.index]=e.end)},e.layer={v:"2.0",index:o,open:function(e){return new r(e||{}).index},close:function(e){var n=i("#"+a[0]+e)[0];n&&(n.innerHTML="",t.body.removeChild(n),clearTimeout(l.timer[e]),delete l.timer[e],"function"==typeof l.end[e]&&l.end[e](),delete l.end[e])},closeAll:function(){for(var e=t[n](a[0]),i=0,s=e.length;s>i;i++)layer.close(0|e[0].getAttribute("index"))}},"function"==typeof define?define(function(){return layer}):function(){var e=document.scripts,n=e[e.length-1],i=n.src,s=i.substring(0,i.lastIndexOf("/")+1);n.getAttribute("merge")||document.head.appendChild(function(){var e=t.createElement("link");return e.href=s+"../../css/layer.css",e.type="text/css",e.rel="styleSheet",e.id="layermcss",e}())}()}(window);
//# sourceMappingURL=_srcmap/layer.js.map
