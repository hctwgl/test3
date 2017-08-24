/*
* @Date:   2017-04-12 10:53:28
*/

var token=formatDateTime()+Math.random().toString(36).substr(2);

var style=$("#style").val();  // 样式
var os=getBlatFrom(); // 1是android，2是ios
console.log(os);

// 更换页面title
if( style==21 ){
    document.title="借款超人注册";

    $("#borrowSuperman").click(function(){  // 更换已登陆链接
        if ( os==1 ){
            window.location.href="http://f.51fanbei.com/online/jiekuancaoren_v3.7.1.apk";
        }else if( os==2 ){
            window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%85%8D%E6%81%AF%E5%B0%8F%E9%A2%9D%E5%80%9F%E8%B4%B7%E6%89%8B%E6%9C%BA%E8%BD%AF%E4%BB%B6/id1263792729?mt=8";
        }
	})
}else if( style==22 ){
    document.title="借钱平台注册";
    $("#BrwPlatform").click(function(){  // 更换已登陆链接
        if ( os==1 ){
            requestMsg("您好~借钱平台安卓端正在加急上线中，马上就能与您相见啦。请耐心等待");
        }else if( os==2 ){
            window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E9%92%B1%E5%B9%B3%E5%8F%B0-%E5%B0%8F%E9%A2%9D%E6%9E%81%E9%80%9F%E7%8E%B0%E9%87%91%E5%80%9F%E8%B4%B7/id1259127316?mt=8";
        }
    })
}


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

// 检测流量
(function (root) {
    root._tt_config = true;
    var ta = document.createElement('script');
    ta.type = 'text/javascript';
    ta.async = true;
    ta.src = document.location.protocol+'//'+'s3.pstatp.com/bytecom/resource/track_log/src/toutiao-track-log.js';
    ta.onerror = function () {
        var request = new XMLHttpRequest();
        var web_url = window.encodeURIComponent(window.location.href);
        var js_url = ta.src;

        if ( style==16 ) { // 钜美头条访问量
            var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url=' + web_url + '&js_url=' + js_url + '&convert_id=63736236689';
        } else if ( style==14 ) {
            var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url='+web_url+'&js_url='+js_url+'&convert_id=62421367574';
        } else {
            var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url='+web_url+'&js_url='+js_url;
        }
        request.open('GET', url, true);
        request.send(null);
    }
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(ta, s);
})(window);


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
		var channelCode = $("#channelCode").val();
		var pointCode = $("#pointCode").val();
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
					channelCode: channelCode,
					pointCode: pointCode,
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
                            // 检测访问量
                            if ( style==16 ) { // 钜美头条注册量
                                _taq.push({convert_id:"63736236689", event_type:"form"})
                            } else if ( style==14 ) {
                                _taq.push({convert_id:"62421367574", event_type:"form"});
                            } else {
                                _taq.push({convert_id:"59212981134", event_type:"form"});  // 其他统计
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
                                    if (returnData.success) {
                                        if ( style==10 || style==12 || style==15 || style==16 ) {
                                            $("#register_submitBtn").attr("disabled",true);

                                            $(".registerSuss8").removeClass("hide");  // 显示样式8
                                            $(".registerSuss12").removeClass("hide");  // 显示样式10

                                            $(".registerMask").removeClass("hide");  // 显示遮罩

                                            $("#downloadApp").click(function(){  // 点击下载app
                                                window.location.href = returnData.url;
                                            });
                                        } else if( style==20 ){
											$(".registerSuss8").removeClass("hide");  // 显示样式8
											$(".registerMask").removeClass("hide");  // 显示遮罩
											$("#downloadApp").click(function(){  // 点击下载app
												if(os==1) {
													window.location.href = 'http://fusion.qq.com/cgi-bin/qzapps/unified_jump?appid=42318693&from=mqq&actionFlag=0&params=pname%3Dcom.alfl.www%26versioncode%3D373%26channelid%3D%26actionflag%3D0';
												}else{
													window.location.href = 'https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?mt=8&submit=edit&term=%E5%88%86%E6%9C%9F%E8%B4%B7#software';
                                                    window.location.href = 'https://itunes.apple.com/us/app/51%E8%BF%94%E5%91%97/id1136587444?mt=8';
												}
											});
										} else if( style==21 ){
                                            if ( os==1 ){
                                                window.location.href="http://f.51fanbei.com/online/jiekuancaoren_v3.7.1.apk";
                                            }else if( os==2 ){
                                                window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E6%AC%BE%E8%B6%85%E4%BA%BA-%E5%85%8D%E6%81%AF%E5%B0%8F%E9%A2%9D%E5%80%9F%E8%B4%B7%E6%89%8B%E6%9C%BA%E8%BD%AF%E4%BB%B6/id1263792729?mt=8";
                                            }
										} else if( style==22 ){
                                            if ( os==1 ){
                                                requestMsg("您好~借钱平台安卓端正在加急上线中，马上就能与您相见啦。请耐心等待");
                                            }else if( os==2 ){
                                                window.location.href="https://itunes.apple.com/cn/app/%E5%80%9F%E9%92%B1%E5%B9%B3%E5%8F%B0-%E5%B0%8F%E9%A2%9D%E6%9E%81%E9%80%9F%E7%8E%B0%E9%87%91%E5%80%9F%E8%B4%B7/id1259127316?mt=8";
                                            }
                                        } else {
                                            $("#register_submitBtn").attr("disabled",true);
                                            window.location.href = returnData.url;
                                        }
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
