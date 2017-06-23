/*
* @Author: yoe
* @Date:   2017-04-12 10:53:28
* @Last Modified by:   yoe
* @Last Modified time: 2017-06-07 14:06:30
*/

var token = formatDateTime()+Math.random().toString(36).substr(2);

var style = $("#style").val();

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

// 同盾校验编号的sessionId
var _fmOpt;
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

// 判断手机号、接收验证码
$(function(){

	var timerInterval ;
	var timerS = 60;
	function timeFunction(){ // 60s倒计时
    timerS--;
    if (timerS<=0) {
    	$("#register_codeBtn").removeAttr("disabled");
     	$("#register_codeBtn").text("获取验证码");
     	clearInterval(timerInterval);
     	timerS = 60;
			$("#register_codeBtn").attr("isState",0);
    } else {
     	$("#register_codeBtn").text(timerS+" s");
    }
	};

	// 获取验证码
	$("#register_codeBtn").click(function(){
		var isState = $(this).attr("isState");
		var mobileNum = $("#register_mobile").val();
		if ( (isState==0 || !isState) && !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
			$("#register_codeBtn").attr("disabled",true);
			$.ajax({
  			url: "/app/user/getRegisterSmsCode",
  			type: "POST",
  			dataType: "JSON",
  			data: {
  				mobile: mobileNum
  			},
  			success: function(returnData){
  				if (returnData.success) {
    				$("#register_codeBtn").attr("isState",1);
						$("#register_codeBtn").text(timerS+" s");
           	timerInterval = setInterval(timeFunction,1000);
  				} else {
  					requestMsg(returnData.msg);
  					$("#register_codeBtn").removeAttr("disabled");
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

  // 提交注册
  $("#register_submitBtn").click(function(){ // 完成注册提交
		// md5加密
		var register_password = $("#register_password").val();
		var password_md5 = String(CryptoJS.MD5(register_password));
		var passwordLength = register_password.length;

		// 正则判断密码为6-18位字母+字符的组合
		var pwdReg = /^(?![^a-zA-Z]+$)(?!\\D+$).{6,18}$/;
		var password = pwdReg.test(register_password);

    var mobileNum = $("#register_mobile").val();
    var register_verification = $("#register_verification").val();
		var channelCode = $("#channelCode").val();
		var pointCode = $("#pointCode").val();

		var isState = $("#register_codeBtn").attr("isState");

		if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum) && mobileNum != "" ){ // 判断电话开头
			if ( register_verification != "" ) { // 验证码不能为空
				if ( password && 6 <= passwordLength <= 18 ) { // 密码6-18位
					if ($("#input_check").is(":checked")) { // 判断当前是否选中
						if ( $("#register_codeBtn").attr("isState") == 1 ) {
              // 检测访问量
              if ( style==7 ) {
                _taq.push({convert_id:"62421367574", event_type:"form"})
              } else if ( style==10 ) {
                _taq.push({convert_id:"61747053456", event_type:"form"})  // oppo导流id
              } else {
                _taq.push({convert_id:"59212981134", event_type:"form"})
              }

							$.ajax({ // 设置登录密码
								url: "/app/user/commitChannelRegister",
								type: 'POST',
								dataType: 'JSON',
								data: {
									registerMobile: mobileNum,
									smsCode: register_verification,
									password: password_md5,
									channelCode: channelCode,
									pointCode: pointCode,
									token:token
								},
								success: function(returnData){
                  if ( style==10 || style==12 ) {  // 样式12弹窗
                    $("#register_submitBtn").attr("disabled",true);
                    $(".registerSuss").removeClass("hide");  // 显示弹窗，样式10
                    $(".registerMask").removeClass("hide");  // 显示弹窗，样式10

                    $("#downloadApp").click(function(){  // 点击下载app
                      window.location.href = returnData.url;
                    });
                  } else {
                    if ( returnData.success ) {
  										$("#register_submitBtn").attr("disabled",true);
  										window.location.href = returnData.url;
  									} else {
  										requestMsg(returnData.msg);
  									}
                  }
								},
								error: function(){
                  if ( style==10 || style==12 ) {
                    $(".registerFail").removeClass("hide");  // 显示弹窗
                    $("#repeatRegister").click(function(){  // 点击关闭弹窗
                      $(".registerMask").addClass("hide");
                      $(".registerFail").addClass("hide");
                    });
                  }else {
                    requestMsg("注册失败");
                  }
								}
							})
						} else {
							requestMsg("请输入正确的验证码");
						}
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
    };
  });
});
