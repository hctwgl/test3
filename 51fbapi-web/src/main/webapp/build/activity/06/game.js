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
        clearInterval(this.startMove);
        clearInterval(this.timeStart);
        clearTimeout(this.Countdown);
        this.startMove=setInterval(function () {           //舞台开始滚动
            if(this.num>=0){
                this.num=-8.25;
                $('#scroll').animate({marginLeft:this.num+'rem'},0,'linear');
            }
            this.num=(Math.round(this.num*1000)+1650)/1000;    //避免浮点数错误
            $('#scroll').animate({marginLeft:this.num+'rem'},1000,'linear');
        }.bind(this),1000);
        this.timeStart=setInterval(function () {
            this.time-=1;
            $('#time').html(this.time);
        })
        this.Countdown=setTimeout(function(){                             //结束倒计时
            $('#startBtn').show();
            alert('游戏结束');
            clearInterval(this.timeStart);
        },20000);
    }
    reset(){
        this.doll();
        $('#scroll').animate({marginLeft:this.init.num+'rem'},0,'linear');
        this.num=this.init.num;
        this.time=this.init.time;
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
        $('#claw').animate({top:'-1.4rem'},1500,function () {          //钩子下落
            $('.doll').each(function () {
                let doll=$(this);
                let dollLeft=doll.offset().left;
                if(dollLeft>(clawLeft-10) && dollLeft<(clawLeft+35)){          //判断钩子与娃娃是否重合，减的越大越偏右
                    let dataProp=doll.attr('data-prop');
                    console.log('ok');
                    if(Math.floor(Math.random()*10+1)>3){                 //随机能否抓到娃娃
                        doll.find('.doll-main').css({position:'absolute',left:'2.47rem'})       //娃娃脱离文档流并跟着上升
                            .animate({top:'-1.5rem'},1000,function () {
                                $('.doll[data-prop='+dataProp+']').css('visibility','hidden');
                                alert('你抓取了'+dataProp+'号娃娃');
                                $('#startBtn').show();
                            })
                    }
                }
            });
            $('#claw').animate({top:'-.2rem'},1000,function () {                 //钩子回升
                $('.button').removeAttr('disabled');
            });
        })
    }
}
let sixGame= new game(16.5,20);

$('#startBtn').click(function () {
    sixGame.start();
    $(this).hide()
});
