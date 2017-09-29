
//获取数据
let vm = new Vue({
    el: '#loanDetail',
    data: {
        content: {},
        flowCont:[
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail01.png','name':'身份认证'},
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail02.png','name':'银行卡认证'},
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail03.png','name':'运营商认证'},
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail04.png','name':'个人信息'},
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail05.png','name':'通讯录认证'},
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail06.png','name':'芝麻信用'},
            {'imgUrl':'http://f.51fanbei.com/h5/app/activity/09/loanDetail07.png','name':'等待放款'}
        ]
    },
    created: function () {
        //this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo",
                data:{'modelId':modelId},
                success: function (data) {
                    self.content = eval('(' + data + ')').data;
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        }
    }
});
//贷款额度
function moneyLimit(){
    console.log(1);
    let picker = new mui.PopPicker();
    picker.setData([{
        value: "1",
        text: "30",
    }, {
        value: "2",
        text: "60"
    }, {
        value: "3",
        text: "120"
    }
    ]);
    // picker.pickers[0].setSelectedIndex(0, 2000);
    picker.show(function(SelectedItem) {
        $('.moneyLimit').val(SelectedItem[0].text);
        picker.dispose();
    })
}

