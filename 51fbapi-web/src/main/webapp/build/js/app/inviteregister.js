const users = [
  {
    phone: '13120808880',
    money: '8000'
  }, {
    phone: '13777739767',
    money: '6000'
  }, {
    phone: '18657172099',
    money: '7000'
  }, {
    phone: '15869125132',
    money: '6000'
  }, {
    phone: '17826833863',
    money: '4000'
  }, {
    phone: '17623000389',
    money: '5000'
  }, {
    phone: '15769163696',
    money: '4000'
  }, {
    phone: '18367127197',
    money: '3000'
  }, {
    phone: '13958114326',
    money: '5000'
  }, {
    phone: '13758140925',
    money: '4000'
  }, {
    phone: '13588865276',
    money: '3000'
  }, {
    phone: '15757161549',
    money: '7000'
  }, {
    phone: '18768110576',
    money: '6000'
  }, {
    phone: '13216178996',
    money: '4000'
  }, {
    phone: '18768110576',
    money: '5000'
  }
]

const recommendCode = getUrl("recommendCode");

// 防止风控被拒
function formatDateTime() {
  var date = new Date();
  var y = date.getFullYear();
  var m = date.getMonth() + 1;
  m = m < 10
    ? ('0' + m)
    : m;
  var d = date.getDate();
  d = d < 10
    ? ('0' + d)
    : d;
  var h = date.getHours();
  var minute = date.getMinutes();
  var second = date.getSeconds();
  return y + m + d + h + minute + second;
};
var token = formatDateTime() + Math.random().toString(36).substr(2);

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


/**
 * 表单验证
 */
let checkphone = () => {
  let phone = $('#user').val()
  const reg = /^1(3|4|5|7|8)\d{9}$/i
  if (reg.test(phone)) {
    return true
  } else {
    return false
  }
}

