/**
 * Created on 2017/07/10.
 */
var activityId = getUrl("activityId");
let vm=new Vue({
    el:'#robCoupon',
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
                    data:{'activityId':activityId},
                    success:function(data) {                      
                      self.content = eval('(' + data + ')');                      
                      self.content = self.content.data;  
                      console.log(self.content)
                      var currentTime = self.content.currentTime;
                      //var validStartTime = self.content.validStartTime;
                      //var validEndTime = self.content.validEndTime;
                      console.log(currentTime)
                      var validStartTime = 1499731200000;
                      var validEndTime = 1500009315000;
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
                              console.log(hour+minute+second)
                      }
                      if(diff<0){
                        showTime(validStartTime);
                      }else{
                        showTime(validStartTime);
                      }
                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
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
