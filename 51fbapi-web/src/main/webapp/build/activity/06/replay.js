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
                    data:{'activityId':13},
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
            let self=this;
            let replayShare = getUrl("replayShare");
            if (replayShare == "replayShare") {
               window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            } else {
               window.location.href=self.content.notifyUrl+'&params={"goodsId":"'+id+'"}'
            }                       
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
            'shareAppContent': '我抢到了一款爆款商品!'+"</br>"+'点击查看',  // 分享的内容
            'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
            'shareAppUrl': ipUrl+'/fanbei-web/activity/replay?replayShare=replayShare&activityId='+13,  // 分享后的链接
            'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
            'sharePage': 'replay' // 分享的页面
          };
          var dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
          return dataStr;
        };
