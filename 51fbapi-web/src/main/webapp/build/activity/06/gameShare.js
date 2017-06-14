/**
 * Created by nizhiwei-labtop on 2017/5/31.
 */
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}
let chance=[],isLogin,isShow,recommendCode;

//数据初始化
function dataInit() {
    $.ajax({
        url:'/fanbei-web/initGame.htm',
        type:'post',
        success:function (data) {
            data=eval('(' + data + ')');
            console.log(data);
            if(data.success){
                //娃娃中奖信息循环
                let con='';
                for(let i=0;i<data.data.awardList.length;i++){
                    con+=`<li>
                    <div class="personImg" style="background-image:url('${data.data.awardList[i].avatar}')"></div>
                    <h2><span>${data.data.awardList[i].userName}</span><span>${data.data.awardList[i].msg}</span></h2></li>`
                }
                $('.awardList').html(con);
            }else{
                requestMsg('中奖名单获取失败');
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
        $('.ad').hide();
        if(state=='end'){
            $('.getState').html('抓取失败');
            $('.jushuo').show();
        }
        if(state=='claw'){
            $('.getState').html('抓取成功');
            $('.getCashCoupon').show();
            $('.getCouponPrize').show();
        }
        $('#alert').show();
        $('#shadow').show();
        $('.tryAgain').click(function () {
            $('#shadow').hide();
            $('.alert').hide();
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
                    // if(Math.floor(Math.random()*100+1)<clientRate){                 //随机能否抓到娃娃
                        $('#claw').css('backgroundImage','url(https://fs.51fanbei.com/h5/app/activity/06/ni_claw2.png)'); //钩子变为收缩样式
                        doll.find('.doll-main').css({position:'absolute',left:'2.47rem'})       //娃娃脱离文档流并跟着上升
                            .animate({top:'-2.2rem'},800,function () {
                                $('.doll[data-prop='+dataProp+']').css('visibility','hidden');
                                self.alertMsg('claw',dataProp);
                            })
                    // }
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
    let self=$(this);
    $('.play').animate({top:'7.1rem'},200,function () {
        $('.play').animate({top:'7.08rem'},150);
        sixGame.start();
        self.hide();
        console.log(chance)
    });
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
