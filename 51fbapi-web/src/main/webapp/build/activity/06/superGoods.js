/**
 * Created on 2017/6/20.
 */
   var activityId = getUrl("activityId");
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
                    data:{'activityId':activityId},
                    success:function(data) {                      
                      self.content = eval('(' + data + ')');                      
                      self.content = self.content.data;  
                      console.log(self.content)
                      var currentTime = self.content.currentTime;
                      var validStartTime = self.content.validStartTime;
                      var validEndTime = self.content.validEndTime;
                      var diff=0;
                      //活动时间倒计时-----------
                        function showTime(word,time,className){
                            diff=parseInt((time-currentTime)/1000);
                            var day=0,
                                hour=0,
                                minute=0,
                                second=0;                           
                            if(diff > -1){
                              day = Math.floor(diff / (60 * 60 * 24));
                              hour = Math.floor(diff / (60 * 60)) - (day * 24);
                              minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
                              second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
                                if (minute <= 9) {minute = '0' + minute;}
                                if (second <= 9) {second = '0' + second;}
                                $('.'+className).html(word+'：'+day+'天'+hour+'时'+minute+'分'+second+'秒');
                            }                        

                        }//------------倒计时

                        function interval(word01,word02,start,end,className){
                            if(currentTime<start){
                                showTime(word01,start,className);
                            }else{
                                showTime(word02,end,className);
                            }
                                let time1=setInterval(function(){
                                    if(currentTime<start){
                                        start-=1000;
                                        showTime(word01,start,className);
                                    }else{
                                        end-=1000;
                                        showTime(word02,end,className);
                                        if(diff<=0){
                                            $('.'+className).parent().hide()
                                                     }
                                    }
                                }, 1000);

                                // timer=setInterval(function(){
                                //
                                //     if(diff==0){
                                //         window.clearInterval(timer);
                                //
                                //         self.logData();
                                //     }else{
                                //         end-=1000;
                                //         showTime(word02,end,className);
                                //     }
                                // }, 1000);

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
                            },0)
                          })(i)   
                      }//商品列表---activityGoodsList---end

                    }, 
                    error:function(){
                       requestMsg("请求失败");
                    }
                });
        },
        buyNow(id){
          let self=this
            var superGoodsShare = getUrl("superGoodsShare");
            if (superGoodsShare == "superGoodsShare") {
               window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            } else {
               window.location.href=self.content.notifyUrl+'&params={"goodsId":"'+id+'"}'
            }
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