
let vue=new Vue({
    el:'#vueCon',
    data:{
        content:{
            buttonString:'立即签到',
            canClick: false
        },
        day:[
            {img:'31.png', txt:'5元红包',canReceive:false,day:1,style:''},
            {img:'6.png', txt:'第2天',day:2},
            {img:'6.png', txt:'第3天',day:3},
            {img:'6.png', txt:'第4天',day:4},
            {img:'31.png', txt:'3元现金20元红包',canReceive:false,day:5,style:''},
            {img:'31.png', txt:'15元现金50元红包',canReceive:false,day:10,style:'hei'},
            {img:'6.png', txt:'第9天',day:9,style:'hei'},
            {img:'6.png', txt:'第8天',day:8,style:'hei'},
            {img:'6.png', txt:'第7天',day:7,style:'hei'},
            {img:'6.png', txt:'第6天',day:6,style:'hei'},
            {img:'6.png', txt:'第11天',day:11},
            {img:'6.png', txt:'第12天',day:12},
            {img:'6.png', txt:'第13天',day:13},
            {img:'6.png', txt:'第14天',day:14},
            {img:'31.png', txt:'25元现金75元红包',canReceive:false,day:15,style:''},
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
                        data.img='3.png';
                        data.style='unactive';
                    }else{
                        data.img='5.png'
                    }
                }
                if(data.canReceive){
                    data.img='4.gif';
                    data.style='active';
                }
            });
            if(num>=1){
                if(this.day[0].canReceive){
                    data.img='4.gif';
                    data.style='active';
                }else{
                    this.day[0].img='3.png';
                    data.style='unactive';
                }
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
                        //红包是否可点击
                        self.day[0].canReceive=self.content.gameConfList[0].canReceive;
                        self.day[4].canReceive=self.content.gameConfList[1].canReceive;
                        self.day[5].canReceive=self.content.gameConfList[2].canReceive;
                        self.day[14].canReceive=self.content.gameConfList[3].canReceive;
                        self.changeImg(self.content.signDays);
                    }
                }
            })


        },
        prize(data){     //点击步骤图片弹出领奖
            if(this.content.canClick&&data.canReceive){
                if((data.day===1||data.day===5||data.day===10||data.day===15)&&this.content.signDays>=data.day){
                    this.dialog.show=true;
                    let i=(data.day%5===0)?data.day/5:0;
                    this.dialog.confId=this.content.gameConfList[i].rid;
                    this.dialog.txt=data.txt;
                }
            }
        },
        go(){       //大图按钮
            if(this.content.canClick){
                window.location.href='/fanbei-web/opennative?name=BORROW_MONEY'
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