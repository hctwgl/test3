let allDate;
let failureStatus;
let againApplyDesc;
let amount;
let type;
let gmtFailuretime;
let interimAmount;
let rule;
let ruleTitle;
let upMoney=document.getElementById('upMoney');


$(function(){
    //初始化信息请求
    getIntitalData(dataloadcb);

    //提额成功
    let applySuccess = getUrl("applySuccess");//获取地址栏后面的approve参数；
    if(applySuccess){//当检测到这个参数的时候 改变相应的功能
        $('.applyButton').css({'background-color':'#999','box-shadow':'none'});//隐改变按钮颜色
        $('.applyButton').html('暂时无法再次提额');//改变按钮文字
        /* upMoney.addEventListener('click',function(e){//禁止点击事件
        　　e.preventDefault();

        });  */
        if(failureStatus==1){//判断临时额度是否失效
            $('.useless').show();//失效显示文字
        }
    } 

    //提额申请失败
    let unapprove = getUrl("unapprove");//获取地址栏后面的approve参数；
    if(unapprove){//当检测到这个参数的时候 改变相应的功能
        $('.applyButton').css({'background-color':'#999','box-shadow':'none'});//隐改变按钮颜色
        setTimeout(function() {
            $('.applyButton').html(`${againApplyDesc}`);//改变按钮文字
        }, 0)
        // $('.applyButton').html(`${againApplyDesc}`);//改变按钮文字
        /* upMoney.addEventListener('click',function(e){//禁止点击事件
        　　e.preventDefault();
        }); */   

    }
    
    //点击申请提额
    $('.applyButton').click(function(){ 

         if(unapprove || applySuccess){//判断当有这个参数的时候禁止点击事件
            upMoney.addEventListener('click',function(e){
            　　e.preventDefault();
            
            });
            return false;

        }
 
        //跳转正在审核页面
        $('.applyButton').hide();
        $('.verirication').show();   

        //点击申请提额请求接口
           $.ajax({
                url:'/fanbei-web/applyInterimAu',
                type:'post',
                success:function(data){
                    let applyDate=JSON.parse(data);
                    console.log(applyDate,'applyDate')
                    //审核页面跳转判断
                    setTimeout(function () {//让判断延迟6秒执行
                             if(applyDate.data.type==3){//3申请失败
                                window.location.href='/fanbei-web/activity/unapprove';//跳转审核结果页
                            }
                            if(applyDate.data.type==1){//1申请通过
                                window.location.href='/fanbei-web/activity/unapprove?approve=show';//跳转审核结果页
                            } 
                    }, 1000); 
                    if(applyDate.data.type==55){
                        window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';//未登录跳转登录页面
                    } 
                    if(applyDate.data.type==2){//判断未经过强风控
                        $('.popUp').show();//显示弹窗
                            //并且阻止跳转正在审核页面
                            $('.applyButton').show();
                            $('.verirication').hide();
                    }

                }
        }) 
        //点击提额埋点
         $.ajax({
                url:'/fanbei-web/postMaidianInfo',
                type:'post',
                data:{maidianInfo:'/fanbei-web/activity/moneyApplyFor?type=applyMoney'},
                success:function (data) {
                }
            });      
    })

    //点击弹窗确定按钮隐藏整个弹窗
    $('.buttonBox').click(function(){
        $('.popUp').hide();//隐藏弹窗
    })

})


function dataloadcb(type) {
    switch(type) {
        case 0: 
            applysuccess()
            break
        case 1:
            applyrefuse()
            break
        case 2:
            initPage()
            break
        default:
            initPage()
    }
}

/**
 * 区分页面状态执行不同的逻辑
 */
//-------------初始化页-------------------
function initPage(){
    //临时额度和失效日期字段没有时显示的文字
    if(interimAmount==undefined || gmtFailuretime==undefined){
        $('.limitMoney').html('0.00');//临时额度
        $('.detailDate').html('----');//失效日期
        $('.detailDate').css({'color':'#999','font-size':'0.42rem','line-height':'0.4rem'});//设置字体颜色大小
    } 
    
}

//-------------审核失败-------------------
function applyrefuse(){
    //临时额度和失效日期字段没有时显示的文字
    if(interimAmount==undefined || gmtFailuretime==undefined){
            $('.limitMoney').html('0.00');//临时额度
            $('.detailDate').html('----');//失效日期
            $('.detailDate').css({'color':'#999','font-size':'0.42rem','line-height':'0.4rem'});//设置字体颜色大小
    } 


}
//-------------审核成功-------------------
function applysuccess(){
    //临时额度和失效日期字段没有时显示的文字
    if(interimAmount==undefined || gmtFailuretime==undefined){
            $('.limitMoney').html('0.00');//临时额度
            $('.detailDate').html('----');//失效日期
            $('.detailDate').css({'color':'#999','font-size':'0.42rem','line-height':'0.4rem'});//设置字体颜色大小
    } 
    
}


//---------------//初始化信息请求--------------
function getIntitalData(callback){
    $.ajax({
        url:'/fanbei-web/applyInterimAuPage',
        type:'post',
        success:function(data){
            allDate=JSON.parse(data);
            console.log(allDate,'allDateff');
            if(allDate.success==false){
                window.location.href='http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN';//未登录跳转登录页面
            } 
            failureStatus=allDate.data.failureStatus;
            interimAmount=allDate.data.interimAmount;//临时额度
            gmtFailuretime=allDate.data.gmtFailuretime;//失效日期
            amount=allDate.data.amount;//当前额度
            againApplyDesc =allDate.data.againApplyDesc;//申请按钮上的描述
            console.log(againApplyDesc,'againApplyDesc');
            type=allDate.data.type;//类型
            rule=allDate.data.rule;//规则
            ruleTitle=allDate.data.ruleTitle;//规则标题
            $('.Money').append(`<span class="Money">${amount}</span>`);//当前额度
            $('.limitMoney').append(`<span class="limitMoney">${interimAmount}</span>`);//临时额度
            $('.detailDate').append(`<span class="detailDate">${gmtFailuretime}</span>`);//失效日期
            $('.moneyApply-rule').append(`<div class="ruleWord">${rule}</div>`);//规则
            $('.wordTitle').append(`<div class="wordTitle">${ruleTitle}</div>`);//规则标题 
            console.log(allDate.success,'allDate.success') 

            callback(type);

        }
    })
}


//客户端调用方法
//从审核没通过结果页到审核没通过页面
/* function turnUnapprove(){
    window.location.href='/fanbei-web/activity/moneyApplyFor?unapprove=show';//临时额度调整(审核没通过)

} */



//提额成功页面到申请成功页面
/* function applySuccess(){
    window.location.href='/fanbei-web/activity/moneyApplyFor?applySuccess=show';//临时额度调整(申请成功)
} */







