/**
 * Created by nizhiwei-labtop on 2017/5/31.
 */
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}
let chance=[],isLogin,isShow;
//数据初始化
function dataInit() {
    $.ajax({
        url:'/fanbei-web/initGame.htm',
        type:'post',
        success:function (data) {
            data=eval('(' + data + ')');
            console.log(data);
            if(data.success){
                isLogin=data.data.isLogin;
                //抽奖次数显示,抽奖码获取
                $('#chance').html('您还有'+data.data.chanceCount+'次机会');
                chance=data.data.chanceCodes.split(',');

                //底部娃娃数量显示
                if(data.data.item1Count>0){
                    $('#toys').find('img').eq(0).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll1.png').css('width','61%');
                    $('#toys').find('span').eq(0).html(data.data.item1Count)
                }
                if(data.data.item2Count>0){
                    $('#toys').find('img').eq(1).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll2.png').css('width','61%');
                    $('#toys').find('span').eq(1).html(data.data.item2Count)
                }
                if(data.data.item3Count>0){
                    $('#toys').find('img').eq(2).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll3.png').css('width','61%');
                    $('#toys').find('span').eq(2).html(data.data.item3Count)
                }
                if(data.data.item4Count>0){
                    $('#toys').find('img').eq(3).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll4.png').css('width','61%');
                    $('#toys').find('span').eq(3).html(data.data.item4Count)
                }
                if(data.data.item5Count>0){
                    $('#toys').find('img').eq(4).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll5.png').css('width','61%');
                    $('#toys').find('span').eq(4).html(data.data.item5Count)
                }

                //中奖信息循环
                {
                    let con='';
                    for(let i=0;i<data.data.awardList.length;i++){
                        con+=`<li>
                <div class="personImg" style="background-image:url('${data.data.awardList[i].avatar}')"></div>
                <h2><span>${data.data.awardList[i].userName}</span><span>${data.data.awardList[i].msg}</span></h2></li>`
                    }$('.awardList').html(con);
                }
                {
                    let con='';
                    for(let i=0;i<data.data.entityAwardList.length;i++){
                        con+=`<li>
                <div class="personImg" style="background-image:url('${data.data.entityAwardList[i].avatar}')"></div>
                <h2><span>${data.data.entityAwardList[i].userName}</span><span>${data.data.entityAwardList[i].msg}</span></h2></li>`}
                    $('.entityAwardList').html(con);
                }

                //是否集齐五娃
                if(data.data.isFinish=='Y'){
                    $('#allToy').find('h3').html('五娃已集齐，请静等开奖！');
                    $('#getPrize h4').html('618活动抽奖获奖名单');

                    //开奖时间
                    if(data.data.gmt_open){
                        $('#dollRoll').show();
                        //五娃是否中奖
                        if (data.data.isAward == 'N') {
                            if(data.data.gmtOpen){
                                let time=0;
                                window.setInterval(function(){
                                    time+=1000;
                                    let hours01,hours02,minutes01,minutes02,seconds01,seconds02;
                                    let leftTime=data.data.gmtOpen-data.data.gmtCurrent-time;
                                    let leftsecond = parseInt(leftTime/1000);
                                    let hours=parseInt(leftsecond/3600);
                                    let minutes=parseInt((leftsecond%3600)/60);
                                    let seconds=(leftsecond%3600)%60;

                                    if(hours<10){
                                        hours01='0';
                                        hours02=hours;
                                    }else{
                                        hours=hours.toString();
                                        hours01=hours.slice(0,1);
                                        hours02=hours.slice(1,2);

                                    }
                                    if(minutes<10){
                                        minutes01='0';
                                        minutes02=minutes;
                                    }else{
                                        minutes=minutes.toString();
                                        minutes01=minutes.slice(0,1);
                                        minutes02=minutes.slice(1,2);

                                    }
                                    if(seconds<10){
                                        seconds01='0';
                                        seconds02=seconds;
                                    }else{
                                        seconds=seconds.toString();
                                        seconds01=seconds.slice(0,1);
                                        seconds02=seconds.slice(1,2);

                                    }
                                    //console.log(hours01+hours02+minutes01+minutes02+seconds01+seconds02)
                                    $('.countDown').find('span').eq(0).html(hours01);
                                    $('.countDown').find('span').eq(1).html(hours02);
                                    $('.countDown').find('span').eq(3).html(minutes01);
                                    $('.countDown').find('span').eq(4).html(minutes02);
                                    $('.countDown').find('span').eq(6).html(seconds01);
                                    $('.countDown').find('span').eq(7).html(seconds02);
                                }, 1000);
                                $('#getPrize h6').hide();
                                $('#getPrize h5').html('开奖倒计时');
                                $('.countDown').show()
                            }
                        } else {
                            if(data.data.awardInfo.type=='L'){
                                $('#getPrize h4').html('恭喜您获得幸运大奖！');
                                $('#getPrize h5').html('奖项已发送至:我的-抵用券');
                            }else if(data.data.awardInfo.type=='Y'){
                                $('#getPrize h4').html('恭喜您获得实物大奖！');
                                if (data.data.isSubmitContacts=='Y') {
                                    $('#getPrize h5').html('奖项已发送至您的收货地址');
                                }else{
                                    $('#getPrize h5').html('请提交资料');

                                }
                            }
                        }

                    }

                }

            }
        }

    });
}
dataInit()



