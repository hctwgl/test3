"use strict";var groupId=getUrl("groupId"),modelId=getUrl("modelId"),imgrooturl="https://f.51fanbei.com/h5/app/activity/11",windowW=$(window).outerWidth(),page=1,finished=0,vm=new Vue({el:"#listPageControll",data:{content:"",isShow:!0,m:"",c:"",tab:1,allStartTime:"",productList:"",productListDetail:"",flag:!0,categoryList:"",leimu:"",detailDes:[],categoryId:"",arr:[],index:"",return:"",page:"",pageNo:1,getId:""},created:function(){this.category(),this.scrollFn()},methods:{category:function(){var t=this;$.ajax({type:"post",url:"https://supplier.51fanbei.com/category/h5/list ",success:function(e){var a=e;t.leimu=a.data;for(var o=[],i=0;i<t.leimu.length;i++){var r=t.leimu[i].rid;o.push(r)}t.arr=o,1e3!==e.code&&requestMsg(e.msg),t.logData(t.arr[0])},error:function(){requestMsg("哎呀，出错了！")}})},logData:function(t){var e=this;$.ajax({type:"post",url:"https://supplier.51fanbei.com/goods/h5/list",data:{categoryId:t,pageNo:e.pageNo},success:function(t){var a=t;a.data.length>0?(console.log(e.detailDes,"detailDes"),e.detailDes=e.detailDes.concat(a.data),console.log(e.detailDes,"111"),e.page=a.pageNo,e.pageNo=e.page):$(".nomore").show()},error:function(){requestMsg("哎呀，出错了！")}})},scrollFn:function(){var t=this;$(window).scroll(function(){var e=$(this).scrollTop(),a=$(document).height();e+$(this).height()>=a&&(t.pageNo++,t.logData(t.getId))})},goodClick:function(t){"SELFSUPPORT"==t.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.rid+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t.rid+'"}'},tabClick:function(t){this.tab=t+1,this.getId=this.arr[t];var e=this;this.detailDes=[],$.ajax({type:"post",url:"https://supplier.51fanbei.com/goods/h5/list",data:{categoryId:this.arr[t],pageNo:1},success:function(t){var a=t;e.detailDes=a.data},error:function(){requestMsg("哎呀，出错了！")}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/listPageControll.js.map
