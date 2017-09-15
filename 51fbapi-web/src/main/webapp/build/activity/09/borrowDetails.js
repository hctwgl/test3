
let vue=new Vue({
    el:'#vueCon',
    data:{
        content:{
            buttonString:'立即签到',
            canClick: false
        },
        day:[
            {img:'3.png', txt:'5元红包',day:1,style:''},
            {img:'6.png', txt:'第2天',day:2},
            {img:'6.png', txt:'第3天',day:3},
            {img:'6.png', txt:'第4天',day:4},
            {img:'3.png', txt:'3元现金20元红包',day:5,style:''},
            {img:'3.png', txt:'15元现金50元红包',day:10,style:'hei'},
            {img:'6.png', txt:'第9天',day:9,style:'hei'},
            {img:'6.png', txt:'第8天',day:8,style:'hei'},
            {img:'6.png', txt:'第7天',day:7,style:'hei'},
            {img:'6.png', txt:'第6天',day:6,style:'hei'},
            {img:'6.png', txt:'第11天',day:11},
            {img:'6.png', txt:'第12天',day:12},
            {img:'6.png', txt:'第13天',day:13},
            {img:'6.png', txt:'第14天',day:14},
            {img:'3.png', txt:'25元现金75元红包',day:15,style:''},
        ],
        dialog:{
            show:false,
            prizeShow:false,
            confId:'',
            txt:'获得现金'
        }
    },
    created:function () {
        this.init();
    },
    methods:{
        changeImg(num){                  //签到图片替换
            this.day.forEach(function (data) {
                if(data.day<=num){
                    if(data.day%5===0){
                        data.img='4.gif';
                        data.style='active';
                    }else{
                        data.img='5.png'
                    }
                }
            });
            if(num>=1){
                this.day[0].img='4.gif';
                this.day[0].style='active';
            }
        },
        init(){        //初始化数据
            let self=this;
            $.ajax({
                url:'/fanbei-web/signActivity?userName=18072975670',
                success:function (data) {
                    data = eval('(' + data + ')');
                    console.log(data);
                    if(data.success){
                        self.content=data.data;
                        self.changeImg(self.content.signDays);
                        //红包文案修改
                        self.day[0].txt=self.content.gameConfList[0].couponNames.join('');
                        self.day[4].txt=self.content.gameConfList[1].couponNames.join('');
                        self.day[5].txt=self.content.gameConfList[2].couponNames.join('');
                        self.day[14].txt=self.content.gameConfList[3].couponNames.join('');
                    }
                }
            })


        },
        prize(day){     //点击步骤图片弹出领奖
            if(this.content.canClick){
                if((day===1||day===5||day===10||day===15)&&this.content.signDays>=day){
                    this.dialog.show=true;
                    let i=(day%5===0)?day/5:0;
                    this.dialog.confId=this.content.gameConfList[i].rid;
                    this.dialog.txt=this.content.gameConfList[i].couponNames.join('');

                }
            }
        },
        go(state){       //大图按钮
            if(state){
                requestMsg('可以签到')
            }
        },
        receive(){     //领取按钮
            let self=this;
            $.ajax({
                url:'/fanbei-web/receiveSignAward?userName=18072975670&confId='+self.dialog.confId,
                success:function (data) {
                    data = eval('(' + data + ')');
                    if(data.success){
                      self.dialog.show=false;
                      self.dialog.prizeShow=true;
                      self.init();
                    }else{
                        requestMsg(data.msg)
                    }
                }
            })
        }
    }
});