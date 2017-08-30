/**
 * Created by yoe on 2017/6/11.
 */

var userName = "";
if(getInfo().userName){
    userName=getInfo().userName
};

new Vue({
  el: '#strollHomeWrap',
  methods:{
    boluomiHome: function(shopId){ // 点击进入菠萝蜜专场
      $.ajax({
        url: "/fanbei-web/getBrandUrl",
        type: "POST",
        dataType: "JSON",
        data:{
          'shopId': shopId,
          'userName': userName
        },
        success: function(returnData){
          if(returnData.success){
            location.href=returnData.url;
          }else{
            location.href=returnData.url;
          }
        },
        error: function(){
          requestMsg("请求失败");
        }
      });
    },
    boluomiCoupons: function(sceneId){ // 点击领取优惠券
      console.log(33333);
      $.ajax({
        url: '/fanbei-web/pickBoluomeCoupon',
        type: 'POST',
        dataType: "JSON",
        data:{
          'sceneId': sceneId,
          'userName': userName
        },
        success: function(returnData){
          // returnData = eval('(' + returnData + ')');
          if(returnData.success){
            requestMsg("领劵成功");
            //$(".receive").text("已领取");
            //window.location.reload();
          }else{
            if(returnData.url){
              location.href=returnData.url;
            }else{
              requestMsg(returnData.msg);
            }
          }
        }
      });
    }
  }
});
