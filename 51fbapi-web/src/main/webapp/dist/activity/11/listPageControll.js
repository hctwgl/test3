"use strict";var groupId=getUrl("groupId"),modelId=getUrl("modelId"),imgrooturl="https://f.51fanbei.com/h5/app/activity/11",windowW=$(window).outerWidth(),page=1,finished=0,vm=new Vue({el:"#listPageControll",data:{content:"",isShow:!0,m:"",c:"",tab:1,allStartTime:"",productList:"",productListDetail:"",flag:!0,categoryList:"",leimu:"",detailDes:"",categoryId:"",arr:[],index:"",return:"",page:"",pageNo:1,getId:""},created:function(){this.category(),this.scrollFn()},methods:{category:function(){var t=this;$.ajax({type:"post",url:"http://testsupplier.51fanbei.com/category/h5/list ",success:function(e){var o=e;console.log(o,"categoryList"),t.leimu=o.data;for(var a=[],i=0;i<t.leimu.length;i++){var s=t.leimu[i].rid;a.push(s)}t.arr=a,1e3!==e.code&&requestMsg(e.msg),t.logData(t.arr[0])},error:function(){requestMsg("哎呀，出错了！")}})},logData:function(t){var e=this;console.log(e.pageNo),console.log(e.detailDes,">>>>>>>"),$.ajax({type:"post",url:"http://testsupplier.51fanbei.com/goods/h5/list",data:{categoryId:t,pageNo:e.pageNo},success:function(t){var o=t;console.log(o.data,"productList"),o.data.length>0?(e.detailDes=e.detailDes.concat(o.data),e.detailDes=o.data,console.log(e.detailDes,"detailDes"),e.page=o.pageNo,e.pageNo=e.page):$(".nomore").show()},error:function(){requestMsg("哎呀，出错了！")}})},scrollFn:function(){var t=this;$(window).scroll(function(){var e=$(this).scrollTop(),o=$(document).height(),a=$(this).height();console.log(a,"windowHeight"),e+a>=o&&(t.pageNo++,t.logData(t.getId))})},goodClick:function(t){"SELFSUPPORT"==t.source?window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+t.rid+'"}':window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+t.rid+'"}'},tabClick:function(t){this.tab=t+1,this.getId=this.arr[t],console.log(this.getId,"getId");var e=this;$.ajax({type:"post",url:"http://testsupplier.51fanbei.com/goods/h5/list",data:{categoryId:this.arr[t],pageNo:1},success:function(t){var o=t;console.log(o,"productList"),e.detailDes=o.data,console.log(e.detailDes,"detailDes")},error:function(){requestMsg("哎呀，出错了！")}})}}});
//# sourceMappingURL=../../_srcmap/activity/11/listPageControll.js.map
