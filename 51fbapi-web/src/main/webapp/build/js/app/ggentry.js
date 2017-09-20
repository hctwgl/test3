
/**
 * 商城id
 */

let getShopId = (shop)=>{
  switch(shop) {
    case 'ele': 
      return 10
    case 'yiguo':
      return 2
    case 'lumama':
      return 4
    case 'xiecheng': 
      return 16
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
      if(data.data.boluomeCouponList[0].isHas == 'Y') {
        $('.coupon .button').addClass('grey')
      }

      $('.coupon .button.unpending')
      .click(function () {
        $('.coupon .button.unpending').removeClass('unpending')
        $.ajax({
          url: '/fanbei-web/pickBoluomeCouponV1',
          data: {
            'sceneId': sceneId,
          },
          type: 'POST',
          success: function (data) {
            $('.coupon .button').addClass('unpending')
            data=eval('(' + data + ')')
            if (data.success) {
              requestMsg(data.msg)
              $.ajax({
                url: '/H5GG/showCoupon',
                type: 'GET',
                success: (data) => {
                  data=eval('(' + data + ')')
            
                  sceneId = data.data.boluomeCouponList[0].sceneId
                  if(data.data.boluomeCouponList[0].isHas == 'Y') {
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
          },
          error: function(err) {
            console.log(err)
            $('.coupon .button').addClass('unpending')
          }
        });
      });

    }
  })

}


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

