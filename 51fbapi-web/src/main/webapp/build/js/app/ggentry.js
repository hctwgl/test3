
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

let sceneId = null

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

  $.ajax({
    url: '/H5GG/showCoupon',
    type: 'GET',
    success: (data) => {
      data=eval('(' + data + ')')

      sceneId = data.data.boluomeCouponList[0].sceneId
      if(data.data.boluomeCouponList[0].isHas) {
        $('.coupon .button').addClass('grey')
      }

    }
  })

}

$('.coupon .button')
  .click(function () {
    $.ajax({
      url: '/fanbei-web/pickBoluomeCouponV1',
      data: {
        'sceneId': sceneId,
      },
      type: 'POST',
      success: function (data) {
        requestMsg(data)
        data=eval('(' + data + ')');
        console.log(data)
        if (data.success) {
          requestMsg(data.msg)
          $.ajax({
            url: '/H5GG/showCoupon',
            type: 'GET',
            success: (data) => {
              data=eval('(' + data + ')')
        
              sceneId = data.data.boluomeCouponList[0].sceneId
              if(data.data.boluomeCouponList[0].isHas) {
                $('.coupon .button').addClass('grey')
              }
        
            }
          })
        } else {
          if (data.url) {
            location.href = data.url;
          } else {
            requestMsg(data.msg)
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
        shop: getShopId(scase),
      },
      type: 'POST',
      success: (data)=>{
        data=eval('(' + data + ')');
        if(data.success){
           location.href=data.url;
        }else{
           location.href=data.url;
        }
      }
    })

    $.ajax({
      url: '/fanbei-web/postMaidianInfo',
      data: {
        'maidianInfo': 'ggentry' + getShopId(scase)
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

