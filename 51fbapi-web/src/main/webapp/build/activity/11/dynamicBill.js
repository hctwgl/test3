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
                        
                        function creadiv(l,r,t){ //l是距左的距离,r是距右的距离,t是要显示的文本内容
                            var dd=document.createElement("div");
                            dd.style.position="absolute";
                            dd.style.left=111+"px";
                            dd.style.right=0+"px"
                            dd.innerText='您还未登录，请先进行登录';
                            document.body.appendChild(dd);
                        }
                        creadiv();
                        $('body').style('background-color','red');




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
