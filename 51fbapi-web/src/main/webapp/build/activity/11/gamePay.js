let goodsId=getUrl('goodsId'); //获取类型id
//获取数据
let vm = new Vue({
    el: '#gamePay',
    data: {
        content: {},
        fixCont:{}
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
                url: "/game/pay/goodsInfo",
                data:{'goodsId':goodsId},
                success: function (data) {
                    //console.log(data);
                    self.content=data.data.content;
                    console.log(self.content);
                    //需要先判断数据格式
                    /*self.fixCont.gameName=$(self.content).find('game').attr('name');//游戏名称
                    if($(self.content).find('gameItemInfo').children('acctTypeItem').text()){ //账号类型若存在则显示游戏通行证 此项
                        self.fixCont.gamePass='Y';
                    }
                    if($(self.content).find('gameItemInfo').children('userNameItem').text()){ //充值账号若存在则显示 否则不显示此项
                        self.fixCont.gameNum='Y';
                    }
                    if($(self.content).find('gameItemInfo').children('gameAreaItem').text()){ //游戏区服若存在则显示 否则不显示此项
                        self.fixCont.gameArea='Y';
                        self.fixCont.gameAreaList=[];
                        $(self.content).find('area').each(function(){
                            self.fixCont.gameAreaList.push($(this).attr('name'));
                        });
                    }
                    if($(self.content).find('gameItemInfo').children('gameTypeItem').text()){ //充值类型若存在则显示 否则不显示此项
                        self.fixCont.gameType='Y';
                    }
                    self.fixCont.quantityList=[]; //面额--点券--价格（点数*倍数）
                    self.fixCont.quantityList=$(self.content).find('type').children('quantity').text().split(",");
                    //面额--点券--价格倍数
                    self.fixCont.priceTimes=$(self.content).find('type').children('quantity').attr('priceTimes');
                    self.fixCont.chargeNumList=[]; //面额--点券--点数
                    self.fixCont.chargeNumList=$(self.content).find('type').children('chargeNum').text().split(",");
                    console.log(self.fixCont);*/
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //字符串转数字
        fixStringToNum(str){
            let num=parseInt(str);
            return num;
        }
    }
});


