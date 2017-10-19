function getinfo(fn) {
  $.ajax({
    url: '/fanbei-web/activityUserInfo',
    data: {
      'userId': 178
    },
    type: 'POST',
    succuess: (data) => {
      data=eval('(' + data + ')')

      fn(data[0])
    },
    error: (err) => {
      requestMsg(err)
    }
  }) 
}

function _init() {
  $('.rule').on('click', function() {
    $('._global_mask').show()
    $('.rulewindow').show()
  })

  $('.rulewindow .close').on('click', function() {
    $('._global_mask').hide()
    $('.rulewindow').hide()
  })

  $('.buttons div').on('click', function() {
    $('.buttons div').removeClass('active')
    $(this).addClass('active')
  })
}



window.onload = ()=>{
  _init()
  let clipboard = new Clipboard('.invitecode')   

  clipboard.on('success', function(e) {
    console.log(e)
  })

  $('.copycode').on('click', ()=>{
    alert('已复制到剪贴板，可粘贴')
  })

  $('.rightown').on('click', ()=>{
    window.location.href='/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"引爆年中抓娃娃，100％中大奖","shareAppContent":"抓娃娃次数无上限100％中奖，集齐5娃，平分1亿大奖，最高888红包雨在等你，有且只在51返呗！","shareAppImage":"http://f.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"http://f.51fanbei.com/fanbei-web/activity/gameShare","isSubmit":"Y","sharePage":"gameShare"})'
  })

  function exec(data) {
    let rule = data.listRule
    let invitationCode = data.invitationCode
    let sum = data.sumPrizeMoney
    let rulehtml = rule.map((item, i)=>{
      return `<p>${i+1}.${item}item</p>`
    })

    $('.rulewindow .content').append(rulehtml.join(''))
    $('.invitecode').text(invitationCode)
    $('.invitecode')[0].dataset.clipboardText = invitationCode
    $('.myreward span').text(sum)
  }

  getinfo(exec)


  function share() {
    window.location.href = '/fanbei-web/opennative?name=APP_SHARE&params={"shareAppTitle":"引爆年中抓娃娃，100％中大奖","shareAppContent":"抓娃娃次数无上限100％中奖，集齐5娃，平分1亿大奖，最高888红包雨在等你，有且只在51返呗！","shareAppImage":"http://f.51fanbei.com/h5/common/icon/midyearCorner.png","shareAppUrl":"http://f.51fanbei.com/fanbei-web/activity/gameShare","isSubmit":"Y","sharePage":"gameShare"}'
  }



  let levelonepage = 1
  let leveltwopage = 1
  let off_on = true

  function returnlist(arr) {
    let newarr = arr.map((item, i)=>{
      return `<div class="item"><div>${arr.a}</div><div>${arr.a}</div><div>${arr.a}</div><div>${arr.a}</div></div>`
    })
  }


  function getlist(page, type, fn) {
    $.ajax({
      url: '/fanbei-web/activityUserInfo',
      data: {
        'userId': 178,
        'type': type,
        'pageSize': 5,
        'currentPage': page
      },
      type: 'POST',
      succuess: (data) => {
        data=eval('(' + data + ')')
  
        fn(data[0], type)
      },
      error: (err) => {
        requestMsg(err)
      }
    }) 
  }

  function replacehtml(data, type) {
    let list = data.rewardQueryList
    let html = returnlist(list)
    $('.list').html(html)
    
    if(type == 1) {
      levelonepage = 1
    } else {
      leveltwopage = 1
    }

    $('.list').html(html)
    off_on = true
  }

  function appendhtml(data, type) {
    let list = data.rewardQueryList
    let html = returnlist(list)

    if(type == 1) {
      levelonepage = 1
    } else {
      leveltwopage = 1
    }

    $('.list').append(html)
    off_on = true
  }

  $('.buttons div').on('click', (e)=>{
    var nodecl = e.target.className
    if(nodecl.indexOf('levelone') > -1) {
      getlist(1, 1, replacehtml)
    } else {
      getlist(1, 2, replacehtml)
    }
  })



  $('.con').scroll(function() {
    if (($(this)[0].scrollTop + $(this).height() + 40) >= $(this)[0].scrollHeight) {
      if (off_on) {
          off_on = false;
          if($('.levelone').hasClass('active')) {
            getlist(1, 1, appendhtml)
          } else {
            getlist(1, 2, appendhtml)
          }
      }
    }
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