

// let startTime='',endTime='';
// //增加开始日期
// function addStartDate(){
//     // dtpicker组件适用于弹出日期选择器
//     var dtpicker = new mui.DtPicker({
//         type: "date", //设置日历初始视图模式
//         labels: ['年', '月', '日'] //设置默认标签区域提示语
//     });

//     dtpicker.show(function(items) {
//         startTime=items.y.value + "-"+ items.m.value + "-"+ items.d.value;
//         var dateStr = items.y.value + "-" + items.m.value + "-" + items.d.value + " ";
//         $('.startTime').text(dateStr);
//         dtpicker.dispose();
//     })
// }

// //增加结束日期
//  function addEndDate(){
//     // dtpicker组件适用于弹出日期选择器
//     var dtpicker = new mui.DtPicker({
//         type: "date", //设置日历初始视图模式
//         labels: ['年', '月', '日'] //设置默认标签区域提示语
//     });

//     dtpicker.show(function(items) {
//         endTime = items.y.value + "-" + items.m.value + "-" + items.d.value;
//         var dateStr = items.y.value + "-" + items.m.value + "-" + items.d.value + " ";
//         $('.endTime').text(dateStr);
//         dtpicker.dispose();
//     })
// } 

// /* function addEndDate(){
//     // dtpicker组件适用于弹出日期选择器
//     var dtpicker = new mui.DtPicker({
//         type: "Int", //设置日历初始视图模式
//         picker:2
//     });

//     dtpicker.show(function(items) {
//         endTime = items.y.value + "-" + items.m.value + "-" + items.d.value;
//         var dateStr = items.y.value + "-" + items.m.value + "-" + items.d.value + " ";
//         $('.endTime').text(dateStr);
//         dtpicker.dispose();
//     })
// } */


// //选择开始结束时间 
// $(".dateOne").click(function(){
//      addStartDate(); 
// });

//  $(".dateTwo").click(function(){
//     addEndDate();
// }); 

// //返回
// $('.return').click(function(){
//     window.location.href=paraArr+".html";
// });
// //完成
// $('.finish').click(function(){
//     if(Number(endTime)>0&&Number(endTime)<Number(startTime)){
//         requestMsg('结束时间不能小于开始时间')
//     }else{
//         window.location.href=paraArr+".html?startTime="+startTime+'&endTime='+endTime+'&stateStatus='+stateStatus;
//     }
// });


 let vm = new Vue({
    el:'#changbillDay',
    data:{
        contentOne:'',
        contentTwo:'',
        calendar:'',
        ruleShow: '',
        otherDate:''
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
                url:'/fanbei-web/changeOutDay/getOutDayList',
                success:function(data){
                    self.contentOne = eval('(' + data + ')');
                    self.otherDate=self.contentOne.data.outDayList[0];
                    console.log(self.otherDate,'self.otherDate');
                    console.log(self.contentOne);
                    if(self.contentOne.success==false){
                        window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';//未登录跳登录
                    }
                     
                    
                }
                    
                
            })


            
        },
        //点击提交按钮
        commit(){
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
                    window.location.href='changeSuccess?testUser=17839218825';//点击提交跳转修改成功页面
                     if(self.contentTwo.success==false){
                        //window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';//未登录跳登录
                    } 
                     
                    
                }
                    
                
            }) 
        },
        //点击修改次数用完页面的返回首页按钮跳转到首页
        firstWeb(){
            window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_HOME";//跳转app到首页
        },
        toPay(){
            window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_TOPAY";//跳转app还款页
        },
        toMove(){
            window.location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_MOVE";//跳转app分期首页
        },
        //点击隐藏mask
        maskHide(){
            $('.mask').hide();
        },
        //点击显示mask
        maskShow(){
            let self=this;
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
        picker.dispose(); 
    })
}
