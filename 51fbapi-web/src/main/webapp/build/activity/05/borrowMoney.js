var userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}

$(function(){
	$(".borrow").click(function(){
		if(userName!=''){
			location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=BORROW_MONEY";
		}else{
			location.href="http://testapp.51fanbei.com/fanbei-web/opennative?name=APP_LOGIN";
		}
	})
})