//游戏
class game{
    constructor(width,time){
        this.init={num:-(width),time:time};
        this.num=-(width);
        this.time=time;
        $('#playBtn').click(this.claw.bind(this));

    }
    start(){
        let self=this;
        this.reset();
        this.doll();
        clearInterval(this.startMove);
        this.startMove=setInterval(function () {           //舞台开始滚动
            if(this.num>=0){
                this.num=-8.25;
                $('#scroll').animate({marginLeft:this.num+'rem'},0,'linear');
            }
            this.num=(Math.round(this.num*1000)+1650)/1000;    //避免浮点数错误
            $('#scroll').animate({marginLeft:this.num+'rem'},1000,'linear');
        }.bind(this),1000);
        clearTimeout(this.Countdown);
        this.Countdown=setTimeout(function(){                             //结束倒计时
            if(isShow!='No'){
                self.alertMsg('end');
            }
        },20000);
        $('.modelPic').hide();
    }
    reset(){
        $('#scroll').animate({marginLeft:this.init.num+'rem'},0,'linear');
        $('#claw').css('backgroundImage','url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw1.png)');  //还原钩子
        this.num=this.init.num;
        this.time=this.init.time;
        clearInterval(this.timeStart);
        this.timeStart=setInterval(function () {
            (this.time<=0)?this.time=0:this.time-=1;
            $('#time').html(this.time+'秒');
        }.bind(this),1000);
    }
    doll(){
        let dollNum=[1,2,3,4,5,1,2,3,4,5];
        let con='';
        for(let i=0;i<dollNum.length;i++){
            con+=`<span data-prop="${dollNum[i]}" class="doll"><div class="doll-main" style="background-image: url('https://fs.51fanbei.com/h5/app/activity/06/ni_boll${dollNum[i]}.png')"></div></span>`
        }
        $('#scroll').html(con)
    }
    alertMsg(state,item){
        isShow='No';
        let data={result:'N',code:chance[1],_appInfo:'{"userName":"13955556666"}'};
        if(state=='end'){
            $('.getState').html('抓取失败');
        }
       if(state=='claw'){
            data={result:'Y',item:item,code:chance[1],_appInfo:'{"userName":"13955556666"}'};
           $('.getState').html('抓取成功');
       }
        $.ajax({
            url:'/fanbei-web/submitGameResult',
            type:'post',
            data:data,
            success:function (data) {
                data=eval('(' + data + ')');
                $('.ad').hide();
                if(data.data.lotteryResult=='Y'){

                  if(data.data.awardType=='CASH'){

                        $('.getCashPrize').html('获得'+data.data.amount+'元现金').show();
                        $('.getCashCoupon').show();
                    }else if(data.data.awardType=='FULLVOUCHER'){
                        $('.limitMoney').html(data.data.limitAmount);
                        $('.limitDate').html(formatDate(data.data.gmtEnd));
                        $('.getMoney').html(data.data.amount);

                        $('.getCashCoupon').show();
                        $('.getCouponPrize').show();
                    }

                }else{
                    $('.jushuo').show();
                }

                $('.tryAgain').html('再抓一次');
                $('#alert').show();
                $('.tryAgain').click(function () {
                    $('#shadow').hide();
                    $('.alert').hide();
                });
                dataInit();
            }
        });
        $('#shadow').show();
        $('#startBtn').show();
        $('.modelPic').show();
    }
    claw(){
        let self=this;
        let clawLeft=$('#claw').offset().left;
        $('.button').attr('disabled','disabled');
        $('#claw').animate({top:'-.5rem'},1500,function () {          //钩子下落
            $('.doll').each(function () {
                let doll=$(this);
                let dollLeft=doll.offset().left;
                if(dollLeft>(clawLeft-10) && dollLeft<(clawLeft+35)){          //判断钩子与娃娃是否重合，减的越大越偏右
                    let dataProp=doll.attr('data-prop');
                    if(Math.floor(Math.random()*10+1)>0){                 //随机能否抓到娃娃
                        $('#claw').css('backgroundImage','url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw2.png)'); //钩子变为收缩样式
                        doll.find('.doll-main').css({position:'absolute',left:'2.47rem'})       //娃娃脱离文档流并跟着上升
                            .animate({top:'-2.2rem'},800,function () {
                                $('.doll[data-prop='+dataProp+']').css('visibility','hidden');
                                self.alertMsg('claw',dataProp);
                            })
                    }
                }
            });
            $('#claw').animate({top:'-2rem'},1000,function () {                 //钩子回升
                $('.button').removeAttr('disabled');
            });
        })
    }
}
let sixGame= new game(16.5,20);
sixGame.doll();
$('#startBtn').click(function () {
    isShow='yes';
    if(isLogin=='Y'){         //是否登录
        if(chance.length<=1||chance[1].length<5){              //否是 有机会
            $('.ad').hide();
            $('.getState').html('机会用完啦').show();
            $('.tryAgain').html('分享增加1次机会').click(function () {
                window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params={"title":"年中抓娃娃,让你一次玩个爽","content":"51返呗年中狂欢，全球好货折上折，iPhone 7+精美电器+上万礼券等你拿~","shareUrl":"www.baidu.com"}';
                // let retrunNum = getBlatFrom();  // retrunNum为1表示是Android
                //     // 分享内容
                //     let dataObj = {
                //         'shareAppTitle': '年中盛宴攻略',
                //         'shareAppContent': '分享年中盛宴攻略赢取大奖',
                //         'shareAppImage': 'https://fs.51fanbei.com/h5/app/activity/05/mumday28_01.jpg',
                //         'shareAppUrl': 'https://www.baidu.com'
                //     };
                //     let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
                //     if ( retrunNum == 1 ) {  // 调用原生方法
                //         alaAndroid.shareData(dataStr);
                //     } else {
                //         alaIos.shareData(dataStr);
                //     }
            });
            $('#alert').show();
            $('#shadow').show();
        }else{
            sixGame.start();
            $(this).hide();
            console.log(chance)
        }
    }else{
        window.location.href = '/fanbei-web/opennative?name=APP_LOGIN';
    }

});
//阴影点击
$('#shadow').click(function () {
    $(this).hide();
    $('.alert').hide()


});



