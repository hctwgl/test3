"use strict";!function(t){t.fn.pin=function(s){var o=0,e=[],i=!1,a=t(window);s=s||{};var n=function(){for(var o=0,n=e.length;o<n;o++){var p=e[o];if(s.minWidth&&a.width()<=s.minWidth)p.parent().is(".pin-wrapper")&&p.unwrap(),p.css({width:"",left:"",top:"",position:""}),s.activeClass&&p.removeClass(s.activeClass),i=!0;else{i=!1;var r=s.containerSelector?p.closest(s.containerSelector):t(document.body),c=p.offset(),d=r.offset(),f=p.offsetParent().offset();p.parent().is(".pin-wrapper")||p.wrap("<div class='pin-wrapper'>");var l=t.extend({top:0,bottom:0},s.padding||{});p.data("pin",{pad:l,from:(s.containerSelector?d.top:c.top)-l.top,to:d.top+r.height()-p.outerHeight()-l.bottom,end:d.top+r.height(),parentTop:f.top}),p.css({width:p.outerWidth()}),p.parent().css("height",p.outerHeight())}}},p=function(){if(!i){o=a.scrollTop();for(var n=[],p=0,r=e.length;p<r;p++){var c=t(e[p]),d=c.data("pin");if(d){n.push(c);var f=d.from-d.pad.bottom,l=d.to-d.pad.top;f+c.outerHeight()>d.end?c.css("position",""):f<o&&l>o?(!("fixed"==c.css("position"))&&c.css({left:c.offset().left,top:d.pad.top}).css("position","fixed"),s.activeClass&&c.addClass(s.activeClass)):o>=l?(c.css({left:"",top:l-d.parentTop+d.pad.top}).css("position","absolute"),s.activeClass&&c.addClass(s.activeClass)):(c.css({position:"",top:"",left:""}),s.activeClass&&c.removeClass(s.activeClass))}}e=n}},r=function(){n(),p()};return this.each(function(){var s=t(this),o=t(this).data("pin")||{};o&&o.update||(e.push(s),t("img",this).one("load",n),o.update=r,t(this).data("pin",o))}),a.scroll(p),a.resize(function(){n()}),n(),a.load(r),this}}(jQuery);
//# sourceMappingURL=../_srcmap/js/jquery.pin.js.map
