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
                    type: 'get',
                    url: '/fanbei-web/encoreActivityInfo',
                    data:{'activityId':123},
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
                        function showTime(word,time){
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
                            $('.countTime').html(word+'：'+day+'天'+hour+'时'+minute+'分'+second+'秒');
                            console.log($('.countTime').html());   
                        }//------------倒计时
                        if(currentTime<validStartTime){                          
                            showTime('距活动开始',validStartTime);                            
                            timer=setInterval(function(){                                
                                validStartTime-=1000;
                                showTime('距活动开始',validStartTime);
                            }, 1000);                            
                        }else{       
                            showTime('距活动结束',validEndTime);
                            timer=setInterval(function(){
                                validEndTime-=1000;
                                showTime('距活动结束',validEndTime);
                            }, 1000);
                            if(validEndTime==currentTime){
                                clearInterval(timer)
                               $('.countTime').remove()
                            }
                        }
                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
        }
        
    }
})