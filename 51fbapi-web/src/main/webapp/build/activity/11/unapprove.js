
let approve = getUrl("approve");//获取地址栏后面的approve参数；
if(approve){//当检测到这个参数的时候 让相应的模块隐藏掉 显示需要显示的模块
     $('#unapprove').hide();//隐藏审核失败页
    $('#approve').show();//显示审核成功页
}

$(function(){
        //点击申请提额请求接口
           $.ajax({
                url:'/fanbei-web/applyInterimAu',
                type:'post',
                success:function(data){
                    console.log(data);
                    let applyDate=JSON.parse(data);
                    let interimAmount=applyDate.data.interimAmount;//临时额度的值
                    console.log(interimAmount)
                    if(interimAmount==undefined){
                        $('.approveMoney').append(`<i class="approveMoney">0.00</i>`);
                    }else{
                        $('.approveMoney').append(`<i class="approveMoney">${interimAmount}</i>`);
                    }


                }
        }) 

})
