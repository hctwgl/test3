let modelId = getUrl("modelId");
let vm = new Vue({
    el: '#gamePoint',
    data: {
        content: {},
        isActive: true
    },
    created: function () {
        this.logData();
    },
    methods: {
        logData() {
            //获取页面初始化信息
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo?modelId="+modelId,
                data: {
                    'modelId': modelId
                },
                success: function (data) {
                    //   self.content = ; 
                    //    console.log(self.content)
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content.activityList[0].activityGoodsList);
                    console.log(self.content.activityList[1]);

                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
        //点击tab栏切换
        isshow: function () {
           
            this.isActive = !this.isActive;

        },
        buyNow(id) {
            let self = this
            var gamePointShare = getUrl("gamePointShare");
            if (gamePointShare == "gamePointShare") {
                window.location.href = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            } else {
                //  alert( self.content.activityList + '&params={"modelId":"' + id + '"}');
                window.location.href = '/fanbei-web/opennative?params={"modelId":"' + id + '"}'
               
            }
        },
    }
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
            'shareAppUrl': ipUrl+'/fanbei-web/activity/gamePoint?gamePointShare=gamePointShare&modelId='+modelId,  // 分享后的链接
            'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
            'sharePage': 'superGoods' // 分享的页面
          };
          var dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
          return dataStr;
        };