let userName = "";
if (getInfo().userName) {
  userName = getInfo().userName;
};


/**
 * 商城id
 */

let getShopId = (shop)=>{
  switch(shop) {
    case 'ele': 
      return 22
    case 'yiguo':
      return 17
    case 'lumama':
      return 24
    case 'xiecheng': 
      return 33
    default: 
      return null
  }
}

window.onload = () => {
  $.ajax({
    url: '/fanbei-web/postMaidianInfo',
    data: {
      'maidianInfo': 'ggentry'
    },
    type: 'POST',
    succuess: (data) => {
      console.log(data)
    }
  })
}

$('.coupon .button')
  .click(function () {
    $.ajax({
      url: '/fanbei-web/pickBoluomeCoupon',
      data: {
        'sceneId': '9022',
        'userName': userName
      },
      type: 'POST',
      success: function (data) {
        data = eval('(' + data + ')');
        console.log(data)
        if (data.success) {
          requestMsg("领劵成功")
        } else {
          if (data.url) {
            if (getBlatFrom() == 2) {
              location.href = data.url;
            } else {
              requestMsg("请退出当前活动页面,登录后再进行领劵");
            }
          } else {
            requestMsg(data.msg);
          }
        }
      }
    });
  });

/**
 * 引流
 */

let drainage = (scase)=>{
  $('.' + scase + ' .button').on('click', ()=>{
    $.ajax({
      url: '/fanbei-web/getBrandUrl',
      data: {
        shop: getShopId(scase),
        userName: userName,
      },
      type: 'POST',
      success: (data)=>{
        data=eval('(' + data + ')');
        if(data.success){
           location.href=data.url;
        }else{
           requestMsg(data.msg);
        }
      }
    })

    $.ajax({
      url: '/fanbei-web/postMaidianInfo',
      data: {
        'maidianInfo': getShopId(scase)
      },
      type: 'POST',
      succuess: (data) => {
        console.log(data)
      }
    })
  })
}

drainage('ele')
drainage('yiguo')
drainage('lumama')
drainage('xiecheng')



