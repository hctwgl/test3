
var num;
//获取数据
let vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {},
        finalPrizeMask:''
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
                        }else{
                            self.finalPrizeMask=false;
                        }
                    }//判断终极大奖蒙版

                }
            })
        },
        //点击优惠券
        couponClick:function(e){
            var sceneId=e.sceneId;
                $.ajax({
                    url: "/fanbei-web/pickBoluomeCouponForApp",
                    type: "POST",
                    dataType: "JSON",
                    data: {'sceneId':sceneId,'userName':15839790051},
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
        //点击我要赠送卡片
        presentClick:function(){
            $.ajax({
                type: 'get',
                url: "/H5GG/sendItems",
                data:{activityId:1,userName:15839790051},
                success: function (returnData) {
                    console.log(returnData)
                    if(returnData.data.loginUrl!=''){
                        location.href = returnData.data.loginUrl;
                    }else{
                        if(num<2){
                            requestMsg(returnData.msg)
                        }else{
                            $('.alertPresent').css('display','block');
                            $('.mask').css('display','block');
                            $('.presentTitle').css('display','block');
                            $('.sure').html('确定赠送');
                        }

                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            })
        },//点击我要赠送卡片
        //点击我要索要卡片
        demandClick:function(){
            $('.alertPresent').css('display','block');
            $('.mask').css('display','block');
            $('.demandTitle').css('display','block');
            $('.sure').html('确定索要');
            let self = this;
            $.ajax({
                type: 'get',
                url: "/H5GG/askForItems",
                data:{activityId:1,userName:15839790051},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(data);
                }
            })
        },//点击我要索要卡片
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
