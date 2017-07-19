var tabWidth=0;
var liWidth=0;
var ulWidth=0;
//获取数据
let vm = new Vue({
    el: '#couponCenter',
    data: {
        content: {}
    },
    created: function () {
        this.logData();        
    },
    methods: {
        logData() {
            //获取页面初始化信息
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/couponCategoryInfo",
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    console.log(self.content);
                    var liLength=self.content.couponCategoryList.length;
                    self.$nextTick(function () {
                        $('.navList li').eq(0).addClass('border');
                        $('.contList li').eq(0).addClass('active');                               //dom渲染完成后执行
                        
                        if(liLength<2){
                             $('#tabNav').hide();
                        }else if(liLength>=2&&liLength<=5){                      
                             tabWidth=$('#tabNav').width();
                             for(var n=0;n<liLength;n++){
                                liWidth+=$('.navList').find('li').eq(n).width();
                             }
                             var x=(tabWidth-liWidth)/(liLength+1);  //margin值
                             //console.log(x)
                             $('.navList').find('li').css('margin-left',x);                                              
                        }else{
                             tabWidth=$('#tabNav').width();
                             var fiveLiWidth=0;
                             for(var m=0;m<5;m++){
                                fiveLiWidth+=$('.navList').find('li').eq(m).width();
                             }
                             for(var n=0;n<liLength;n++){
                                liWidth+=$('.navList').find('li').eq(n).width();
                             }
                             var x=(tabWidth-fiveLiWidth)/6;  //margin值                           
                             $('.navList').find('li').css('margin-left',x);
                             $('.kong').css('width',x) 
                             ulWidth=liWidth+(liLength+1)*x;
                             $('.navList').width(ulWidth);
                             //console.log(liWidth)
                        }
                    })
                    //计算有效期与是否过期
                    var diff=0;
                    for(var i=0;i<liLength;i++){
                        var couponCategory=self.content.couponCategoryList[i];
                        //console.log(couponCategory.couponInfoList)  
                        for(var j=0;j<couponCategory.couponInfoList.length;j++){
                             var currentTime=couponCategory.couponInfoList[j].currentTime;
                             var startTime=couponCategory.couponInfoList[j].gmtStart;
                             var endTime=couponCategory.couponInfoList[j].gmtEnd;
                             couponCategory.couponInfoList[j].start=format(startTime);
                             couponCategory.couponInfoList[j].end=format(endTime);
                             diff=(endTime-currentTime)/1000;
                             var h=parseInt(diff/3600);
                             var state;
                             if(h<48){
                                state='timeOver';                                
                             }else{
                                state='noTimeOver';
                             }
                             couponCategory.couponInfoList[j].state=state;
                             //排序                            
                             for(var k = j + 1;k<couponCategory.couponInfoList.length;k++){
                                 if(couponCategory.couponInfoList[j].gmtEnd>couponCategory.couponInfoList[k].gmtEnd){
                                     var tmp = couponCategory.couponInfoList[j].gmtEnd;
                                     couponCategory.couponInfoList[j].gmtEnd = couponCategory.couponInfoList[k].gmtEnd;
                                     couponCategory.couponInfoList[k].gmtEnd = tmp;
                                 }
                             }
                             //console.log(couponCategory.couponInfoList)
                             //排序                           
                        }
                    }
                    function format(shijianchuo) { //shijianchuo是整数，否则要parseInt转换 
                        var time = new Date(shijianchuo); 
                        var y = time.getFullYear(); 
                        var m = time.getMonth()+1; 
                        var d = time.getDate();
                        if(m<10){m='0'+m} 
                        if(d<10){d='0'+d}
                        return y+'.'+m+'.'+d; 
                    } 

                },
                error: function () {
                    requestMsg("请求失败");
                }
            });
        },       
        liClick:function(index){
            //console.log(index);
            $('.navList li').eq(index).addClass('border');
            $('.navList li').eq(index).siblings().removeClass('border');
            $('.contList').find('li').eq(index).show().siblings().hide();         
        },
        couponClick:function(e){
            let self=this;
            var couponId=e.couponId;
            var shopUrl=e.shopUrl;
            var couponType=e.type;
            //只有现金券、满减券、会场券时，券状态有去用券 可点击跳转 其他不可
                if(e.isDraw=='N' && (couponType=='FULLVOUCHER' || couponType=='CASH' || couponType=='ACTIVITY')){                 
                    //去用券
                    if(shopUrl){
                        window.location.href=shopUrl;
                    }else{
                        window.location.href='/fanbei-web/opennative?name=APP_HOME';
                    }
                }else if(e.isDraw=='Y'){
                    //点击领券
                    //console.log(1)
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
                                   e.isDraw='N';                           
                                }else{
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
                                        $(".couponLi").eq(i).css('display', 'none');
                                    } 
                                }
                            },
                            error: function(){
                                requestMsg("请求失败");
                            }
                        });
                }     
        }//couponClick--

    }
})
