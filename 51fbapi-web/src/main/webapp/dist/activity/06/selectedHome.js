"use strict";function _classCallCheck(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}function alaShareData(){var t=void 0,e=void 0;switch(sort){case 1:t=shareData[0].title,e=shareData[0].content;break;case 2:t=shareData[1].title,e=shareData[1].content;break;case 3:t=shareData[2].title,e=shareData[2].content}var a={appLogin:"Y",type:"share",shareAppTitle:t,shareAppContent:e,shareAppImage:"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png",shareAppUrl:"https://app.51fanbei.com/fanbei-web/activity/feastRaidersShare",isSubmit:"Y",sharePage:"feastRaidersShare"};return JSON.stringify(a)}var _createClass=function(){function t(t,e){for(var a=0;a<e.length;a++){var n=e[a];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(t,n.key,n)}}return function(e,a,n){return a&&t(e.prototype,a),n&&t(e,n),e}}(),modelId=getUrl("modelId"),sort=getUrl("sort"),windowW=$(window).outerWidth(),shareData=[{title:"分期购物无忧 3/6/9/12免息",content:"分期无忧 拯救剁手党,90后消费哲学潮这里 我的 我的 都是我的！"},{title:"高佣好货聚集618，最高返利50%",content:"全球好货你来淘，佣金我来返。大牌好货爆款，超高额返利尽在嗨购高佣超级券专场。你来淘，我来返！"},{title:"联手大牌  满百就减",content:"一份品味逆天的秘籍送给你！不止大牌集群，小众精美好物待你来挖掘~美好生活，触手可及"}],Swipe=function(){function t(e){_classCallCheck(this,t),this.container=e,this.element=this.container.children[0],this.distance=0,this.length=this.element.children.length,this.speed=200,document.getElementById("listAlert").style.width=1.5*this.length+"rem",this.length>5&&(this.element.addEventListener("touchstart",this),this.element.addEventListener("touchmove",this))}return _createClass(t,[{key:"handleEvent",value:function(t){switch(t.type){case"touchstart":this.onTouchStart(t);break;case"touchmove":this.onTouchMove(t)}}},{key:"onTouchStart",value:function(t){this.isScrolling=!1,this.deltaX=0,this.start={pageX:t.touches[0].pageX,pageY:t.touches[0].pageY},this.element.style.MozTransitionDuration=this.element.style.webkitTransitionDuration=this.speed+"ms",this.startDistance=this.distance,t.stopPropagation()}},{key:"onTouchMove",value:function(t){t.touches.length>1||t.scale&&1!==t.scale||(this.deltaX=t.touches[0].pageX-this.start.pageX,Math.abs(this.deltaX)>Math.abs(t.touches[0].pageY-this.start.pageY)?this.isScrolling=!0:this.isScrolling=!1,this.isScrolling&&(this.distance=this.startDistance-this.deltaX,this.distance<0?this.distance=0:this.distance>this.element.clientWidth-windowW&&(this.distance=this.element.clientWidth-windowW),this.element.style.left=-this.distance+"px",t.stopPropagation()))}}]),t}();new Vue({el:"#vueCon",data:{tableUrl:"/fanbei-web/partActivityInfo?modelId="+modelId,content:[],moreHref:"getMore?modelId="+modelId+"&subjectId=",divTop:"",option:{sort:sort}},created:function(){this.logData()},methods:{handleScroll:function(){jQuery(window).scrollTop()>=this.divTop?jQuery("#navWrap").addClass("fixTop"):jQuery("#navWrap").removeClass("fixTop")},logData:function logData(){Vue.http.options.emulateJSON=!0;var self=this,op={data:JSON.stringify(self.option)};self.$http.post(self.tableUrl,op).then(function(res){self.content=eval("("+res.data+")"),console.log(self.content),self.$nextTick(function(){document.title=self.content.data.modelName,2==sort&&($("#vueCon").css("background-color","#4515aa"),$("#listAlert").css("background-color","#4515aa")),self.divTop=document.getElementById("navWrap").offsetTop,2==getBlatFrom()?(window.addEventListener("touchstart",this.handleScroll),window.addEventListener("touchmove",this.handleScroll),window.addEventListener("touchend",this.handleScroll)):window.addEventListener("scroll",this.handleScroll),new Swipe(document.getElementById("navWrap"))})},function(t){console.log(t)})}}});
//# sourceMappingURL=../../_srcmap/activity/06/selectedHome.js.map
