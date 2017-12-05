let goodsId=getUrl('goodsId'); //获取类型id
let discout=getUrl('discout'); //折扣
let rebate=getUrl('rebate'); //返利
$(function(){
    $('#gamePay').height($(window).height());
    $('.footer').height($(window).height()*0.07);
    $('.allTop').height($(window).height()*0.93);
});
//获取数据
let vm = new Vue({
    el: '#gamePay',
    data: {
        content: {},
        fixCont:{},
        dataType:'',
        allData:[],
        allDataLen:'',
        discout:discout,
        rebate:rebate,
        needGameNumShow:'', //是否显示游戏账号
        liIndex:'',  //所选游戏名称index
        areaIndex:0, //所选游戏选区index
        maskShow:'', //蒙版显示与隐藏
        serviceList:[], //选择选区--服务器
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
            //数据格式---B---是否显示游戏账号
            if(getattr(_xml, 'game', 'needGameAcct') || getselfattr(_xml, 'needGameAcct')){
                returnobj.needGameAcct = getattr(_xml,'game', 'needGameAcct')|| getselfattr(_xml, 'needGameAcct');
            }
            //充值账号
            let accountType=$(_xml).find('acctType').text();
            if(accountType){
                returnobj.accountType =accountType;
            }else{
                $('.gameNum').hide();
            }
            //游戏区服
            if($(_xml).find('area') && $(_xml).find('area').attr('name')){
                $('.gameArea').show();
                let areas = getattr(_xml, 'areas');
                let areaslen = areas.length; //areas 长度
                if(areaslen && areaslen>=2) {
                    returnobj.areasList = areas.map((index, item)=>{
                        let areaname = getattr(item, 'area');
                        let areaarr = [];
                        if(areaname.length) {
                            areaname.each((index, a)=>{
                                areaarr.push(getselfattr(a, 'name'));
                            })
                        }
                        return {
                            text: getselfattr(item, 'name'),
                            children: areaarr
                        }
                    });
                    returnobj.areasList=Array.prototype.slice.call(returnobj.areasList);
                    returnobj.areaslen=areaslen;
                }
                if(areaslen && areaslen==1) {
                    let areaLength=$(_xml).find('area').length;
                    let areasList=[];
                    if(areaLength){
                        $(_xml).find('area').each(function(){
                            areasList.push($(this).attr('name'));
                        });
                    }
                    returnobj.areasList=areasList;
                    returnobj.areaslen=areaslen;
                }
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
            quantityList=$(_xml).find('type').children('quantity').text().split(",");
            //点券--点数
            let chargeNumList=[];
            chargeNumList=$(_xml).find('type').children('chargeNum').text().split(",");
            for(let i=0;i<quantityList.length;i++){
                let obj={quantity:quantityList[i],chargeNum:chargeNumList[i]};
                priceTypeList.push(obj);
            }
            returnobj.priceTypeList=priceTypeList;
            //点券--价格倍数
            returnobj.priceTimes=$(_xml).find('type').children('quantity').attr('priceTimes');
            //点券--名称
            if($(_xml).find('type').children('chargeNum').attr('name')){
                returnobj.chargeNumName=$(_xml).find('type').children('chargeNum').attr('name');
            }else{
                returnobj.chargeNumName='';
            }
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
                    self.$nextTick(function(){
                        if($(self.content).find('deposititem').attr('nameDesc')){
                            $('.nameDesc').html($(self.content).find('deposititem').attr('nameDesc'));
                            $('.nameDesc01').attr('placeholder','请输入'+($(self.content).find('deposititem').attr('nameDesc')));
                        }
                        if($(self.content).find('deposititem').attr('nameconfirmDesc')){
                            $('.nameconfirmDesc').html($(self.content).find('deposititem').attr('nameconfirmDesc'));
                            $('.nameconfirmDesc01').attr('placeholder','请'+($(self.content).find('deposititem').attr('nameconfirmDesc')));
                        }
                    });
                    //需要先判断数据格式
                    $(self.content).find('game').each((index, item)=>{
                        self.allData.push(self.rewritedata(item));
                    });
                    console.log(self.allData, '全部数据');
                    console.log(self.allData.length, '全部数据长度');
                    self.fixCont=self.allData[0];
                    console.log(self.allData[0], '默认显示第一个数据');
                    self.liIndex=0;
                    //默认显示的游戏区服
                    if(self.fixCont.areasList){
                        for(let i=0;i<self.fixCont.areasList.length;i++){
                            if(self.fixCont.areasList[0].children){
                                $('.gameService').show();
                                self.fixCont.initArea=self.fixCont.areasList[0].text;
                                self.fixCont.initService=self.fixCont.areasList[0].children[0];
                            }else{
                                self.fixCont.initArea=self.fixCont.areasList[0];
                            }
                        }
                    }
                    self.allDataLen=self.allData.length;
                    if(self.allDataLen==1){
                        self.fixCont=self.allData[0];
                    }
                    self.$nextTick(function(){
                        $('.typeList li').eq(0).addClass('changeColor01');
                        $('.typeList li').eq(0).find('p').addClass('changeColor02');
                        $('.moneyList li').eq(0).addClass('changeColor01');
                        $('.moneyList li').eq(0).find('p').addClass('changeColor02');
                        $('.payMoney span').html($('.moneyList li').eq(0).find('.typePrice').html());
                        $('.fanMoney span').html((($('.moneyList li').eq(0).find('.pricePay').html())*rebate).toFixed(2)+'元');
                        $('.gameNum input').attr('placeholder','请输入'+self.fixCont.accountType);
                    });
                    //判断游戏账号是否显示-----针对B
                    if(self.fixCont.needGameAcct=='1') {
                        self.needGameNumShow = true;
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击显示所有游戏名称
        gameNameClick(){
            let self=this;
            self.maskShow=true;
            $('.nameCont').animate({'bottom':0},400);
        },
        //选择游戏名称
        chooseName(index,item){
            let self=this;
            self.fixCont=self.allData[index];
            console.log(self.fixCont);
            $('.gameName:first-child').find('span').html(item.gameName);
            self.maskShow=false;
            $('.nameCont').animate({'bottom':'-8.7rem'},0);
            //判断游戏账号是否显示
            if(self.fixCont.needGameAcct=='1') {
                self.needGameNumShow = true;
            }else{
                self.needGameNumShow = false;
            }
            //默认显示的游戏区服
            if(self.fixCont.areasList){
                for(let i=0;i<self.fixCont.areasList.length;i++){
                    if(self.fixCont.areasList[0].children){
                        $('.gameService').show();
                        self.fixCont.initArea=self.fixCont.areasList[0].text;
                        self.fixCont.initService=self.fixCont.areasList[0].children[0];
                    }else{
                        self.fixCont.initArea=self.fixCont.areasList[0];
                    }
                }
            }
        },
        //点击游戏选区
        gameAreaClick(){
            let self=this;
            if(self.fixCont.areaslen){
                self.maskShow=true;
                $('.areaCont').animate({'bottom':0},400);
            }
        },
        //选择选区
        chooseArea(index){
            let self=this;
            self.areaIndex=index;
            if(self.fixCont.areasList[self.areaIndex].children){
                self.serviceList=self.fixCont.areasList[self.areaIndex].children;
                $('.serviceCont').animate({'bottom':0},400);
            }
            if(self.fixCont.areaslen==1){
                $('.gameArea span').html(self.fixCont.areasList[self.areaIndex]);
                self.maskShow=false;
                $('.areaCont').animate({'bottom':'-8.7rem'},0);
            }
            if(self.fixCont.areaslen>=2){
                $('.gameArea span').html(self.fixCont.areasList[self.areaIndex].text);
            }
        },
        //点击服务器
        gameServiceClick(){
            let self=this;
            self.serviceList=self.fixCont.areasList[self.areaIndex].children;
            self.maskShow=true;
            $('.serviceCont').animate({'bottom':0},400);
        },
        //选择服务器
        chooseService(index){
            let self=this;
            $('.gameService span').html(self.serviceList[index]);
            self.maskShow=false;
            $('.areaCont').animate({'bottom':'-8.7rem'},0);
            $('.serviceCont').animate({'bottom':'-8.7rem'},0);
        },
        //点击充值类型
        gameTypeClick(index){
            $('.typeList li').eq(index).addClass('changeColor01');
            $('.typeList li').eq(index).find('p').addClass('changeColor02');
            $('.typeList li').eq(index).siblings().removeClass('changeColor01');
            $('.typeList li').eq(index).siblings().find('p').removeClass('changeColor02');
        },
        //点击面额
        gameMoneyClick(index){
            $('.moneyList li').eq(index).addClass('changeColor01');
            $('.moneyList li').eq(index).find('p').addClass('changeColor02');
            $('.moneyList li').eq(index).siblings().removeClass('changeColor01');
            $('.moneyList li').eq(index).siblings().find('p').removeClass('changeColor02');
            $('.payMoney span').html($('.moneyList li').eq(index).find('.pricePay').html()+'元');
            $('.fanMoney span').html((($('.moneyList li').eq(index).find('.pricePay').html())*rebate).toFixed(2)+'元');
            this.liIndex=index;
        },
        //确认充值
        sureClick(){
            let self = this;
            let quantityNum,times;
            let gameName,acctType,userName,goodsNum,actualAmount,gameAcct,gameArea,gameType,gameSrv;
            if(self.fixCont.priceTypeList){
                quantityNum=self.fixCont.priceTypeList[self.liIndex].quantity;
                times=self.fixCont.priceTimes;
            }else{ // goodsNum计算
                quantityNum=self.fixCont.priceTypeList[self.liIndex].quantity;
                times=self.fixCont.priceTimes;
            }
            if(self.needGameNumShow){ //游戏账号
                gameAcct=$('.needGameNum input').val();
            }else{
                gameAcct='';
            }
            if($('.gameName').hasClass('gameArea')){ //游戏区
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
                    acctType=$('.gameNum p').html();
                    userName=$('.gameNum input').val();
                    goodsNum=times*quantityNum;
                    actualAmount=$('.changeColor01 .pricePay').html();
                }
            if(self.dataType=='B'){
                gameName=$('.gameName:first-child').find('span').html();
                acctType=$('.nameDesc').html();
                userName=$('.nameDesc01').val();
                goodsNum=times*quantityNum;
                actualAmount=$('.changeColor01 .pricePay').html();
            }
            if($('.gameNum input').val() || (userName && ($('.nameconfirmDesc01').val()==userName) && !self.needGameNumShow) || (userName && ($('.nameconfirmDesc01').val()==userName) && gameAcct)){
                $.ajax({
                    type: 'post',
                    url: "/game/pay/order",
                    data:{'goodsId':goodsId,'gameName':gameName,'acctType':acctType,
                        'userName':userName,'goodsNum':goodsNum,'actualAmount':actualAmount,
                        'gameAcct':gameAcct,'gameArea':gameArea,'gameSrv':gameSrv,'gameType':gameType
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
                if(self.dataType=='B' && ($('.nameDesc01').val() != $('.nameconfirmDesc01').val())){
                    requestMsg($('.nameDesc').html()+'填写不一致！');
                }else{
                    requestMsg('信息填写不完整！');
                }
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
            $('.areaCont').animate({'bottom':'-8.7rem'},0);
            $('.serviceCont').animate({'bottom':'-8.7rem'},0);
        },
        maskClick01(){  //选择服务器里的返回
            let self=this;
            if($('.sameCont').hasClass('areaCont') && $('.areaCont').css('bottom')=='0px'){
                $('.serviceCont').animate({'bottom':'-8.7rem'},0);
            }else{
                self.maskShow=false;
                $('.serviceCont').animate({'bottom':'-8.7rem'},0);
            }

        }
    }
});


