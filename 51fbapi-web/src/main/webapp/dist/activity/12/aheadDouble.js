"use strict";var protocol=window.location.protocol,host=window.location.host,urlHost=protocol+"//"+host,groupId=getUrl("groupId"),goodArr1=[{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/apple.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=197",name:"苹果"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/vivo.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=195",name:"vivo"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/oppo.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=193",name:"oppo"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/xiaomi.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=203",name:"小米"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/huawei.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=204",name:"华为"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/honer.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=205",name:"荣耀"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/meizu.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=213",name:"魅族"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/telephone/meitu.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=202",name:"美图"}],goodArr2=[{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/sony.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=219",name:"SONY索尼专题"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/nikan.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=215",name:"Nikon尼康专题"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/canon.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=212",name:"Canon佳能专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/acer.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=211",name:"宏碁(acer)"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/sansung.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=210",name:"三星（SAMSUNG）"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/hp.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=209",name:"惠普(HP)"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/vsus.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=208",name:"华硕（ASUS）"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/lenove.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=207",name:"联想（Lenovo）"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/dell.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=206",name:"戴尔（DELL）"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/computer/huawei.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=221",name:"huawei"}],goodArr3=[{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/DW.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=191",name:"DW品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/tiansuo.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=192",name:"天梭品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/CASIO.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=194",name:"卡西欧品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/armani.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=201",name:"阿玛尼品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/Rossini.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=200",name:"罗西尼品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/Longines.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=199",name:"浪琴品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/SWAROVSKI.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=190",name:"施华洛世奇品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/zhekun.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=189",name:"喆堃珠宝品牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/sports.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=196",name:"服饰运动潮牌专场"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/watch/MK.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=198",name:"MK品牌专场"}],goodArr4=[{img:"https://f.51fanbei.com/h5/app/activity/12/brand/household/supor.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=214",name:"家电-苏泊尔 "},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/household/liren.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=216",name:"家电-利仁"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/household/sansung.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=217",name:"家电-三星"},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/household/media.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=218",name:"家电-美的 "},{img:"https://f.51fanbei.com/h5/app/activity/12/brand/household/jiuyang.png",link:"https://app.51fanbei.com/app/goods/goodsListModel?modelId=220",name:"家电-九阳"}],userName=getUrl("userName"),spread=getUrl("spread"),vm=new Vue({el:"#main",data:{goodsData:[],tabs:[{tab:"手机",key:0},{tab:"数码电脑",key:1},{tab:"手表配饰",key:2},{tab:"家电",key:3}],goods:[goodArr1,goodArr2,goodArr3,goodArr4],pretab:0,nowkey:0,couponData:[],currentData:[],redRainData:[],couponFlag:!0,touchstartx:null,touchendx:null,touchdefx:null,touchstarty:null,touchendy:null,touchdefy:null,isApp:!0,couponUrl:"/activity/double12/couponHomePage"},created:function(){this.isAppFn(),this.maidian("enter=true")},watch:{nowkey:function(o){var t=-$(".rule").width();this.$nextTick(function(){$(".goodpi").css({transform:"translateX("+t*o+"px)"})})},touchdefx:function(o){this.isScroll&&(o>80?this.nowkey<this.goods.length-1&&this.nowkey++:o<-80&&this.nowkey>0&&this.nowkey--)}},computed:{couponNum:function(){return this.firstGoods.couponList?this.firstGoods.couponList.filter(function(o,t){return 0!=o.state}).length:0},isScroll:function(){return Math.abs(this.touchdefy)<50}},mounted:function(){var o=this;this.$nextTick(function(){setTimeout(function(){$(".good_tab").pin({containerSelector:".good_con"}),$(".goodpi").css({width:$(".rule").width()*o.goods.length+"px"}),$(".goodeach").css({width:$(".rule").width()+"px"}),$(".goodpi").on("touchstart",function(t){var a=t.originalEvent.targetTouches[0];o.touchstartx=a.pageX,o.touchstarty=a.clientY}),$(".goodpi").on("touchend",function(t){var a=t.originalEvent.changedTouches[0];o.touchendx=a.pageX,o.touchendy=a.clientY,o.touchdefy=o.touchstarty-o.touchendy,o.touchdefx=o.touchstartx-o.touchendx})},0)})},methods:{changetab:function(o){this.pretab=this.nowkey,this.nowkey=o},linkto:function(o){this.maidian("good="+encodeURI(o.link)),this.isApp||this.toRegister(),window.location.href=o.link},isAppFn:function(){""!=getUrl("spread")&&(this.isApp=!1,this.couponUrl="/activityH5/double12/couponHomePage"),this.logData()},logData:function logData(){var self=this;$.ajax({type:"post",url:"/fanbei-web/activityCouponInfo",data:{groupId:groupId},success:function success(data){self.couponData=eval("("+data+")").data.couponInfoList,console.log(self.couponData)},error:function(){requestMsg("哎呀，获取优惠券出错了！")}})},couponClick:function(o,t){var a=this,n=o.couponId;if("Y"==o.drawStatus)return requestMsg("您已经领取过了，快去使用吧"),!1;$.ajax({url:"/fanbei-web/pickCoupon",type:"POST",dataType:"JSON",data:{couponId:n},success:function(o){if(console.log(o),o.success)requestMsg("优惠劵领取成功"),a.$set(a.couponData[t],"drawStatus","Y"),a.maidian("couponSuccess=true");else{var n=o.data.status;"USER_NOT_EXIST"==n&&(window.location.href=o.url),"OVER"==n&&(requestMsg("您已经领取过了，快去使用吧"),a.maidian("couponSuccess=got")),"COUPON_NOT_EXIST"==n&&(requestMsg(o.msg),a.maidian("couponSuccess=noCoupon")),"MORE_THAN"==n&&(requestMsg(o.msg),a.maidian("couponSuccess=end"))}},error:function(){requestMsg("哎呀，出错了！")}})},buy:function(o){this.isApp?this.toRegister():window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+o+'"}'},maidian:function(o){$.ajax({url:"/fanbei-web/postMaidianInfo",type:"post",data:{maidianInfo:"/fanbei-web/activity/aheadDouble?"+o},success:function(o){console.log(o)}})},toRegister:function(){location.href="doubleTwelveRegister?spread="+spread}}});
//# sourceMappingURL=../../_srcmap/activity/12/aheadDouble.js.map
