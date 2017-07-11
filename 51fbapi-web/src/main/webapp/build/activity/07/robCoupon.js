/**
 * Created on 2017/07/10.
 */
var activityId = getUrl("activityId");
//console.log(activityId)
let vm=new Vue({
    el:'#robCoupon',
    data:{
        content:{},
        couponContent:{}
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
                    data:{'activityId':activityId},
                    success:function(data) {                      
                      self.content = eval('(' + data + ')');                      
                      self.content = self.content.data;  
                      console.log(self.content)
                      var currentTime = self.content.currentTime;//2017/7/11 13:58:49
                      var validStartTime = self.content.validStartTime+3600*10*1000;//活动开始准确时间2017/7/12 10:0:0
                      var startMore= self.content.validStartTime+3600*24*1000;//活动开始一天；
                      var validEndTime = self.content.validEndTime;//活动结束时间
                      var date=new Date(currentTime);//当前日期
                      var year=date.getFullYear();
                      var month=date.getMonth()+1;
                      var day=date.getDate();
                      var dateStr01=year+'/'+month+'/'+day+' '+'10:00:00'; //今天10:00                      
                      var dateStr02=year+'/'+month+'/'+day+' '+'16:34:00'; //今天16:00
                      var dateStr03=year+'/'+month+'/'+(day+1)+' '+'10:00:00'; //明天10:00
                      var currentTen= new Date(dateStr01).getTime();                         
                      var currentFourteen= new Date(dateStr02).getTime();
                      var nextTen= new Date(dateStr03).getTime();                                          
                      //console.log(currentTime)
                      //console.log(validStartTime)
                      //console.log(validEndTime)
                      var diff=0;
                      function showTime(time){
                          diff=parseInt((time-currentTime)/1000);
                          var hour=0,
                              minute=0,
                              second=0;                           
                            hour = Math.floor(diff / (60 * 60));
                            minute = Math.floor(diff / 60) - (hour * 60);
                            second = Math.floor(diff) - (hour * 60 * 60) - (minute * 60);
                              if (hour <= 9) {hour = '0' + hour;}
                              if (minute <= 9) {minute = '0' + minute;}
                              if (second <= 9) {second = '0' + second;}
                              $('.countTime').find('span').eq(0).html(hour);
                              $('.countTime').find('span').eq(1).html(minute);
                              $('.countTime').find('span').eq(2).html(second);
                      }
                      function interval(start,currentTen,currentFourteen,nextTen,end){
                            if(currentTime<start){
                                //活动未开始
                                start-=1000;
                                showTime(start);
                                $('.timeName').html('10:00');                                         
                            }else if(currentTime>=start&&currentTime<end){
                                if(currentTime<currentTen){
                                    currentTen-=1000;
                                    showTime(currentTen);
                                    $('.timeName').html('10:00');
                                }else if(currentTime>=currentTen&&currentTime<currentFourteen){
                                    currentFourteen-=1000;
                                    showTime(currentFourteen);
                                    $('.timeName').html('10:00');
                                }else if(currentTime>=currentFourteen){
                                    nextTen-=1000;
                                    showTime(nextTen);
                                    $('.timeName').html('14:00');
                                }
                            }else{
                                //活动结束
                                $('.timeName').html('14:00');
                                $('.desWord').find('p').eq(2).hide();
                                $('.countTime').hide();
                            }
                            //倒计时                           
                            let time1=setInterval(function(){
                                if(currentTime<start){
                                    //活动未开始
                                    start-=1000;
                                    showTime(start);
                                    $('.timeName').html('10:00');                                         
                                }else if(currentTime>=start&&currentTime<end){
                                    if(currentTime<currentTen){
                                        currentTen-=1000;
                                        showTime(currentTen);
                                        $('.timeName').html('10:00');
                                    }else if(currentTime>=currentTen&&currentTime<currentFourteen){
                                        currentFourteen-=1000;
                                        showTime(currentFourteen);
                                        $('.timeName').html('10:00');
                                    }else if(currentTime>=currentFourteen){
                                        nextTen-=1000;
                                        showTime(nextTen);
                                        $('.timeName').html('14:00');
                                    }
                                }else{
                                    //活动结束
                                    $('.timeName').html('14:00');
                                    $('.desWord').find('p').eq(2).hide();
                                    $('.countTime').hide();
                                }
                            }, 1000);

                        }//定时器
                       interval(validStartTime,currentTen,currentFourteen,nextTen,validEndTime);
                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
            $.ajax({
              type: 'post',
              url: '/fanbei-web/superCouponList',
              success:function(data) {                      
                self.couponContent = eval('(' + data + ')');                      
                self.couponContent = self.couponContent.data;  
                console.log(self.couponContent)                
              }, 
              error:function(){
                 requestMsg("请求失败");
              }
            })
        },
        buyNow(id){
          let self=this;            
          window.location.href=self.content.notifyUrl+'&params={"goodsId":"'+id+'"}'
        },
        ruleClick(){
          $('.mask').show();
          $('.rule').show();
        },
        maskClick(){
          $('.mask').hide();
          $('.rule').hide();
        },
        couponClick(index){
           let self=this;    
           var couponIdNum=self.couponContent.couponInfoList[index].couponId;
           var userName=self.couponContent.userName;
           //console.log(couponIdNum)
           $.ajax({
                    url: "/fanbei-web/pickCoupon",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        couponId: couponIdNum,
                        userName: userName
                    },
                    success: function(returnData){
                        if (returnData.success) {
                            requestMsg("优惠劵领取成功");
                        } else {
                            var status = returnData.data["status"];
                            if (status == "USER_NOT_EXIST") { // 用户不存在
                                window.location.href = returnData.url;
                            }
                            if (status == "OVER") { // 优惠券个数超过最大领券个数
                                requestMsg(returnData.msg);
                                requestMsg("优惠券个数超过最大领券个数");
                            }
                            if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                                requestMsg(returnData.msg);
                            }
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
               });
        },
        txtFix(i){
            function get_length(s){
                var char_length = 0;
                for (var i = 0; i < s.length; i++){
                    var son_char = s.charAt(i);
                    encodeURI(son_char).length > 2 ? char_length += 1 : char_length += 0.5;
                }
                return char_length;
            }
            function cut_str(str, len){
                var char_length = 0;
                for (var i = 0; i < str.length; i++){
                    var son_str = str.charAt(i);
                    encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
                    if (char_length >= len){
                        var sub_len = char_length == len ? i+1 : i;
                        return str.substr(0, sub_len);
                        break;
                    }
                }
            }
            return cut_str(i, 20)
        }
        
    }
});
