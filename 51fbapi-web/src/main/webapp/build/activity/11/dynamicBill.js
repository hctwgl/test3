let vm = new Vue({
    el:'#dynamicBill',
    data:{
        content:'',
        contentOne:'',
        contentThree:'',
        ruleShow: '',
    },
    created:function(){
        this.logData();
    },
    methods:{
        logData(){
            //当前账单初始化信息
            let self=this;
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getUserOutDay',
                success:function(data){
                    self.content = eval('(' + data + ')');
                    console.log(self.content);

                    if(self.content.success==false){
                        window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
                    }
                     
                    
                }
                    
                
            })

        },
        //点击修改账单日
        changeBillDay(){
             $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getOutDayList',
                success:function(data){
                    self.contentOne = eval('(' + data + ')');
                     window.location.href='changebillDay?testUser=17839218825';//没有逾期账单跳修改账单日页面
                        if(self.contentOne.msg==1){
                            window.location.href='cunpaidBill';//有逾期账单返回1跳修账单为还清页面
                            }else if(self.contentOne.msg==2){
                            window.location.href='changetimeOver';//有逾期账单返回2跳账修改次数用完页面
                        } 
                     
                    
                }
                    
                
            }) 
            refundState()
        },
        // 点击活动规则
        ruleClick() {
            let self = this;
            self.ruleShow = 'Y';
        },
        //点击蒙版
        maskClick() {
            let self = this;
            self.ruleShow = '';
        }
    }
})
/* function refundState(){
    var picker = new mui.PopPicker();
    picker.setData([{
        value: "REFUNDING",
        text: "退款中",
    }, {
        value: "REFUND",
        text: "已退款"
    }, {
        value: "REFUNDFAIL",
        text: "退款失败"
    }
    ])
    picker.pickers[0].setSelectedIndex(0, 2000);
    picker.show(function(SelectedItem) {
        stateStatus=SelectedItem[0].value;
        $('.startTime').text(SelectedItem[0].text);
        picker.dispose(); 
    })
} */
