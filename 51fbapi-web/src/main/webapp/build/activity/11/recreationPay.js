let goodsId=getUrl('goodsId'); //获取类型id
let discout=getUrl('discout'); //折扣
let rebate=getUrl('rebate'); //返利
$(function(){
    $('.footer').height('7%');
    $('.gamePay').css('padding-top','3%');
    let h=$(window).height()-$('.footer').height()-$(window).height()*0.015;
    $('.allTop').height(h);
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
        rebate:rebate
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
                    //需要先判断数据格式
                    $(self.content).find('game').each((index, item)=>{
                        self.allData.push(self.rewritedata(item));
                    });
                    console.log(self.allData, '全部数据');
                    console.log(self.allData.length, '全部数据长度');
                    self.initChooseFirst=self.allData[0];
                    console.log(self.allData[0], '默认显示第一个数据');
                    self.allDataLen=self.allData.length;
                    if(self.allDataLen==1){
                        self.fixCont=self.allData[0];
                    }
                    self.$nextTick(function(){
                        $('.gamePass input').attr('placeholder','请输入'+self.initChooseFirst.accountType);
                        $('.typeList li').eq(0).addClass('changeColor01');
                        $('.typeList li').eq(0).find('p').addClass('changeColor02');
                        $('.moneyList li').eq(0).addClass('changeColor01');
                        $('.moneyList li').eq(0).find('p').addClass('changeColor02');
                        $('.payMoney span').html($('.moneyList li').eq(0).find('.typePrice').html());
                        $('.fanMoney span').html((($('.moneyList li').eq(0).find('i').html())*rebate).toFixed(2)+'元');
                    });
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //点击游戏名称
        gameNameClick(){
            let self=this;
            let allGameName=[];
            for(let i=0;i<self.allData.length;i++){
                if(self.allData[i].gameName){
                    allGameName.push({value:i,text:self.allData[i].gameName});
                }
            }
            //根据所选游戏名称 填充数据fixCont
            let picker = new mui.PopPicker({
                layer: 1
            });
            picker.setData(allGameName);
            picker.pickers[0].setSelectedIndex(0);
            picker.show(function(SelectedItem) {
                let selectedData=SelectedItem[0].text;
                //console.log(SelectedItem);
                //console.log(selectedData);
                $('.gameName:first-child span').html(selectedData);
                self.fixCont=self.allData[SelectedItem[0].value];
                console.log(self.fixCont);
                picker.dispose();
            })
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
            $('.payMoney span').html($('.moneyList li').eq(index).find('.typePrice').html());
            $('.fanMoney span').html((($('.moneyList li').eq(index).find('i').html())*rebate).toFixed(2)+'元');
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
        }
    }
});
