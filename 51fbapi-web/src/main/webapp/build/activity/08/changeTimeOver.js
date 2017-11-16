
 let vm = new Vue({
    el:'#changbillDay',
    data:{
        contentOne:'',
        contentTwo:'',
        calendar:'',
        ruleShow: '',
        otherDate:'',
        over: false,
        msg: '',
        flag:true,
        isA:false
    },
    created:function(){
        this.logData();
    },
    mounted: function () {
        /* this.$nextTick(()=>{
            var mySwiper = new Swiper('.swiper-container', {
                direction: 'vertical',
                pagination: {
                    el: '.swiper-pagination',
                    clickable: true,
                },

            });

        }) */

    },
    methods:{
        logData(){
            let self=this;
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getUserOutDay',
                success:function(data){
                    self.contentOne = eval('(' + data + ')');
                    self.otherDate= self.contentOne.data.outDayList ? self.contentOne.data.outDayList.slice(0,1)[0]: [];
                    console.log( self.otherDate,' self.otherDate')
                    if(self.contentOne.msg==2){
                         /* self.flag=true;
                         self.isA=true; */
                            //alert(11111)
                            window.location.href='changeTimeOver';
                    } 
                    /* if(self.contentOne.success==false) {
                        window.location.href='http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';//未登录跳登录
                        
                    } */
                        
                }
                    
                
            })
            //禁止提交按钮的点击
            self.flag=false;

            
        },
        //点击提交按钮
        commit(){
            // return false;
            // 判断提交按钮是否能点击
            if(!this.flag){
                return false;
            }
            let self=this;
            let outDay=document.getElementById("outDay").innerHTML;//获取出账日
            let payDay=document.getElementById("payDay").innerHTML;//获取还款日
            // console.log(outDay,'outDay')
            localStorage.setItem("outDay",outDay);//将手机号存储到本地
            localStorage.setItem("payDay",payDay);//将短信验证码存储到本地
            console.log(outDay,'peng2222');
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/updateOutDay',
                data:{
                    'outDay':outDay,
                    'payDay':payDay,
                },
                success:function(data){
                    self.contentTwo = eval('(' + data + ')');
                    console.log(self.contentTwo,'self.contentTwo');
                    console.log(self.contentTwo.msg,'console.log')
                    window.location.href='changeSuccess';//点击提交跳转修改成功页面
                    //判断修改多次的时候跳到修改次数用完页面
                      /* if(self.contentTwo.msg==1){
                            window.location.href='changeTimeOver';
                    }   */
                     
                    
                }
                    
                
            }) 

            //点击提交按钮加埋点
            $.ajax({
               url:'/fanbei-web/postMaidianInfo',
               type:'post',
               data:{maidianInfo:'/fanbei-web/activity/barginIndex?type=changeTwo'},
               success:function (data) {
                     console.log(data)
                    }
            });



        },
        //点击修改次数用完页面的返回首页按钮跳转到首页
        firstWeb(){
            window.location.href="http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_HOME";//跳转app到首页
        },
        toPay(){
            window.location.href="http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_TOPAY";//跳转app还款页
        },
       /*  toMove(){
            window.location.href="http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_MOVE";//跳转app分期首页
        }, */
        //点击隐藏mask
        maskHide(){
            $('.mask').hide();
        },
        //点击显示mask
        maskShow(){
            // this.isA=true;//点击选择年月日的时候提交按钮变亮
            // this.flag=true;//点击选择年月日的时候点击提交可跳转
            let self=this;
            if(self.over) {
                requestMsg(self.msg)
                return
            }
            $('.dynamic-center').show();
            let dateList=self.contentOne.data.outDayList;
            let list=[];
            let a='';
            for(let i=0;i<dateList.length;i++){
                a='每月'+dateList[i].outDay+'日'+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+'每月'+dateList[i].payDay+'日';
            list.push(a);
            }

            refundState(list)
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

function refundState(data){
    var picker = new mui.PopPicker();
    picker.setData(data)
    picker.pickers[0].setSelectedIndex(0, 2000);
    picker.show(function(SelectedItem) {
        let stateStatus=SelectedItem[0];
        let bb=stateStatus.split("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        let theFirst=bb[0].slice(2,-1);
        let theSecond=bb[1].slice(2,-1);
        console.log(theFirst,theSecond)
         var monthOne="";//出账日
         var monthTwo="";//还款日
        monthOne+="<i class='monthOne startTime'>"+'每月'+"<i id='outDay'>"+theFirst+"</i>"+"</i>"+'号'+"</i>";//字符串拼接到显示的出账日里面
        $('.monthOne').html(monthOne);
        monthTwo+="<i class='monthTwo startTime'>"+'每月'+"<i id='payDay'>"+theSecond+"</i>"+"</i>"+'号'+"</i>";//字符串拼接到显示的还款日里面
        $('.monthTwo').html(monthTwo);
        //$('.stateStatus').text(SelectedItem[0].text);
        vm.isA=true;//点击选择年月日的时候提交按钮变亮
        vm.flag=true;//点击选择年月日的时候点击提交可跳转
        picker.dispose(); 
    })

}


