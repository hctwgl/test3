var userName=getInfo().userName;

$(function(){
	$(".borrow").click(function(){
		if(userName!=''){
			location.href="/fanbei-web/opennative?name=BORROW_MONEY";
		}else{
			location.href="/fanbei-web/opennative?name=APP_LOGIN";
		}
	})
})