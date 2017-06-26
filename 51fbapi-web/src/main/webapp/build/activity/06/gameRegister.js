
var token = formatDateTime()+Math.random().toString(36).substr(2);

function formatDateTime() {    
    var date = new Date();  
    var y = date.getFullYear();    
    var m = date.getMonth() + 1;    
    m = m < 10 ? ('0' + m) : m;    
    var d = date.getDate();    
    d = d < 10 ? ('0' + d) : d;  
    var h = date.getHours();  
    var minute = date.getMinutes();  
    var second = date.getSeconds();
    return y +  m +  d +h +minute+second;    
};  

var _fmOpt;
// 同盾校验编号的sessionId
 (function() {
    _fmOpt = {
         partner: 'alading',
         appName: 'alading_web',
         token: token
     };
     var cimg = new Image(1,1);
     cimg.onload = function() {
         _fmOpt.imgLoaded = true;
     };
     cimg.src = ('https:' == document.location.protocol ? 'https://' : 'http://') +"fp.fraudmetrix.cn/fp/clear.png?partnerCode=alading&appName=alading_web&tokenId=" + _fmOpt.token;
     var fm = document.createElement('script'); fm.type = 'text/javascript'; fm.async = true;
     fm.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'static.fraudmetrix.cn/fm.js?ver=0.1&t=' + (new Date().getTime()/3600000).toFixed(0);
     var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(fm, s);
 	// alert(json.msg);
 })();

// 根据点击显示input文字
$('.input').on('keydown',function () {
	$(this).next().hide()
});
$('.input').on('keyup',function () {
    let self=$(this);
    if(self.val()==''){
        self.next().show()
    }
});

function gou() {
    if ($("#input_check").is(":checked")) {
    	$('.gou').show()
	}else{
        $('.gou').hide()
    }
}

// 判断手机号、接收验证码
$(function(){
	var timerInterval ;
	var timerS = 60;
	function timeFunction(){ // 60s倒计时
        timerS--;
        if (timerS<=0) {
         	$(".time").hide();
         	clearInterval(timerInterval);
         	timerS = 60;
        } else {
         	$(".time").show();
            $(".time1").text(timerS+" s");
        }
	}

	$("#codeBtn").click(function(){ // 获取验证码
		let phone = $("#phone").val();
		if (/^1(3|4|5|7|8)\d{9}$/i.test(phone)){
	     	$.ajax({
    			url: "/app/user/getRegisterSmsCode",
    			type: "POST",
    			dataType: "JSON",
    			data: {
    				mobile: phone,
    				token: token
    			},
    			success: function(returnData){
    				if (returnData.success) {
                        $(".time").show();
                        $(".time1").text(timerS+"s");
             			timerInterval = setInterval(timeFunction,1000);
    				} else {
    					requestMsg(returnData.msg);
    				}
    			},
    			error: function(){
		      		requestMsg("请求失败");
    			}
    		})
		} else{
	  		requestMsg("请填写正确的手机号");
		}
	});


	$("#submit").click(function(){ // 完成注册提交
        let recommendCode = getUrl("recommendCode");

        let phone = $("#phone").val();
        let code = $("#code").val();
        let password = $("#password").val();
        // md5加密
		let password_md5 = String(CryptoJS.MD5(password));
		let passwordLength = password.length;

		// 正则判断密码为6-18位字母+字符的组合
		let pwdReg = /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/;
		let ispassword = pwdReg.test(password);


		if(/^1(3|4|5|7|8)\d{9}$/i.test(phone)){ // 判断电话开头
			if ( code != "" ) { // 验证码不能为空
				if ( ispassword && 6 <= passwordLength <= 18 ) { // 密码6-18位
					if ($("#input_check").is(":checked")) { // 判断当前是否选中
						$.ajax({ // 设置登录密码
							url: "/app/user/commitRegister",
							type: 'POST',
							dataType: 'JSON',
							data: {
								registerMobile: phone,
								smsCode: code,
								password: password_md5,
								recommendCode: recommendCode,
								token:token
							},
							success: function(returnData){
								if ( returnData.success ) {
									$("#submit").off("click");
									window.location.href = returnData.url;
								} else {
									requestMsg(returnData.msg);
								}
							},
							error: function(){
								requestMsg("注册失败");
							}
						})
					} else {
						requestMsg("请阅读并同意《51返呗用户注册协议》");
					}
				}else{
					requestMsg("请填写6-18位的数字、字母、字符组成的密码");
				}
			} else {
				requestMsg("请输入验证码");
			}
		} else{
			requestMsg("请填写正确的手机号");
		}
	});
});