let checkpwd = () => {
  let pwd = $('#pwd').val()
  const reg = /^((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[.!@#$%])|(?=.*?[A-Za-z])(?=.*?[.]))[\dA-Za-z.!@#$%]+$/
  if (reg.test(pwd)) {
    return true
  } else {
    return false
  }
}

let checkverify = () => {
  if ($('#verify').val().length !== 6) {
    return false
  } else {
    return true
  }
}

let checkcheckbox = () => {
  if ($('.checkbox').hasClass('checked')) {
    return true
  } else {
    return false
  }
}

/**
 * 验证码倒计时
 */
let sixty = () => {
  let time = 60
  let timer = setInterval(() => {
    $('.getcode').html(`${time}S`)
    time--
  }, 1000)
  setTimeout(() => {
    clearInterval(timer)
    $(".getcode").removeAttr("disabled");
    $('.getcode')
      .removeClass('disabled')
      .text('获取验证码')
  }, 61000)
}

/**
 * 动态生成列表
 * @param {*} arr
 */
let insertroll = (arr) => {
  let docfrag = ''
  arr.forEach((each) => {
    let exarr = each
      .phone
      .split('')
    exarr.splice(3, 4, '****')
    docfrag += `<li>用户 ${exarr.join('')} &nbsp;成功借款 ${each.money}元</li>`
  })
  return docfrag
}

let timescroll = (arr) => {
  const length = arr.length
  setInterval(() => {
    $('.roll ul').animate({top: '-=20px'})
  }, 2000)
  setInterval(() => {
    $('.roll ul').css({top: '20px'})
  }, 2000 * (length) - 1)
}

window.onload = () => {
  $('.roll ul').append(insertroll(users))
  timescroll(users)





  var timerS = 60;
  //第三方图片验证
  $.ajax({
    url: "/fanbei-web/getGeetestCode",
    type: "get",
    dataType: "json",
    success: function (data) {
      initGeetest({
        gt: data.gt,
        challenge: data.challenge,
        new_captcha: data.new_captcha, // 用于宕机时表示是新验证码的宕机
        offline: !data.success, // 表示用户后台检测极验服务器是否宕机，一般不需要关注
        product: "bind", // 产品形式，包括：float，popup
      }, function (captchaObj) {
        document.getElementById('checkbtn').addEventListener('click', function () {
          var mobileNum = $("#user").val();
          if (!(/^1(3|4|5|7|8)\d{9}$/i.test(mobileNum))) { // 验证码不能为空、判断电话开头
            requestMsg('请输入手机号')
          } else {
            $.ajax({
              url: '/app/user/checkMobileRegistered',
              type: 'post',
              data: {
                mobile: mobileNum
              },
              success: function (data) {
                $('.getcode').removeClass('pending');
                data = JSON.parse(data);
                if (data.data == 'N') {
                  captchaObj.verify(); //调起图片验证
                  maidianFn("getCodeSuccess");
                } else {
                  maidianFn("getCodeRegistered");
                  if (data.msg == "用户已存在") {
                    requestMsg("您已注册，请直接登录")
                  } else {
                    requestMsg(data.msg)
                  }
                }
              }
            })
          }
        });
        captchaObj.onSuccess(function () {
          var result = captchaObj.getValidate();
          $.ajax({
            url: '/fanbei-web/verifyGeetestCode',
            type: 'POST',
            dataType: 'json',
            data: {
              userId: data.userId,
              geetestChallenge: result.geetest_challenge,
              geetestValidate: result.geetest_validate,
              geetestSeccode: result.geetest_seccode
            },
            success: function (data) {
              if (data.data.status === 'success') {
                maidianFn("sendCodeSuccess");
                getCode();
              } else if (data.data.status === 'fail') {
                maidianFn("sendCodeFail");
                requestMsg(data.msg);
              }
            }
          })
        });
      });
    }
  });

  maidianFn('inviteRegister');

  function getCode() {
    if (checkphone()) {
      var mobileNum = $("#user").val();
      $(".getcode").attr("disabled", true);
      /* $.ajax({
        url: "/app/user/getImgCode",
        type: "POST",
        dataType: "JSON",
        data: {
          mobile: $('#user').val()
        },
        success: function (r) {
          $('.getcode').removeClass('pending')
          // 显示弹窗
          $(".registerMask").removeClass("hide")
          $(".imgVftCodeWrap").removeClass("hide")
          $("#imgVftCodeWrapImg").attr("src", "data:image/png;base64," + r.data);
          $('.imgVftCodeRefresh').on('click', () => {
            $.ajax({
              url: "/app/user/getImgCode",
              type: "POST",
              dataType: "JSON",
              data: {
                mobile: $('#user').val()
              },
              success: function (r) {
                $("#imgVftCodeWrapImg").attr("src", "data:image/png;base64," + r.data);
              },
              error: function () {
                requestMsg("请求失败")
              }
            });
          })
          $('.imgVftCodeSbumit').on('click', () => {
            $.ajax({
              url: "/app/user/getRegisterSmsCode",
              type: "POST",
              dataType: "JSON",
              data: {
                mobile: $('#user').val(),
                token: token,
                verifyImgCode: $("#imgVftCode").val()
              },
              success: function (returnData) {
                if (returnData.success) {
                  // 关闭弹窗
                  $(".registerMask").addClass("hide");
                  $(".imgVftCodeWrap").addClass("hide");
                  // 倒计时
                  $('.getcode').addClass('disabled')
                  sixty()
                } else {
                  requestMsg(returnData.msg);
                  $(".getcode").removeClass("disabled");
                }
              },
              error: function () {
                requestMsg("请求失败");
              }
            })
          })
          $("#imgVftCodeClose").click(function () { // 关闭弹窗
            $(".registerMask").addClass("hide")
            $(".imgVftCodeWrap").addClass("hide")
          })
        },
        error: function () {
          $('.getcode').removeClass('pending')
          requestMsg("请求失败")
        }
      }); */
      $.ajax({
        url: "/app/user/getRegisterSmsCode4Geetest",
        type: "POST",
        dataType: "json",
        data: {
          "mobile": mobileNum, //将手机号码传给后台
          token: token
        },
        success: function (returnData) {
          if (returnData.success) {
            // 倒计时
            $(".checkbtn").attr("isState", 1);
            $(".checkbtn").text(timerS + " s");
            // $(".checkbtn").addClass("gray");
            // timerInterval = setInterval(timeFunction, 1000);
            sixty()
            requestMsg("验证码已发送");
          } else {
            requestMsg(returnData.msg);
            $(".getcode").removeAttr("disabled");
          }
        },
        error: function () {
          requestMsg("请求失败");
        }
      })
    } else if (!checkphone()) {
      requestMsg("请填写正确的手机号")
    }
  }


  /* $('.getcode').on('click', () => {
    if (checkphone() && !$('.getcode').hasClass('disabled') && !$('.getcode').hasClass('pending')) {
      $('.getcode').addClass('pending')
      $.ajax({
        url: "/app/user/getImgCode",
        type: "POST",
        dataType: "JSON",
        data: {
          mobile: $('#user').val()
        },
        success: function (r) {
          $('.getcode').removeClass('pending')
          // 显示弹窗
          $(".registerMask").removeClass("hide")
          $(".imgVftCodeWrap").removeClass("hide")
          $("#imgVftCodeWrapImg").attr("src", "data:image/png;base64," + r.data);
          $('.imgVftCodeRefresh').on('click', () => {
            $.ajax({
              url: "/app/user/getImgCode",
              type: "POST",
              dataType: "JSON",
              data: {
                mobile: $('#user').val()
              },
              success: function (r) {
                $("#imgVftCodeWrapImg").attr("src", "data:image/png;base64," + r.data);
              },
              error: function () {
                requestMsg("请求失败")
              }
            });
          })
          $('.imgVftCodeSbumit').on('click', () => {
            $.ajax({
              url: "/app/user/getRegisterSmsCode",
              type: "POST",
              dataType: "JSON",
              data: {
                mobile: $('#user').val(),
                token: token,
                verifyImgCode: $("#imgVftCode").val()
              },
              success: function (returnData) {
                if (returnData.success) {
                  // 关闭弹窗
                  $(".registerMask").addClass("hide");
                  $(".imgVftCodeWrap").addClass("hide");
                  // 倒计时
                  $('.getcode').addClass('disabled')
                  sixty()
                } else {
                  requestMsg(returnData.msg);
                  $(".getcode").removeClass("disabled");
                }
              },
              error: function () {
                requestMsg("请求失败");
              }
            })
          })
          $("#imgVftCodeClose").click(function () { // 关闭弹窗
            $(".registerMask").addClass("hide")
            $(".imgVftCodeWrap").addClass("hide")
          })
        },
        error: function () {
          $('.getcode').removeClass('pending')
          requestMsg("请求失败")
        }
      });
    } else if (!checkphone()) {
      requestMsg("请填写正确的手机号")
    }
  }) */

  $('.reg').on('click', () => {
    maidianFn('registerBtn')
    const boolphone = checkphone()
    const boolpwd = checkpwd()
    const boolverify = checkverify()
    const boolcheckbox = checkcheckbox()
    !boolphone ? requestMsg('请填写正确的手机号') : 
    !boolpwd ? requestMsg('请填写6-18位的数字、字母、字符组成的密码') :
    !boolverify ? requestMsg('请输入正确的验证码') : 
    !boolcheckbox ? requestMsg('请阅读并同意《51返呗用户注册协议》') : null
    if (boolphone && boolpwd && boolverify && boolcheckbox) {
      if(!$('.reg').hasClass('pending')) {
        $('.reg').addClass('pending')
        $.ajax({ 
          url: "/app/user/commitRegister",
          type: 'POST',
          dataType: 'JSON',
          data: {
            registerMobile: $('#user').val(),
            smsCode: $('#verify').val(),
            password: String(CryptoJS.MD5($("#pwd").val())),
            recommendCode: recommendCode,
            token: token
          },
          success: function (returnData) {
            maidianFn("registerSuccess");
            $('.reg').removeClass('pending')
            if (returnData.success) {
              window.location.href = returnData.url;
            } else {
              maidianFnNew("registerFail",returnData.msg);
              requestMsg(returnData.msg);
            }
          },
          error: function () {
            $('.reg').removeClass('pending')            
            requestMsg("注册失败");
          }
        }) 
      }
    }
  })

  $('.checkbox').on('click', ()=>{
    $('.checkbox').toggleClass('checked')
  })
}