/**
 * Created by nizhiwei-labtop on 2017/5/31.
 */
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}
//数据初始化
$.ajax({
    url:'/order/payResultOfAlipay',
    type:'post',
    success:function (data) {
        $('#chance').html('您还有'+data.chanceCount+'次机会');

        //中奖信息循环
        let con='';
        for(let i=0;i<data.awardList.length;i++){
            con+=`<li>
            <div class="personImg" style="background-image:url('${data.awardList.avatar}')"></div>
            <h2><span>${data.awardList.userName}</span><span>${data.awardList.msg}</span></h2>
         </li>`
        }
        $('.awardList').html(con);

        //开奖时间

        window.setInterval(function(){
            let leftTime=data.gmt_open-data.gmt_current;
            let leftsecond = parseInt(leftTime/1000);
            let day1=Math.floor(leftsecond/(60*60*24));
            let hour=Math.floor((leftsecond-day1*24*60*60)/3600);
            let minute=Math.floor((leftsecond-day1*24*60*60-hour*3600)/60);
            let second=Math.floor(leftsecond-day1*24*60*60-hour*3600-minute*60);
            let con = day1+"天"+hour+"小时"+minute+"分"+second+"秒";
        }, 1000);


    }
    
});



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
        this.run();
        clearTimeout(this.Countdown);
        this.Countdown=setTimeout(function(){                             //结束倒计时
            self.alertMsg('end');
        },20000);
    }
    run(){
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
        let dollNum=[5,2,6,1,8,5,2,6,1,8];
        let con='';
        for(let i=0;i<dollNum.length;i++){
            con+=`<span data-prop="${dollNum[i]}" class="doll"><div class="doll-main" style="background-image: url('https://fs.51fanbei.com/h5/app/activity/06/ni_boll${dollNum[i]}.png')"></div></span>`
        }
        $('#scroll').html(con)
    }
    alertMsg(state){
        let data={result:'N'};
        if(state=='end'){

        }else if(state=='claw'){
            data={result:'Y'};
        }
        // $.ajax({
        //     url:'/game/submitGameResult',
        //     type:'post',
        //     data:{result:'N',},
        //     success:function (data) {
        //
        //     }
        //
        // });
        console.log(data)
        $('#shadow').show();
        $('#startBtn').show();
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
                    console.log('ok');
                    if(Math.floor(Math.random()*10+1)>3){                 //随机能否抓到娃娃
                        $('#claw').css('backgroundImage','url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw2.png)'); //钩子变为收缩样式
                        doll.find('.doll-main').css({position:'absolute',left:'2.47rem'})       //娃娃脱离文档流并跟着上升
                            .animate({top:'-2.2rem'},800,function () {
                                $('.doll[data-prop='+dataProp+']').css('visibility','hidden');
                                self.alertMsg('claw');
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
sixGame.run();
$('#startBtn').click(function () {
    if(userName){
        sixGame.start();
        $(this).hide()
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
     $('#allToy #getPrize').slideDown(); 
     //$('#allToy #words').slideDown();
     //$('#allToy h3').css('paddingTop',0); //活动未开始时 
     $('#allToy .gotoTop').css('transform','rotate(180deg)');
}
function goDown(){
     $('#shadow').css('display','none');
     $('#allToy #getPrize').slideUp();
     //$('#allToy #words').slideUp();
     //$('#allToy h3').css('paddingTop','.3rem');//活动未开始时
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
