
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
        //this.logData();
    },
    mounted: function () {
        this.logData();
    },
    methods:{
        logData(){
            let self=this;
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getUserOutDay',
                success:function(data){
                    self.contentOne = eval('(' + data + ')');
                        
                }
            })
            //禁止提交按钮的点击
            self.flag=false;   
        },
        //点击修改次数用完页面的返回首页按钮跳转到首页
        firstWeb(){
            window.location.href="http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_HOME";//跳转app到首页
        },
        //点击隐藏mask
        maskHide(){
            $('.mask').hide();
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


