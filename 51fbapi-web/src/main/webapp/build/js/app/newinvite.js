
window.onload = ()=>{
  $('.copycode').on('click', ()=>{
    const clipboard = new Clipboard('.invitecode')    
    clipboard.on('success', function(e) {
      alert('已复制，可粘贴')
    })
  })

  $('.rightown').on('click', ()=>{
    window.location.href='/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"引爆年中抓娃娃，100％中大奖","shareAppContent":"抓娃娃次数无上限100％中奖，集齐5娃，平分1亿大奖，最高888红包雨在等你，有且只在51返呗！","shareAppImage":"http://f.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"http://f.51fanbei.com/fanbei-web/activity/gameShare","isSubmit":"Y","sharePage":"gameShare"})'
  })
}

function maidian(scene) {
  $.ajax({
    url: '/fanbei-web/postMaidianInfo',
    data: {
      'maidianInfo': 'sharewith'+ scene
    },
    type: 'POST',
    succuess: (data) => {
      console.log(data)
    }
  })
}

window.postshareex = (incase)=>{
  maidian(incase)
}

window.postshareaf = (incase)=>{
  // maidian(incase)
}