//--------------------------------------yun--------------------------------------------
//活动规则
$('#rule').click(function(){
    $('#rules').css('display','block');
    $('#shadow').show();
})
$('.closeRules').click(function(){
    $('#rules').css('display','none');
    $('#shadow').hide();
})




//滚轮事件
function AutoScroll(obj,x) {
    $(obj).find("ul:first").animate({
        marginTop: x
    },
    500,
    function() {
        $(this).css({
            marginTop: "0"
        }).find("li:first").appendTo(this);
    });
}
$(document).ready(function() {
    setInterval('AutoScroll(".roll01","-1rem")', 1000);
    setInterval('AutoScroll(".roll02","-.55rem")', 1000);
});


// if(活动为开始时点击立即抓取 活动时间提示出现)
function goUp(){
     $('#shadow').css('display','block');
     $('#getPrize').slideDown();
     $('#allToy').css('z-index',300);
     $('#allToy .gotoTop').css('transform','rotate(180deg)');
}
function goDown(){
     $('#shadow').css('display','none');
     $('#getPrize').slideUp();
     $('#allToy').css('z-index',150);
     $('#allToy .gotoTop').css('transform','rotate(0deg)'); 
}
$(function(){
    //箭头点击事件
    var onoff=1; //加上开关
    $('#allToy .gotoTop').click(function(){
       if(onoff==1){
       goUp();
       onoff=0;
       } else {
       goDown();
       onoff=1;
       }
    })
    //蒙版点击事件
    $('#shadow').click(function(){
       goDown()
       onoff=1;
    })

});
