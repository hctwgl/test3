let goodsId=getUrl('goodsId'); //获取类型id
let discout=getUrl('discout'); //折扣
let rebate=getUrl('rebate'); //返利
$(function(){
    $('#recreationPay').height($(window).height());
    $('.footer').height($(window).height()*0.07);
    $('.allTop').height($(window).height()*0.93);
});

//获取数据
let vm = new Vue({
    el: '#recreationPay',
    data: {
        content: {},
        fixCont:{},
        dataType:'',
        allData:[],
        initChooseFirst:'',
        allDataLen:'',
        discout:discout,
        rebate:rebate,
        liIndex:'',
        maskShow:'',
        initPriceList:[]  //默认显示的面额list
    },
    created: function () {
        this.logData();
    },
    methods: {
        //数据格式为A
        rewritedata(xml) {
            let _xml = xml;
            let returnobj = Object.create(null);
            let getattr = (parentMark, mark, attrName)=>{
                if(attrName) {
                    return $(parentMark).find(mark).attr(attrName)
                } else {
                    return $(parentMark).find(mark)
                }
            };
            let getselfattr = (mark, attrName)=>{
                return $(mark).attr(attrName)
            };
            //判断game 里attr('name')是否存在
            if(getattr(_xml, 'game', 'name') || getselfattr(_xml, 'name')){
                returnobj.gameName = getattr(_xml,'game', 'name')|| getselfattr(_xml, 'name');
            }
            //充值账号
            let accountType=$(_xml).find('acctType').text();
            if(accountType){
                returnobj.accountType =accountType;
            }else{
                $('.gameNum').hide();
            }
            //判断充值类型
            let typeNameList=[];
            if(getattr(_xml,'type', 'name')){
                $(_xml).find('type').each(function(){
                    typeNameList.push($(this).attr('name'));
                });
                returnobj.typeNameList=typeNameList;
            }else if(getattr(_xml,'types', 'name')){
                $(_xml).find('types').each(function(){
                    typeNameList.push($(this).attr('name'));
                });
                returnobj.typeNameList=typeNameList;
            }else{
                $('.gameType').hide();
            }
            //面额
            let priceTypeList=[];
            let quantityList=[]; //面额--点券--价格（点数*倍数）
            let chargeNumList=[];
            for(let i=0;i<$(_xml).find('type').length;i++){
                quantityList[i]=$(_xml).find('type').eq(i).children('quantity').text().split(',');
                //点券--点数
                chargeNumList[i]=$(_xml).find('type').eq(i).children('chargeNum').text().split(',');
                let list=[];
                for(let j=0;j<quantityList[i].length;j++){
                    let obj={quantity:quantityList[i][j],chargeNum:chargeNumList[i][j]};
                    list.push(obj);
                }
                let aa={chargeNumName:$(_xml).find('type').eq(i).children('chargeNum').attr('name')?$(_xml).find('type').eq(i).children('chargeNum').attr('name') : '',priceTimes:$(_xml).find('type').eq(i).children('quantity').attr('priceTimes'),list:list};
                priceTypeList.push(aa);
            }
            //console.log(priceTypeList)
            returnobj.priceTypeList=priceTypeList;
            return returnobj;
        },
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
                    self.dataType=data.data.xmlType;//数据格式
                    console.log(self.content);
                    //需要先判断数据格式
                    $(self.content).find('game').each((index, item)=>{
                        self.allData.push(self.rewritedata(item));
                    });
                    console.log(self.allData, '全部数据');
                    console.log(self.allData.length, '全部数据长度');
                    self.fixCont=self.allData[0];
                    console.log(self.allData[0], '默认显示第一个数据');
                    //默认显示的面额为第一个充值类型下的第一个list
                    self.initPriceList=self.allData[0].priceTypeList[0];
                    self.liIndex=0;
                    self.allDataLen=self.allData.length;
                    if(self.allDataLen==1){
                        self.fixCont=self.allData[0];
                    }
                    self.$nextTick(function(){
                        $('.gamePass input').attr('placeholder','请输入'+self.fixCont.accountType);
                        $('.typeList li').eq(0).addClass('changeColor01');
                        $('.typeList li').eq(0).find('p').addClass('changeColor02');
                        $('.moneyList li').eq(0).addClass('changeColor01');
                        $('.moneyList li').eq(0).find('p').addClass('changeColor02');
                        $('.payMoney span').html($('.moneyList li').eq(0).find('.typePrice').html());
                        $('.fanMoney span').html((($('.moneyList li').eq(0).find('.pricePay').html())*rebate).toFixed(2)+'元');
                    });
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击服务名称
        gameNameClick(){
            let self=this;
            self.maskShow=true;
            $('.nameCont').animate({'bottom':0},400);
        },
        //选择服务名称
        chooseName(index,item){
            let self=this;
            self.fixCont=self.allData[index];
            console.log(self.fixCont);
            $('.gameName:first-child').find('span').html(item.gameName);
            self.maskShow=false;
            $('.nameCont').animate({'bottom':'-8.7rem'},0);
        },
        //点击充值类型
        gameTypeClick(index){
            let self=this;
            $('.typeList li').eq(index).addClass('changeColor01');
            $('.typeList li').eq(index).find('p').addClass('changeColor02');
            $('.typeList li').eq(index).siblings().removeClass('changeColor01');
            $('.typeList li').eq(index).siblings().find('p').removeClass('changeColor02');
            self.initPriceList=self.fixCont.priceTypeList[index];
            console.log(self.initPriceList,'000')
            $('.payMoney span').html(((self.initPriceList.list[0].quantity) * discout).toFixed(2)+'元');
            $('.fanMoney span').html(((self.initPriceList.list[0].quantity) * discout*rebate).toFixed(2)+'元');
        },
        //点击面额
        gameMoneyClick(index){
            let self=this;
            $('.moneyList li').eq(index).addClass('changeColor01');
            $('.moneyList li').eq(index).find('p').addClass('changeColor02');
            $('.moneyList li').eq(index).siblings().removeClass('changeColor01');
            $('.moneyList li').eq(index).siblings().find('p').removeClass('changeColor02');
            $('.payMoney span').html($('.moneyList li').eq(index).find('.pricePay').html()+'元');
            $('.fanMoney span').html((($('.moneyList li').eq(index).find('.pricePay').html())*rebate).toFixed(2)+'元');
            self.liIndex=index;
        },
        //确认充值
        sureClick(){
            let self = this;
            let quantityNum,times;
            let gameName,acctType,userName,goodsNum,actualAmount,gameAcct,gameArea,gameType,gameSrv;
            if($('.gamePass input').val()){
                if(self.fixCont.priceTypeList){ // goodsNum计算
                    quantityNum=self.initPriceList.list[self.liIndex].quantity;
                    times=self.initPriceList.priceTimes;
                }
                if($('.gameName').hasClass('needGameNum')){ //游戏账号
                    gameAcct=$('.needGameNum input').val();
                }else{
                    gameAcct='';
                }
                if($('.gameName').hasClass('gameArea')){ //游戏选区
                    gameArea=$('.gameArea span').html();
                }else{
                    gameArea='';
                }
                if($('.gameName').hasClass('gameService')){ //游戏服务器
                    gameSrv=$('.gameService span').html();
                }else{
                    gameSrv='';
                }
                if($('.payType').hasClass('gameType')){ //充值类型
                    gameType=$('.typeList .changeColor02').html();
                }else{
                    gameType='';
                }
                if(self.dataType=='A'){
                    gameName=$('.gameName:first-child').find('span').html();
                    acctType=$('.gamePass p').html();
                    userName=$('.gamePass input').val();
                    goodsNum=$('.changeColor01 .goodsNum').html();
                    actualAmount=$('.changeColor01 .pricePay').html();
                }
                if(self.dataType=='B'){
                    gameName=$('.gameName:first-child').find('span').html();
                    acctType=$('.gamePass p').html();
                    userName=$('.gamePass input').val();
                    goodsNum=$('.changeColor01 .goodsNum').html();
                    actualAmount=$('.changeColor01 .pricePay').html();
                }
                $.ajax({
                    type: 'post',
                    url: "/game/pay/order",
                    data:{'goodsId':goodsId,'gameName':gameName,'acctType':acctType,
                        'userName':userName,'goodsNum':goodsNum,'actualAmount':actualAmount,
                        'gameAcct':gameAcct,'gameArea':gameArea,'gameSrv':gameSrv,'gameType':gameType,
                        'priceTimes':times
                    },
                    success: function (data) {
                        console.log(data,'确认充值');
                        let orderNo=data.data.orderNo;
                        window.location.href='gameOrderDetail?orderNo='+orderNo;
                    },
                    error:function(){
                        requestMsg('哎呀，出错了！');
                    }
                });
            }else{
                requestMsg('信息填写不完整！');
            }

        },
        //字符串转数字
        fixStrToNum(str){
            let num=parseInt(str);
            return num;
        },
        //保留小数点后两位
        fixNum(n){
            let number=n.toFixed(2);
            return number;
        },
        maskClick(){
            let self=this;
            self.maskShow=false;
            $('.nameCont').animate({'bottom':'-8.7rem'},0);
        },
    }
});
