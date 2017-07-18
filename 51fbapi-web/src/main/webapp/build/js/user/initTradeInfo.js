   var bid = getUrl("bid");

   let vm=new Vue({
    el:'#businessCode',
    data:{
        content:{}
    },
    created:function () {
        this.logData();
    },
    // methods:{
    //     logData(){
    //         //获取页面初始化信息
    //         let self=this;
    //         $.ajax({
    //                 type: 'post',
    //                 url: '/fanbei-web/initTradeInfo',
    //                 data:{bid:'d0pmtf8U7Ml/D4DoHN7Brw=='},
    //                 success:function(data) {   
    //                     console.log(data);
    //                    self.content = eval('(' + data + ')');                      
    //                    self.content = self.content.data;  
    //                   console.log(self.content)

    //                 }, 
    //                 error:function(){
    //                    requestMsg("请求失败");
    //                 }
    //             });
    //     }
    // },
     buyNow(){
         var amount=$('.paymoney').text();
         window.location.href='/fanbei-web/opennative?name=APP_PAY'+'&params={"amount":"'+amount+'"}';
        },
   })

    function alaShareData(){
          // 分享内容
          var ipUrl=domainName();
          var dataObj = {
            'appLogin': 'N', // 是否需要登录，Y需要，N不需要
            'type': 'share', // 此页面的类型
            'shareAppTitle': '特卖会',  // 分享的title
            'shareAppContent': '51返呗返场加购，精选好货抄低价！爆款精品仅在“特卖会”，拼的就是手速，赶紧来围观~',  // 分享的内容
            'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
            'shareAppUrl': ipUrl+'/fanbei-web/activity/superGoods?superGoodsShare=superGoodsShare&activityId='+activityId,  // 分享后的链接
            'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
            'sharePage': 'superGoods' // 分享的页面
          };
          var dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
          return dataStr;
        };
