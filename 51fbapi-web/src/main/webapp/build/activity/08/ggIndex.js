var cardLength;
var num;//卡片数量
//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {},
        finalPrizeMask:'',
        present:''
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GG/initHomePage",
                data:{'activityId':1,userName:15839790051},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    wordMove();//左右移动动画
                    var couponList=self.content.boluomeCouponList;
                    for(var i=0;i<couponList.length;i++){
                        couponList[i] = eval("("+couponList[i]+")");
                    }
                    for(var j=0;j<self.content.itemsList.length;j++){//判断终极大奖蒙版
                        num=self.content.itemsList[j].num;
                        if(num==0){
                            self.finalPrizeMask=true;
                        }else if(num==1){
                            self.finalPrizeMask=false;
                        }else {
                            self.finalPrizeMask=false;
                            self.present='Y';
                        }
                    }//判断终极大奖蒙版
                }
            })
        },
        //点击优惠券
        couponClick:function(e){
            var sceneId=e.sceneId;
                $.ajax({
                    url: "/fanbei-web/pickBoluomeCouponV1",
                    type: "POST",
                    dataType: "JSON",
                    data: {'sceneId':sceneId,userName:15839790051},
                    success: function (returnData){
                        console.log(returnData)
                        if(returnData.success){
                            requestMsg(returnData.msg);
                        }else{
                            location.href=returnData.url;
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                });

        },
        //点击卡片
        cardClick:function(e){
            var shopId=e.refId;
            $.ajax({
                type: 'post',
                url: '/fanbei-web/getBrandUrl',
                data:{'shopId':shopId,'userName':15839790051},
                dataType:'JSON',
                success: function (returnData) {
                    console.log(returnData)
                    if(returnData.success){
                        location.href=returnData.url;
                    }else{
                        location.href=returnData.url;
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            })
        },
        //点击获取终极大奖
        finalPrize:function(){
            let self = this;
            console.log(self.finalPrizeMask)
            if(self.finalPrizeMask){
                $.ajax({
                    type: 'get',
                    url: '/H5GGShare/pickUpSuperPrize',
                    data:{'activityId':1,'userName':15839790051},
                    dataType:'JSON',
                    success: function (returnData) {
                        if(returnData.success){
                            requestMsg(returnData.msg)
                        }
                    },
                    error: function(){
                        requestMsg("请求失败");
                    }
                })
            }

        },
        ruleClick:function(){
            $('.alertRule').css('display','block');
            $('.mask').css('display','block');
        },
        close:function(){
            $('.alertPresent').css('display','none');
            $('.mask').css('display','none');
            $('.title').css('display','none');
            $('.alertRule').css('display','none');
            $('.mask').css('display','none');
        }
    }
})
