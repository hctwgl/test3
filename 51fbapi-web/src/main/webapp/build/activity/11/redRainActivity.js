/**
 * Created by nizhiwei-labtop on 2017/10/12.
 */
let couponArr=[];
$(document).ready(function() {
    let win = (parseInt($(".content").css("width"))) - 60;
    //增加红包
    let add =()=>{
        let imgName = parseInt(Math.random() * 2 + 1),
            Wh = parseInt(Math.random() * 70 + 35),
            Left = parseInt(Math.random() * win),
            rot = (parseInt(Math.random() * 90 - 45)) + "deg";
        num++;
        $(".content").append("<li class='li" + num + "' ></li>");
        $(".li" + num).css({
            "left": Left,
        });
        $(".li" + num).css({
            "width": Wh,
            "height": Wh,
            "transform": "rotate(" + rot + ")",
            "background":"url(https://f.51fanbei.com/h5/app/activity/11/redRain" + imgName + ".png) no-repeat center center",
            "background-size":"100%",
            "-ms-transform": "rotate(" + rot + ")", /* Internet Explorer */
            "-moz-transform": "rotate(" + rot + ")", /* Firefox */
            "-webkit-transform": "rotate(" + rot + ")",/* Safari 和 Chrome */
            "-o-transform": "rotate(" + rot + ")" /* Opera */
        });
        $(".li" + num).animate({'top':$(window).height()+20},5000,function(){
            //删掉已经显示的红包
            this.remove()
        });
        //点击红包的时候弹出模态层
        $(".li" + num).one('click',function(){
            let self=this;
            if(parseInt(Math.random() * 10)>5&&couponArr.length<3){   //概率50%并且总获奖数小于3
                $.ajax({
                    url:'/fanbei-web/redRain/applyHit',
                    type:'post',
                    success:function (data) {
                        data=JSON.parse(data);
                        console.log(data);
                        if(data.success){
                            couponArr.push(data.data);
                            self.style.backgroundImage='url(https://f.51fanbei.com/h5/app/activity/11/redRain4.png)';
                            redNum+=1;
                            //中奖弹框显示
                            $('.noWard').hide();
                            $('.ward').show();
                            $('.redNum span:nth-child(2)').text('中红包数量：'+redNum);
                        }else{
                            self.style.backgroundImage='url(https://f.51fanbei.com/h5/app/activity/11/redRain3.png)';
                        }
                    }
                });
            }else{
                self.style.backgroundImage='url(https://f.51fanbei.com/h5/app/activity/11/redRain3.png)';
            }
        });
        setTimeout(add,400)
    };

    //倒数计时
    let backward=()=>{
        numz--;
        if(numz>0){
            $(".backward span").html(numz);
            setTimeout(backward,1000)
        }else{
            $(".backward").remove();
            gameEnd()
        }
    };

    let gameEnd=()=>{
        gameNum--;
        if(gameNum>0){
            $('.redNum span:nth-child(1)').text(gameNum);
            setTimeout(gameEnd,1000)
        }else{
            let str='';
            for(let i=0;i<couponArr.length;i++){
                str+=`<div class="wardCoupon">
                          <span class="wardMoney"><span>￥</span>${couponArr[i].amount}</span>
                          <span class="wardTxt">${couponArr[i].couponName}</span>
                      </div>`;
            }
            $('.wardCoupon').html(str);
            $(".gameWard").show();
        }
    };
//开始倒计时
    let numz = 4;
    let gameNum=21;
    backward();
//开始掉红包
    let num = 0;
    let redNum=0;
    setTimeout(add,3000);
});