/**
 * Created by nizhiwei-labtop on 2017/5/31.
 */
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}
let chance=[],isLogin,isShow,clientRate,chanceCount,recommendCode;

// let protocol = window.location.protocol;
// let host = window.location.host;
// let urlHost = protocol+'//'+host;
// alert(urlHost);
// let shareAppUrl = urlHost+'/fanbei-web/activity/gameShare?recommendCode='+recommendCode;
// console.log(shareAppUrl);


// app调用web的方法
function alaShareData(){
    // 分享内容
    let dataObj = {
        'appLogin': 'Y', // 是否需要登录，Y需要，N不需要
        'type': 'share', // 此页面的类型
        'shareAppTitle': '引爆年中抓娃娃，100%中大奖',  // 分享的title
        'shareAppContent': '抓娃娃次数无上限，赢最高888元现金红包！集齐5娃，平分1亿大奖，有且只在51返呗！',  // 分享的内容
        'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
        'shareAppUrl': 'https://app.51fanbei.com/fanbei-web/activity/gameShare?recommendCode='+recommendCode,  // 分享后的链接
        'isSubmit': 'Y', // 是否需要向后台提交数据，Y需要，N不需要
        'sharePage': 'gameShare' // 分享的页面
    };
    let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
    return dataStr;
}

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
                chanceCount=data.data.chanceCount;
                clientRate=data.data.clientRate||100;
                recommendCode=data.data.recommendCode;
                //抽奖次数显示,抽奖码获取
                if(isLogin=='N'){
                    $('#chance').html('您还有3次机会');
                }else{
                    $('#chance').html('您还有'+data.data.chanceCount+'次机会');
                }
                if(data.data.chanceCodes){
                    chance=data.data.chanceCodes.split(',');
                    for(var i = 0;i<chance.length;i++){
                        if(chance[i]==''||chance[i]==null||typeof(chance[i])==undefined){
                            chance.splice(i,1);
                            i=i-1;
                        }
                    }
                }else{chance=[]}

                //底部娃娃数量显示
                if(data.data.item1Count>0){
                    $('#toys').find('img').eq(0).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll1.png');
                    $('#toys').find('span').eq(0).html(data.data.item1Count)
                }
                if(data.data.item2Count>0){
                    $('#toys').find('img').eq(1).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll2.png');
                    $('#toys').find('span').eq(1).html(data.data.item2Count)
                }
                if(data.data.item3Count>0){
                    $('#toys').find('img').eq(2).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll3.png');
                    $('#toys').find('span').eq(2).html(data.data.item3Count)
                }
                if(data.data.item4Count>0){
                    $('#toys').find('img').eq(3).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll4.png');
                    $('#toys').find('span').eq(3).html(data.data.item4Count)
                }
                if(data.data.item5Count>0){
                    $('#toys').find('img').eq(4).attr('src','https://fs.51fanbei.com/h5/app/activity/06/ni_boll5.png');
                    $('#toys').find('span').eq(4).html(data.data.item5Count)
                }

                //娃娃中奖信息循环
                {
                    let con='';
                    for(let i=0;i<data.data.awardList.length;i++){
                        con+=`<li>
                <div class="personImg" style="background-image:url('${data.data.awardList[i].avatar}')"></div>
                <h2><span>${data.data.awardList[i].userName}</span><span>${data.data.awardList[i].msg}</span></h2></li>`
                    }$('.awardList').html(con);
                }

                //实物中奖信息循环
                {
                    let con='';
                    for(let i=0;i<data.data.entityAwardList.length;i++){
                        con+=`<li>
                <div class="personImg" style="background-image:url('${data.data.entityAwardList[i].avatar}')"></div>
                <h2><span>${data.data.entityAwardList[i].userName}</span><span>${data.data.entityAwardList[i].msg}</span></h2></li>`}
                    $('.entityAwardList').html(con);
                }

                //如果有人中实物大奖，就显示中奖滚轮
                if(data.data.entityAwardList.length>0){
                    $('#getPrize h6').hide();
                    $('#getPrize h4').html('618活动抽奖获奖名单');
                    $('#dollRoll').show();
                }
                //是否集齐五娃
                if(data.data.isFinish=='Y'){
                    $('#allToy').find('h3').html('五娃已集齐，请静等开奖！');
                        //五娃是否中奖
                        if (data.data.isAward == 'N') {
                            //开奖时间
                            if(data.data.gmtOpen>0) {
                                let time = 0,hours01, hours02, minutes01, minutes02, seconds01, seconds02;
                                let starTime=setInterval(function () {
                                    time += 1000;
                                    let leftTime = data.data.gmtOpen - data.data.gmtCurrent - time;
                                    let leftsecond = parseInt(leftTime / 1000);
                                    let hours = parseInt(leftsecond / 3600);
                                    let minutes = parseInt((leftsecond % 3600) / 60);
                                    let seconds = (leftsecond % 3600) % 60;

                                    if (hours < 10) {
                                        hours01 = '0';
                                        hours02 = hours;
                                    } else {
                                        hours = hours.toString();
                                        hours01 = hours.slice(0, 1);
                                        hours02 = hours.slice(1, 2);

                                    }
                                    if (minutes < 10) {
                                        minutes01 = '0';
                                        minutes02 = minutes;
                                    } else {
                                        minutes = minutes.toString();
                                        minutes01 = minutes.slice(0, 1);
                                        minutes02 = minutes.slice(1, 2);

                                    }
                                    if (seconds < 10) {
                                        seconds01 = '0';
                                        seconds02 = seconds;
                                    } else {
                                        seconds = seconds.toString();
                                        seconds01 = seconds.slice(0, 1);
                                        seconds02 = seconds.slice(1, 2);

                                    }
                                    //console.log(hours01+hours02+minutes01+minutes02+seconds01+seconds02)
                                    $('.countDown').find('span').eq(0).html(hours01);
                                    $('.countDown').find('span').eq(1).html(hours02);
                                    $('.countDown').find('span').eq(3).html(minutes01);
                                    $('.countDown').find('span').eq(4).html(minutes02);
                                    $('.countDown').find('span').eq(6).html(seconds01);
                                    $('.countDown').find('span').eq(7).html(seconds02);
                                }, 1000);
                                //如果倒计时到0
                            if(hours01<=0&&hours02<=0&&minutes01<=0&&minutes02<=0&&seconds01<=0&&seconds02<=0){
                                clearInterval(starTime);
                                $('#getPrize h5').html('正在抽取大奖中...');
                            }else{
                                if(data.data.gmtOpen>1497751200000){
                                    $('#getPrize h4').html('很遗憾，您暂未中奖，请静等下一波！');
                                }
                                $('#getPrize h5').html('开奖倒计时');
                                $('.countDown').show()
                            }

                        }
                        } else {
                            if (data.data.awardInfo.type == 'G') {
                                $('#getPrize h4').html('恭喜您获得幸运大奖！');
                                $('#getPrize h5').html('奖项已发送至:我的-抵用券');
                            } else if (data.data.awardInfo.type == 'E') {
                                $('#getPrize h4').html('恭喜您获得实物大奖！');
                                if (data.data.awardInfo.isSubmitContacts == 'Y') {
                                    $('#getPrize h5').html('奖项已发送至您的收货地址');
                                } else {
                                    $('#getPrize h5').html('<a href="personInfo?url='+data.data.awardInfo.awardIcon+'">请提交资料</a>');
                                }
                            }
                        }

                }
            }else{
                requestMsg('初始化失败');
            }
        }
    });
}
dataInit();

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
        this.startMove=setInterval(function (){
            if(self.num>=0){
                self.num=-8.25;
                $('#scroll').animate({marginLeft:self.num+'rem'},0,'linear');
            }
            self.num=(Math.round(self.num*1000)+1650)/1000;    //避免浮点数错误
            $('#scroll').animate({marginLeft:self.num+'rem'},1000,'linear');
        },1000);
        clearTimeout(this.Countdown);
        this.Countdown=setTimeout(function(){                             //结束倒计时
            if(isShow!='No'){
                self.alertMsg('end');
            }
        },20000);
        setTimeout(function(){                             //结束倒计时
            $('.modelPic').hide();
        },1000);

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
        let data={result:'N',code:chance[0]};
        if(state=='end'){
            $('.getState').html('抓取失败');
        }
       if(state=='claw'){
            data={result:'Y',item:item,code:chance[0]};
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

                        $('.getCashPrize').html('获得'+data.data.amount+'元').show();
                        $('.getCashCoupon').show();
                    }else{
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
                $('#shadow').show();
                $('.tryAgain').click(function () {
                    $('#shadow').hide();
                    $('.alert').hide();
                });
                dataInit();
            }
        });
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
                    if(Math.floor(Math.random()*100+1)<clientRate){                 //随机能否抓到娃娃
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
    $('.play').animate({top:'7.1rem'},200,function () {
        $('.play').animate({top:'7.08rem'},150)
    });
    if(isLogin=='Y'){         //是否登录
        if(chanceCount<1){              //否是 有机会
            $('.ad').hide();
            $('.getState').html('机会用完啦').show();
            $('.tryAgain').html('分享增加1次机会').click(function () {
                window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"引爆年中抓娃娃，100％中大奖","shareAppContent":"抓娃娃次数无上限，赢最高888元现金红包！集齐5娃，平分1亿大奖，有且只在51返呗！","shareAppImage":"https://fs.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"https://app.51fanbei.com/fanbei-web/activity/gameShare?recommendCode='+recommendCode+'","isSubmit":"Y","sharePage":"gameShare"}';
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
// function loginSuccess(obj) {
//     dataInit();
//     $('#startBtn').html(isLogin);
//     window.location.href='DragonBoat'
// }
//


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
