let lsmNo=getUrl('lsmNo');//获取借款超市的编码
//获取数据
let vm = new Vue({
    el: '#loanDetail',
    data: {
        content: {
            moneyMin:500,
            moneyMax:3000,
            timeMin:2,
            timeMax:20,
            lsmName:'51fanbei',
            timeUnit:2,
            isRegister:0
        },
        flowCont:[]
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
                url: "/borrowCash/getRegisterLoanSupermarket",
                data:{
                    lsmNo:lsmNo
                },
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                    //流程map
                    let stat=[
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail01.png','name':'身份认证'},
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail02.png','name':'银行卡认证'},
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail03.png','name':'运营商认证'},
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail04.png','name':'个人信息认证'},
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail05.png','name':'通讯录认证'},
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail06.png','name':'芝麻信用认证'},
                        {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail07.png','name':'等待认证'}
                    ];
                    let con=self.content.applyProcess.split(',');
                    for(let i=0;i<con.length;i++){
                        for(let j=0;j<stat.length;j++){
                            if(con[i]==stat[j].name){
                                self.flowCont.push(stat[j])
                            }
                        }
                    }
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        fen(data){
            return data.split('；');
        },
        dayMonth(){
            if(this.content.timeUnit==2){
                return '月'
            }else{
                return '日'
            }
        },
        btnTxt(){
            if(this.content.isRegister==1){return '打开'}else{return '提交资料并申请'}
        },
        submit(){
            $.ajax({
                type: 'post',
                url: "/unionRegister/third/register",
                data:{
                    lsmNo:lsmNo,
                    phone:getInfo().userName
                },
                success: function (data) {
                    window.location.href=data.data.return_url
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        }
    }
});
//贷款额度
// function moneyLimit(){
//     console.log(1);
//     let picker = new mui.PopPicker();
//     picker.setData([{
//         value: "1",
//         text: "30",
//     }, {
//         value: "2",
//         text: "60"
//     }, {
//         value: "3",
//         text: "120"
//     }
//     ]);
//     // picker.pickers[0].setSelectedIndex(0, 2000);
//     picker.show(function(SelectedItem) {
//         $('.moneyLimit').val(SelectedItem[0].text);
//         picker.dispose();
//     })
// }

