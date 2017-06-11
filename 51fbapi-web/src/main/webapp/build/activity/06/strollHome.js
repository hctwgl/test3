/**
 * Created by yoe on 2017/6/11.
 */

var userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
    console.log(userName);
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
            SrequestMsg(returnData.msg);
          }
        },
        error: function(){
          requestMsg("请求失败");
        }
      });
    }
  }
});
