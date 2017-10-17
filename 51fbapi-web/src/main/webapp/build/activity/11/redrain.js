function yes(el, status, text) {
  el.removeClass('on off notyet')
  el.addClass('off')
  el.find('.status').text('红包雨已结束')
}

function no(el, status, text) {
  el.removeClass('on off notyet')
  el.addClass('notyet')
  el.find('.status').text(text)
}

function not(el, status, text) {
  el.removeClass('on off notyet')
  el.addClass('on')
  el.find('.status').text(text)
}


let on = (el, status, text)=>{
  switch(status) {
    case 'yes': 
      yes(el, status, text)
      return
    case 'no': 
      no(el, status, text)
      return
    case 'not':
      not(el, status, text)
      return
    default: 
      return
  }
}

/**
 * 维护不同时间点的红包雨场次
 */
const firstrain = {
  el: $('.fir'),
  reset: '第一场红包雨',
  dot: 10,
  exec: on,
  status: null,
}

const secondrain = {
  el: $('.sec'),
  reset: '第二场红包雨',
  dot: 14,
  exec: on,
  status: null,
}

const thirdrain = {
  el: $('.thi'),
  reset: '第三场红包雨',
  dot: 20,
  exec: on,
  status: null,
}

/**
 * 一天的红包雨顺序
 */
let order = new Array(0)

order.push(firstrain, secondrain, thirdrain)


let checkstatus = (obj)=>{
  let dot = obj.dot || null
  let nowhour = localtime.getHours()
  let nowminute = localtime.getMinutes()
  let nowsecond = localtime.getSeconds()

  if(nowhour<dot || (nowhour ===dot && nowminute === 0 && nowsecond <= 20)) {
    return 'no'
  }

  return 'yes'
}

/**
 * 返回一个表达order状态的数组
 * @param {Array} order 
 */
let checkorder = (order)=>{
  let returnorder = new Array(0)


  order.forEach((item)=>{
    returnorder.push(checkstatus(item))
  })

  return returnorder
}

let adjuststatus = (status)=>{
  let length = 0
  let _status = status

  _status.forEach((a)=>{
    if(a === 'no') length ++ 
  })

  if(length > 0 ) {
    _status.splice(_status.indexOf('no'), 1 ,'not')
  } 

  return _status
} 

let execstatsu = (order, adjustedstatus)=>{
  order.forEach((item, i)=>{
    item.status = adjustedstatus[i]
    item.exec(item.el, item.status, item.reset)
  })
}

/**
 * 倒计时
 * @param {*} order 
 */
let counttime = (order)=>{
  let timedot = null
  let timestamp = null
  let nowhour = localtime.getHours()
  let nowminute = localtime.getMinutes()
  let nowsecond = localtime.getSeconds()

  order.forEach((item)=>{
    if(item.status === 'not') timedot = item.dot
  })


  if(timedot) {
    timestamp = ((60-nowsecond) + (59-nowminute) * 60 + (timedot-1-nowhour) * 3600) * 1000
  } else {
    timestamp = ((60-nowsecond) + (59-nowminute) * 60 + (10+23-nowhour) * 3600) * 1000
  }

  let timer = setInterval(()=>{
    let hour = new Date(timestamp).getHours() >= 8 ?  new Date(timestamp).getHours() - 8 : new Date(timestamp).getHours() + 16
    let minute = new Date(timestamp).getMinutes()
    let second = new Date(timestamp).getSeconds()

    $('.hour').text(hour)
    $('.minute').text(minute)
    $('.second').text(second)

    timestamp -= 1000
  }, 1000)

  setTimeout(()=>{
    clearInterval(timer)
    $('.hour').text('0')
    $('.minute').text('0')
    $('.second').text('0')


    setTimeout(()=>{
      window.localtime = new Date('2017/10/15 03:00:21')
      
      let lateststatus = adjuststatus(checkorder(order))
      
      execstatsu(order, lateststatus)
      
      counttime(order)
    },20000)
  }, timestamp)
}

window.onload = ()=>{
  //将当前时间的时间赋值给window.localtime,之后用后台请求数据替换
  window.localtime = new Date('2017/10/15 20:24:21')

  let lateststatus = adjuststatus(checkorder(order))

  execstatsu(order, lateststatus)

  counttime(order)
}