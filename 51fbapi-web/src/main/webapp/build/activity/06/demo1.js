 var activityId = getUrl("activityId");
let vm=new Vue({
    el:'#replay',
    data:{
        content:{}
    },
    created:function () {
       
        this.logData();
    },
    methods:{
        logData(){
            //获取页面初始化信息
            let self=this;
            $.ajax({
                    type: 'post',
                    url: '/fanbei-web/encoreActivityInfo',
                    data:{'activityId':4},
                    success:function(data) {                      
                      self.content = eval('(' + data + ')');  
                      console.log(self.content)                    
                      self.content = self.content.data;  
                      console.log(self.content)
                      console.log(self.content.recommendGoodsList[0].goodsId)
                     
                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }                     
                });                
        },
        show:function(id){
            var self=this;                       
            window.location.href=self.content.notifyUrl+'&params={"goodsId":"'+id+'"}';
            // window.location.reload()
        } 
   
    }
});

function alaShareData(){
          // 分享内容
          var ipUrl=domainName();
          var dataObj = {
            'appLogin': 'N', // 是否需要登录，Y需要，N不需要
            'type': 'share', // 此页面的类型
            'shareAppTitle': '人气爆款专场',  // 分享的title
            'shareAppContent': '51返呗返场加购，精选好货抄低价！爆款精品仅在“特卖会”，拼的就是手速，赶紧来围观~',  // 分享的内容
            'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
            'shareAppUrl': ipUrl+'/fanbei-web/activity/replay?&activityId='+activityId,  // 分享后的链接
            'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
            'sharePage': 'replay' // 分享的页面
          };
          var dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
          return dataStr;
        };
