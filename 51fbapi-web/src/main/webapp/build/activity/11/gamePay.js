let goodsId=getUrl('goodsId'); //获取类型id
//获取数据
let vm = new Vue({
    el: '#gamePay',
    data: {
        content: {},
        fixCont:{},
        dataType:'',
        allData:[],
        gameNameShow:''
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
            if(getattr(_xml, 'game', 'name') || getselfattr(_xml, 'name')){  //存在时直接渲染  否则手动输入
                returnobj.gameName = getattr(_xml,'game', 'name')|| getselfattr(_xml, 'name');
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
        //数据格式为B
        fixDataB(xml){

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
                    if(self.dataType=='A'){
                        $('.gamePass').hide(); //无通行证这列
                        $(self.content).find('game').each((index, item)=>{
                            self.allData.push(self.rewritedata(item));
                            if(self.allData[index].gameName){
                                self.gameNameShow='Y';
                            }else{
                                self.gameNameShow='N';
                            }
                        });
                        console.log(self.allData, 11111);
                    }
                    if(self.dataType=='B'){
                       alert('B')
                    }
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
                $('.gameName:first-child span').css('color','#232323');
                self.fixCont=self.allData[SelectedItem[0].value];
                console.log(self.fixCont);
                picker.dispose();
            })
        },
        //点击游戏区服
        gameAreaClick(){
            let self=this;
            if(self.fixCont.areaslen){
                if(self.fixCont.areaslen==1){
                    gameAreaOne(self.fixCont.areasList);
                }
                if(self.fixCont.areaslen>=2){
                    gameAreaTwo(self.fixCont.areasList);
                }
            }
        },
        //字符串转数字
        fixStrToNum(str){
            let num=parseInt(str);
            return num;
        }
    }
});

//游戏区服二级联动
function gameAreaTwo(allCont){
    let picker = new mui.PopPicker({
                layer: 2
         });
    picker.setData(allCont);
    picker.pickers[0].setSelectedIndex(0);
    picker.pickers[1].setSelectedIndex(0);
    picker.show(function(SelectedItem) {
        let selectedData=SelectedItem[0].text+'&nbsp'+SelectedItem[1];
        //console.log(SelectedItem);
        //console.log(selectedData);
        $('.gameArea span').html(selectedData);
        $('.gameArea span').css('color','#232323');
        picker.dispose();
    })
}
//游戏区服一级联动
function gameAreaOne(allCont){
    let picker = new mui.PopPicker({
        layer: 1
    });
    picker.setData(allCont);
    picker.pickers[0].setSelectedIndex(0);
    picker.show(function(SelectedItem) {
        let selectedData=SelectedItem[0];
        //console.log(SelectedItem);
        //console.log(selectedData);
        $('.gameArea span').html(selectedData);
        $('.gameArea span').css('color','#232323');
        picker.dispose();
    })
}
