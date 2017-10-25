
//活动日历显示
/*判断时间list*/ // 根据时间判断导航栏高亮
    let currentStarmp = new Date().getTime();
    let oneTime = Date.parse(new Date('2017/11/1 00:00:00'));
    let twoTime = Date.parse(new Date('2017/11/9 00:00:00'));
    let threeTime = Date.parse(new Date('2017/11/12 00:00:00'));
    let fourTime=Date.parse(new Date('2017/11/12 00:00:00'));
    let fiveTime=Date.parse(new Date('2017/11/11 00:00:00'))
    let sixTime=Date.parse(new Date('2017/11/13 10:00:00'))
    console.log(new Date(currentStarmp))
    if(currentStarmp<oneTime){
        addStyle(0);
    }
    if (currentStarmp>oneTime && currentStarmp<=threeTime){ //11.1-11.11
        addStyle(0);
    }
    if (currentStarmp >= twoTime && currentStarmp <= threeTime) { //11.9-11.11
        addStyle(1);
    }
    if (currentStarmp == fiveTime) { //11.11
        addStyle(2);
    }
    if (currentStarmp >= sixTime) { //11.13
        addStyle(2);
    }

    function addStyle(i) {
        $('.active').eq(i).addClass('active01');
        $('.active').eq(i).siblings().removeClass('active01');
       /*  $('.time').eq(i).find('span').addClass('active02');
        $('.time').eq(i).siblings().find('span').removeClass('active02'); */
        $('.tangle').eq(i).addClass('tangleOne')//添加三角
        $('.tangle').eq(i).siblings().removeClass('tangleOne')//移除三角
        $('.tangle>.tangleTwo').eq(i).hide();//隐藏显示的三角图片
        
    }



//全民倒计时
$(function(){
    // 结束时间的时间戳
    let endDate = new Date("oct 29,2017 23:59:59");
    let endStamp = endDate.valueOf();
    // 获取当前时间的时间戳
    let now = new Date();
    let nowTimeStamp = now.valueOf();
    // 相差的时间戳
    let differStamp = endStamp - nowTimeStamp;
    let intDiff = parseInt(differStamp/1000);//倒计时总秒数量

    function showTimerS( diff ){
        var day=0,
        hour=0,
        minute=0,
        second=0;//时间默认值

        if(diff > 0){
        day = Math.floor(diff / (60 * 60 * 24));
        hour = Math.floor(diff / (60 * 60)) - (day * 24);
        minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
        second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
        }
       /*  if (minute <= 9) minute = '0' + minute;
        if (second <= 9) second = '0' + second;
        $('#day_show').html(day+"天");
        $('#hour_show').html('<s id="h"></s>'+hour+'时');
        $('#minute_show').html('<s></s>'+minute+'分');
        $('#second_show').html('<s></s>'+second+'秒'); */

        $('.countTwo').html(day+"天"+" : "+hour+'时'+" : "+minute+'分'+" : "+second+'秒');//全民倒计时
        //顶部倒计时
        $('.blankOne').html(day);
        $('.blankTwo').html(hour);
        $('.blankThree').html(minute);
        $('.blankFour').html(second);
        //判断活动时间 活动开始前显示倒计时时间 活动中跳转红包雨活动主页 活动结束后显示活动已结束  点击无跳转
        if(day==0&&hour==0&&minute==0&&second==0){
            window.location.href = '/activity/barginIndex';
            $('.countTwo').html('活动已结束');

        }
        


    };

    function timer(intDiff){
        showTimerS(intDiff);
        intDiff--;
        window.setInterval(function(){
            showTimerS(intDiff);
            intDiff--;
        }, 1000);
    };
    timer(intDiff);


    //红包雨倒计时
    // 结束时间的时间戳
    let overDate = new Date("oct 28,2017 23:59:59");
    let endOver = overDate.valueOf();
    // 获取当前时间的时间戳
    let nowTime = new Date();
    let nowTimeS = nowTime.valueOf();
    // 相差的时间戳
    let differS = endOver - nowTimeS;
    let diffValue = parseInt(differS/1000);//倒计时总秒数量

    function showTime( ctime ){
        var day=0,
        hour=0,
        minute=0,
        second=0;//时间默认值

        if(ctime > 0){
        day = Math.floor(ctime / (60 * 60 * 24));
        hour = Math.floor(ctime / (60 * 60)) - (day * 24);
        minute = Math.floor(ctime / 60) - (day * 24 * 60) - (hour * 60);
        second = Math.floor(ctime) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
        }

        $('.countThree').html(day+"天"+" : "+hour+'时'+" : "+minute+'分'+" : "+second+'秒');//红包雨
        // $('.countThree').html('活动已结束');
        //判断活动时间 活动开始前显示倒计时时间 活动中跳转红包雨活动主页 活动结束后显示活动已结束  点击无跳转
        if(day==0&&hour==0&&minute==0&&second==0){
            window.location.href = '';
        }

    };

    function timeS(diffValue){
        showTime(diffValue);
        diffValue--;
        window.setInterval(function(){
            showTime(diffValue);
            diffValue--;
        }, 1000);
    };
    timeS(diffValue);

});



var groupId = getUrl("groupId"); //获取活动Id
var modelId=getUrl("modelId");//获取模板Id
var imgrooturl = "https://f.51fanbei.com/h5/app/activity/11";

