/**
 * Created by nizhiwei-labtop on 2017/5/31.
 */
class game{
    constructor(width,time){
        this.init={num:-(width),time:time};
        this.num=-(width);
        this.time=time;
        $('#playBtn').click(this.claw.bind(this));

    }
    start(){
        this.reset();
        this.run();
        clearTimeout(this.Countdown);
        this.Countdown=setTimeout(function(){                             //结束倒计时
            $('#startBtn').show();
            alert('游戏结束');
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
    alertMsg(){


    }
    claw(){
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
                                $('#startBtn').show();
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
    sixGame.start();
    $(this).hide()
});


//滚轮事件
function AutoScroll(obj) {
    $(obj).find("ul:first").animate({
        marginTop: "-1rem"
    },
    500,
    function() {
        $(this).css({
            marginTop: "0"
        }).find("li:first").appendTo(this);
    });
}
$(document).ready(function() {
    setInterval('AutoScroll("#roll")', 1000);
});

//阴影点击
$('#shadow').click(function () {
    $(this).hide();
    $('.alert').hide()
});
