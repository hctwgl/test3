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
                        location.href='http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';
                    }
                     
                    
                }
                    
                
            })

            //统计pvuv
             $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:location.pathname+'?type=pvuv'},
                success:function (data) {
                    console.log(data)
                }
            });

        },
        //点击修改账单日
        changeBillDay(){
             $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getOutDayList',
                success:function(data){
                    self.contentOne = eval('(' + data + ')');
                     window.location.href='/fanbei-web/activity/changebillDay';//没有逾期账单跳修改账单日页面
                        if(self.contentOne.msg==1){
                            window.location.href='cunpaidBill';//有逾期账单返回1跳修账单为还清页面
                            }else if(self.contentOne.msg==2){
                            window.location.href='changeTimeOver';//有逾期账单返回2跳账修改次数用完页面
                        } 
                     
                    
                }
                    
                
            }) 
            refundState()

              //点击修改账单日加埋点
               $.ajax({
                      url:'/fanbei-web/postMaidianInfo',
                      type:'post',
                      data:{maidianInfo:'/fanbei-web/activity/barginIndex?type=changOne'},
                      success:function (data) {
                      console.log(data)
                        }
                    });



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
