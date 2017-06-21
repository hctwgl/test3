/**
 * Created on 2017/6/20.
 */
let vm=new Vue({
    el:'#superGoods',
    data:{
        content:{},
        A:{}
    },
    created:function () {
        this.logData();
    },
    methods:{
        logData(){
            //获取页面初始化信息
            let self=this;
            $.ajax({
                    type: 'get',
                    url: '/fanbei-web/encoreActivityInfo',
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
                            //console.log($('.countTime').html());   
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
                                    //console.log(diff)
                                    if(diff==0){
                                      clearInterval(timer)
                                      $('.'+className).remove()
                                    }
                                }, 1000);                            
                            }
                        }
                       interval('距活动开始','距活动结束',validStartTime,validEndTime,'countTime');
                      //商品列表---activityGoodsList
                      var activityGoodsList=self.content.activityGoodsList;
                      console.log(activityGoodsList)
                      for(var i=0;i<activityGoodsList.length;i++){
                          var activityValidEndTime=activityGoodsList[i].validEndTime;
                          var activityValidStartTime=activityGoodsList[i].validStartTime;
                          interval('距本产品开始','距本产品结束',activityValidStartTime,activityValidEndTime,'productCountTime');    
                      }//商品列表---activityGoodsList---end

                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
        }
        
    }
})