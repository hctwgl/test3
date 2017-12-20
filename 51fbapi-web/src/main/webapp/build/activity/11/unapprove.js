

let approve = getUrl("approve");//获取地址栏后面的approve参数；
if(approve){//当检测到这个参数的时候 让相应的模块隐藏掉 显示需要显示的模块
     $('#unapprove').hide();//隐藏审核失败页
    $('#approve').show();//显示审核成功页
}
