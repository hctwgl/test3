let type=getUrl('type'); //获取类型 GAME 游戏/AMUSEMENT 娱乐
//获取数据
let vm = new Vue({
    el: '#gamePayIndex',
    data: {
        content: {},
        ruleShow:false
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/game/pay/goods",
                data:{'type':type},
                success: function (data) {
                    //console.log(data);
                    self.content=data.data;
                    console.log(self.content);
                    self.$nextTick(function(){
                        let windowWidth=$(window).width();
                        //let hotContWidth=windowWidth*0.95;
                        let lastLiMarRight=windowWidth*0.056; //最后一个li的margin-right
                        let liLen=$('.hotGameList li').length; //hotCont宽度
                        //let liWidth=parseInt(0.266*hotContWidth); //li宽度
                        //let liMarRight=parseInt(0.072*hotContWidth);//每一个li的margin-right
                        let liWidth=parseInt(4*windowWidth/15); //li宽度
                        let liMarRight=parseInt(0.072*windowWidth);//每一个li的margin-right
                        let ulWidth=(liWidth*liLen)+(liLen-1)*liMarRight+lastLiMarRight;
                        $('.hotGameList li').width(liWidth);
                        $('.hotGameList li').css('margin-right',liMarRight);
                        $('.hotGameList li:last-child').css('margin-right',lastLiMarRight);
                        $('.hotCont').css('margin-left',lastLiMarRight);
                        $('.hotGameList').width(ulWidth);
                        console.log(liWidth,liMarRight,lastLiMarRight,ulWidth)
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击热门游戏
        hotGameClick(item){
            //console.log(item.id);
            let goodsId=item.id;
            window.location.href='gamePay?goodsId='+goodsId+'&discout='+item.discout+'&rebate='+item.rebate;
        },
        //小数点后一位
        fixNum(num){
            let numFix=num.toFixed(1);
            return numFix;
        }
    }
});


