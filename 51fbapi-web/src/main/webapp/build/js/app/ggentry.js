
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
      url: '/fanbei-web/pickBoluomeCouponV1',
      dataType: "JSON",
      data: {
        'sceneId': '9022',
      },
      type: 'POST',
      success: function (data) {
        data = eval('(' + data + ')');
        console.log(data)
        if (data.success) {
          requestMsg(data.msg)
        } else {
          if (data.url) {
            location.href = data.url;
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
      url: '/fanbei-web/getBrandUrlV1',
      data: {
        shopId: getShopId(scase),
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

