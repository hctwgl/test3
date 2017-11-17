let vm = new Vue({
    el:'#dynamicBill',
    data:{
        contentThree:'',
        ruleShow: ''
    },
    created:function(){
        this.logData();
    },
    methods:{
        logData(){
            let self=this;
            //修改成功页面初始化信息
            /* let outDay=document.getElementById("outDay").innerHTML;//获取出账日
            let payDay=document.getElementById("payDay").innerHTML;//获取还款日 */
            // console.log(payDay)
            var outDay= localStorage.getItem("outDay",outDay);//将手机号存储到本地
            var payDay=localStorage.getItem("payDay",payDay);//将短信验证码存储到本地
            console.log(outDay,'pengxiangyu');
            console.log(payDay,'payDay')
            $.ajax({
                type:'post',
                url:'/fanbei-web/changeOutDay/getUserOutDay',
                success:function(data){
                    self.contentThree = eval('(' + data + ')');
                    console.log(self.contentThree,'1111');
                    
                    
                }
                    
                
            })
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
        },
        //点击修改成功页面的返回首页按钮跳转到首页
        firstWeb(){
            window.location.href="http://yapp.51fanbei.com/fanbei-web/opennative?name=APP_HOME";//跳转到app首页
        },
        //原生调取方法
        goMain(){
            window.location.href="cdynamicBill?name=goMain";//跳转到app首页
        }
    }
})