let vm=new Vue({
    el:'#doubleEleven',
    data:{
        content: '',
        isShow:true,
        m:'',
        c:'',
        tab: 1,
        productList:'',
        productListDetail:'',
        allData :[{'name':'苹果','img': imgrooturl +'/brand-01.png','src':'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=187'},
                  {'name':'vivo/OPPO','img':imgrooturl +'/brand-02.png','src':'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=186'},
                  {'name':'华为','img':imgrooturl +'/brand-03.png','src':'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=185'},
                  {'name':'小米','img':imgrooturl +'/brand-04.png','src':'http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=183'},
                  {'name':'马克华菲','img':imgrooturl +'/brand-05.png','src':''},
                  {'name':'PLAYBOY','img':imgrooturl +'/brand-06.png','src':''},
                  {'name':'GUESS','img':imgrooturl +'/brand-07.png','src':''},
                  {'name':'拉夏贝尔','img':imgrooturl +'/brand-08.png','src':''},
                  {'name':'韩都衣舍','img':imgrooturl +'/brand-09.png','src':''},
                  {'name':'小米','img':imgrooturl +'/brand-10.png','src':''},
                  {'name':'NIKE','img':imgrooturl +'/brand-11.png','src':''},
                  {'name':'adidas','img':imgrooturl +'/brand-12.png','src':''},
                  {'name':'newbalance','img':imgrooturl +'/brand-13.png','src':''},
                  {'name':'鸿星尔克','img':imgrooturl +'/brand-14.png','src':''},
                  {'name':'宾卡达','img':imgrooturl +'/brand-15.png','src':''},
                  {'name':'DW','img':imgrooturl +'/brand-16.png','src':''},
                  {'name':'Dior','img':imgrooturl +'/brand-17.png','src':''},
                  {'name':'雅诗兰黛','img':imgrooturl +'/brand-18.png','src':''},
                  {'name':'lilbetter','img':imgrooturl +'/brand-19.png','src':''},
                  {'name':'速写','img':imgrooturl +'/brand-20.png','src':''},
                  {'name':'衣香丽影','img':imgrooturl +'/brand-21.png','src':''},
                  {'name':'妖精的口袋','img':imgrooturl +'/brand-22.png','src':''},
                  {'name':'后','img':imgrooturl +'/brand-23.png','src':''}
                ],//返回回来的数据
        htmlStr :'',
        n : 0,
        arr : [],//就是把数据转成二维数组。一维是swiper-slide的个数。二维是每个swiper-slide的img的个数和数据，最后转成arr[['图片1','图片2','图片3','图片4','图片5','图片6','图片7','图片8'],[,'图片9','图片10','图片11','图片12','图片13','图片14','图片15','图片16'],['图片17','图片18']]
        mm : [[1,2,3],[2,3,4]]
    },
    created:function(){
         this.logData();
         this.coupon();
         var arr = new Array([]);
            	for(var i =0; i<=Math.floor(this.allData.length/8); i++){
	            	arr[i]=[];
	    
				     arr[i]=this.allData.slice(i*8,i*8+8);
            	}
            	
            this.arr = arr;
            console.log( this.arr.length );
    },
    mounted : function(){
    var mySwiper = new Swiper('.swiper-container', {
			            loop: true,
			            // 如果需要分页器
			            //pagination: '.swiper-pagination',

			            // 如果需要前进后退按钮
			            nextButton: '.swiper-button-next',
			            prevButton: '.swiper-button-prev',

		        });
        mySwiper.update()
            	
    },    
    methods:{
        //页面初始化信息
         logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo",
                data:{
                    'modelId':modelId                
                    },
                 success: function (data) {
                    //  console.log(JSON.parse(data));
                     var a = eval('(' + data + ')')
                    //  console.log(a);
                     self.productList = a.data.activityList;
                    console.log(self.productList)
        
                }, 
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        //点击商品
        goodClick(p) {
            if (p.source == "SELFSUPPORT") {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"' + p.goodsId + '"}';
            } else {
                window.location.href = '/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"goodsId":"' + p.goodsId + '"}';
            }
        },
        //优惠券初始化信息
        coupon(){
            let self=this;
            $.ajax({
                type:'post',
                url:"/fanbei-web/activityCouponInfo",
                data:{'groupId':groupId},
                success:function(data){
                    self.content = eval('(' + data + ')').data;
                    self.m=self.content.couponInfoList;
                    self.c=JSON.stringify(self.m);
                    self.m=JSON.parse(self.c);
                    
                    // console.log(self.content);
                    console.log(self.m);
                }
            })
        },
         //点击领券
        couponClick:function(item) {
            let self = this;
            let couponId = item.couponId;
            // alert(couponId)
            $.ajax({
                url: "/fanbei-web/pickCoupon",
                type: "POST",
                dataType: "JSON",
                data: {
                    couponId: couponId
                },
                success: function (returnData) {
                    // alert(returnData);
                    if (returnData.success) {
                        requestMsg("优惠劵领取成功");
                    } else {
                        var status = returnData.data["status"];
                        if (status == "USER_NOT_EXIST") { // 用户不存在
                            window.location.href = returnData.url;
                        }
                        if (status == "OVER") { // 优惠券个数超过最大领券个数
                            //requestMsg(returnData.msg);
                            requestMsg("您已经领取，快去使用吧");
                        }
                        if (status == "COUPON_NOT_EXIST") { // 优惠券不存在
                            requestMsg(returnData.msg);
                        }
                        if (status == "MORE_THAN") { // 优惠券已领取完
                            requestMsg(returnData.msg);
                        }
                    }
                },
                error: function () {
                    requestMsg("哎呀，出错了！");
                }
            })
        },
        //点击显示顶部活动日历
        noShow(){
                this.isShow=!this.isShow;
                if(this.isShow){
                    $('.processBar').show();
                }else{
                    $('.processBar').hide();
                } 
        },
        //点击tab栏切换
        tabClick(i){
            this.tab=i+1;

        
        },
        //点击tab栏箭头
        tabClickTwo(){
            if(this.tab<this.productList.length){
                ++this.tab;
            }
        }
    }
})