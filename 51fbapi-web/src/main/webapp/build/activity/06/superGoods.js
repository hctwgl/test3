/**
 * Created on 2017/6/20.
 */
let vm=new Vue({
    el:'#superGoods',
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
                      self.content = self.content.data;  
                      console.log(self.content)
                      var currentTime = self.content.currentTime;
                      var validStartTime = self.content.validStartTime;
                      var validEndTime = self.content.validEndTime;
                      var diff=0; 
                      var timer; 

                      //活动时间倒计时-----------
                        function showTime(word,time,className){
                            diff=parseInt((time-currentTime)/1000);
                            var day=0,
                                hour=0,
                                minute=0,
                                second=0;                           
                            if(diff > 0){
                              day = Math.floor(diff / (60 * 60 * 24));
                              hour = Math.floor(diff / (60 * 60)) - (day * 24);
                              minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
                              second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
                            }                        
                            if (minute <= 9) {minute = '0' + minute;}
                            if (second <= 9) {second = '0' + second;}
                            $('.'+className).html(word+'：'+day+'天'+hour+'时'+minute+'分'+second+'秒'); 
                        }//------------倒计时

                        function interval(word01,word02,start,end,className){
                           if(currentTime<start){                          
                                showTime(word01,start,className);                            
                                timer=setInterval(function(){                                
                                    start-=1000;
                                    showTime(word01,start,className);
                                }, 1000);                            
                            }else{       
                                showTime(word02,end,className);
                                timer=setInterval(function(){
                                    end-=1000;
                                    showTime(word02,end,className);
                                    if(diff==0){
                                      //clearInterval(timer)
                                      //$('.'+className).remove()
                                      this.logData();
                                    }
                                }, 1000);                            
                            }
                        }//定时器
                       interval('距活动开始','距活动结束',validStartTime,validEndTime,'countTime');

                      //商品列表---activityGoodsList
                      var activityGoodsList=self.content.activityGoodsList;                     
                      for(var i=0;i<activityGoodsList.length;i++){                        
                          (function(j){
                            let dom='productCountTime'+j;
                            //console.log(dom)
                              setTimeout(function timer(){
                              interval('距开始仅剩','距结束仅剩',activityGoodsList[j].startTime,activityGoodsList[j].validEndTime,dom);
                            },j*0)
                          })(i)   
                      }//商品列表---activityGoodsList---end

                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
        },
        alaShareData(){
          // 分享内容
          var dataObj = {
            'appLogin': 'N', // 是否需要登录，Y需要，N不需要
            'type': 'share', // 此页面的类型
            'shareAppTitle': '特卖会',  // 分享的title
            'shareAppContent': '51返呗返场加购，精选好货抄低价！爆款精品仅在“特卖会”，拼的就是手速，赶紧来围观~',  // 分享的内容
            'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
            'shareAppUrl': 'https://app.51fanbei.com/fanbei-web/activity/superGoods?superGoodsShare=superGoodsShare',  // 分享后的链接
            'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
            'sharePage': 'superGoods' // 分享的页面
          };
          var dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
          return dataStr;
        },
        buyNow(){
            var superGoodsShare = getUrl("superGoodsShare");
            if (superGoodsShare != "superGoodsShare") {
               window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }
        }
        
    }
})