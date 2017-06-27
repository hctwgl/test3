

var token = formatDateTime()+Math.random().toString(36).substr(2);

// var style = $("#style").val();

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
    var url = '//ad.toutiao.com/link_monitor/cdn_failed?web_url='+web_url+'&js_url='+js_url;
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
      // $("#register_codeBtn").attr("isState",0);
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
          mobile: mobileNum,
          token: token
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

    var isState = $("#register_codeBtn").attr("isState");

    if(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum) && mobileNum != "" ){ // 判断电话开头
      if ( register_verification != "" ) { // 验证码不能为空
        if ( password && 6 <= passwordLength <= 18 ) { // 密码6-18位
          if ($("#input_check").is(":checked")) { // 判断当前是否选中
            if ( $("#register_codeBtn").attr("isState") == 1 ) {
              // 检测访问量
              _taq.push({convert_id:"59212981134", event_type:"form"})  // 其他统计

              $.ajax({ // 设置登录密码
                url: "/app/user/commitChannelRegister",
                type: 'POST',
                dataType: 'JSON',
                data: {
                  registerMobile: mobileNum,
                  smsCode: register_verification,
                  password: password_md5,
                  channelCode: 'Xpyq1',
                  pointCode: 'Xpyq5',
                  token: token
                },
                success: function(returnData){
                  if ( returnData.success ) {
                    $("#register_submitBtn").attr("disabled",true);
                    // window.location.href = returnData.url;
                    requestMsg("恭喜您，注册成功，赶紧下载51返呗App来体验吧");
                    setTimeout(function(){
                      document.body.scrollTop=0;
                      $(".mask").removeClass('hide');
                      $(".downloadQRcode").removeClass('hide');
                    }, 2000);
                  } else {
                    requestMsg(returnData.msg);
                  }
                },
                error: function(){
                  requestMsg("注册失败");
                }
              })
            } else {
              requestMsg("请获取验证码11111");
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

  // 关闭遮罩
  $(".mask").click(function(event) {
    window.location.reload();
    $(".mask").addClass('hide');
    $(".downloadQRcode").addClass('hide');
  });

});
