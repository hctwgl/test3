/**
 * Created on 2017/6/20.
 */
let vm=new Vue({
    el:'#superGoods',
    data:{
        content:{},
        activityTxt:''
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
                      self.activityTxt=self.setTime(validStartTime,validEndTime,currentTime)
                      //   var i=0;
                      // function getTime(){
                      // var activityGoodsList=self.content.activityGoodsList;

                      //    if(i<activityGoodsList.length){
                      //       var activityValidEndTime=activityGoodsList[i].validEndTime;
                      //       var activityValidStartTime=activityGoodsList[i].validStartTime;
                      //       interval('距本产品开始','距本产品结束',activityValidStartTime,activityValidEndTime,'productCountTime');    
                      //       i++;
                      //       getTime();
                      //    }
                      //    return false
                      // }
                      // getTime();
                      //  interval('距活动开始','距活动结束',validStartTime,validEndTime,'countTime');
            
                      
       
                      /*for(var i=0;i<activityGoodsList.length;i++){
                          var activityValidEndTime=activityGoodsList[i].validEndTime;
                          var activityValidStartTime=activityGoodsList[i].validStartTime;
                          interval('距本产品开始','距本产品结束',activityValidStartTime,activityValidEndTime,'productCountTime');    
                      }//商品列表---activityGoodsList---end*/
                      
                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
        },
        setTime(start,end,current){
          let self=this;
          var timer,diff=0;
          var con='' ;
            //活动时间倒计时--------,,---
          function showTime(time){
              diff=parseInt((time-current)/1000);
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
              return day+'天'+hour+'时'+minute+'分'+second+'秒';
              //console.log($('.countTime').html());   
          }//------------倒计时
             if(current<start){                          
                  self.activityTxt=showTime(start);                            
                  timer=setInterval(function(){                                
                      start-=1000;
                      self.activityTxt=showTime(start);
                      console.log(self.activityTxt)  
                  }, 1000);                            
              }else{       
                  con=showTime(end);
                  timer=setInterval(function(){
                      end-=1000;
                      con=showTime(end);
                      console.log(con) 
                      //console.log(diff)
                      if(diff==0){
                        clearInterval(timer)
                        self.logData()
                      }
                  }, 1000);                            
              }
              return con
        }
        
    }
})