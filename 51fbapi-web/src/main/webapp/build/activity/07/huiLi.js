let modelId = getUrl("modelId");
console.log(modelId)
//获取数据
let vm = new Vue({
    el: '#huiLi',
    data: {
        couponCont:{},
        content: {},
        list01:{},
        list02:{},
        list03:{},
        first:{},
        second:{}
    },
    created: function () {
        this.logCoupon();
        this.logContent();
    },
    methods: {
        logCoupon() {
            //获取页面初始化信息
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/activityCouponList",
                success: function (data) {
                    self.couponCont = eval('(' + data + ')').data;
                    console.log(self.couponCont);                   
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
            
        },
        couponClick:function(e){
            let self=this;
            var couponId=e.couponId;
            //点击领券
            console.log(couponId)
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId:couponId
                },
                success: function(returnData){
                    if(returnData.success){
                       requestMsg("优惠劵领取成功");                          
                    }else{
                        console.log(returnData)
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
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                        }  
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
            
        },
        logContent(){
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo?modelId="+modelId,
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    self.list01=self.content.activityList[0].activityGoodsList;
                    self.list02=self.content.activityList[1].activityGoodsList;
                    self.list03=self.content.activityList[2].activityGoodsList;
                    self.first.goodsId=self.list01[0].goodsId;
                    self.first.source=self.list01[0].source;                    
                    self.second.goodsId=self.list02[0].goodsId;
                    self.second.source=self.list02[0].source;
                    self.list01=self.list01.slice(1);
                    self.list02=self.list02.slice(1);
                    console.log(self.first)  
                    console.log(self.second)            
                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },
        productClick(item){
            console.log(item);
            if ( item.source=="SELFSUPPORT" ) {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"'+item.goodsId+'"}'
            } else {
                window.location.href='/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"'+item.goodsId+'"}'
            }
        },
        txtFix(i,l){
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
            return cut_str(i, l)
        }
    }
})
