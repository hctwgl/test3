/*
* @Date:   2017-04-12 10:53:28
*/

var token=formatDateTime()+Math.random().toString(36).substr(2);

var style=$("#style").val();  // 样式
var os=getBlatFrom(); // 1是android，2是ios


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
        } else {
         	$("#register_codeBtn").text(timerS+" s");
        }
	};

	// 获取图形验证码
    $("#register_codeBtn").click(function(){
        var mobileNum = $("#register_mobile").val();
        if ( !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
            $.ajax({
                url: "/app/user/getImgCode",
                type: "POST",
                dataType: "JSON",
                data: {mobile:mobileNum},
                success: function (r) {
                    console.log(r);
                    // 显示弹窗
                    $(".registerMask").removeClass("hide");
                    $(".imgVftCodeWrap").removeClass("hide");
                    $("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+r.data);
                    $("#imgVftCodeClose").click(function(){ // 关闭弹窗
                        $(".registerMask").addClass("hide");
                        $(".imgVftCodeWrap").addClass("hide");
                    })
                },
                error: function () {
                    requestMsg("请求失败")
                }
            });
        } else{
            requestMsg("请填写正确的手机号");
        }
    });

    // 刷新重新获取图片验证
    $("#imgVftCodeRefresh").click(function(){
        var mobileNum = $("#register_mobile").val();
        $.ajax({
            url: "/app/user/getImgCode",
            type: "POST",
            dataType: "JSON",
            data: {mobile:mobileNum},
            success: function (r) {
                console.log(r);
                // 显示弹窗
                $(".registerMask").removeClass("hide");
                $(".imgVftCodeWrap").removeClass("hide");
                $("#imgVftCodeWrapImg").attr("src","data:image/png;base64,"+r.data);
                $("#imgVftCodeClose").click(function(){ // 关闭弹窗
                    $(".registerMask").addClass("hide");
                    $(".imgVftCodeWrap").addClass("hide");
                })
            },
            error: function () {
                requestMsg("请求失败")
            }
        });
    });

    // 获取验证码
	$("#imgVftCodeSbumit").click(function(){
		var isState = $(this).attr("isState");
		var mobileNum = $("#register_mobile").val();
		var verifyImgCode=$("#imgVftCode").val();

		if ( !isNaN(mobileNum) && (/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum)) ){  // 验证码不能为空、判断电话开头
			$("#register_codeBtn").attr("disabled",true);
			$.ajax({
      			url: "/app/user/getRegisterSmsCode",
      			type: "POST",
      			dataType: "JSON",
      			data: {
      				mobile: mobileNum,
      				token: token,
					channelCode: iphone8,
					pointCode: iphone8,
                    verifyImgCode:verifyImgCode
      			},
      			success: function(returnData){
      				if (returnData.success) {
      				    // 关闭弹窗
                        $(".registerMask").addClass("hide");
                        $(".imgVftCodeWrap").addClass("hide");
                        // 倒计时
                        $("#register_codeBtn").attr("isState",1);
    					$("#register_codeBtn").text(timerS+" s");
               	        timerInterval = setInterval(timeFunction,1000);
      				} else {
      					requestMsg(returnData.msg);
      					$("#register_codeBtn").removeAttr("disabled");
      				}
      			},
      			error: function(){
    	      	    requestMsg("网络跑丢了，请稍候重试");
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
        var pwdReg=/^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/;
        var password = pwdReg.test(register_password);

        var mobileNum = $("#register_mobile").val();
        var register_verification = $("#register_verification").val();
		var channelCode = $("#channelCode").val();
		var pointCode = $("#pointCode").val();

		var isState = $("#register_codeBtn").attr("isState");

		if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum) && mobileNum != "" ){ // 判断电话开头
			if ( register_verification != "" ) { // 验证码不能为空
				if ( password && 6 <= passwordLength && passwordLength <= 18 ) { // 密码6-18位
					if ($("#input_check").is(":checked")) { // 判断当前是否选中
						if ( $("#register_codeBtn").attr("isState")==1 ) {
							$.ajax({ // 设置登录密码
								url: "/app/user/commitChannelRegister",
								type: 'POST',
								dataType: 'JSON',
								data: {
									registerMobile: mobileNum,
									smsCode: register_verification,
									password: password_md5,
									channelCode: iphone8,
									pointCode: iphone8,
									token:token
								},
								success: function(returnData){
                                    if (returnData.success) {

                                        $("#register_submitBtn").attr("disabled",true);
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
							requestMsg("请获取验证码");
						}
					} else {
						requestMsg("请阅读并同意《51返呗用户注册协议》");
					}
				}else{
					requestMsg("请填写6-18位的数字、字母、字符组成的密码");
				}
			} else {
				requestMsg("请输入正确的验证码");
			}
		} else{
            requestMsg("请填写正确的手机号");
        };
    });
});
