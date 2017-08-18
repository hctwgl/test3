/*
 * @Author: yoe
 * @Date:   2017-02-15 09:59:54
 * @Last Modified by:   Marte
 * @Last Modified time: 2017-06-08 11:22:16
 * @title:  公用的
 */


// 公用弹框内容
function requestMsg(msg) {
    layer.open({
        content: msg,
        skin: 'msg',
        time: 2
    });
}

// 获取当前页面的URL 对其带的参数进行处理
function getUrl(para) {
    var paraArr = location.search.substring(1).split("&");
    for (var i = 0; i < paraArr.length; i++) {
        if (para == paraArr[i].split('=')[0]) {
            return paraArr[i].split('=')[1];
        }
    }
    return '';
}

// 获取当前页面的URL 对其带的参数进行处理
function getInfo() {
    var url = decodeURIComponent(location.toString());
    var paraArr = url.toString().split("_appInfo=");

    if (paraArr.length > 1) {
        return eval('(' + paraArr[1] + ')');
    } else {
        return ''
    }

}

// 处理默认的保留二位小数
function toDecimal2(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return false;
    }
    var fM = Math.round(x * 100) / 100;
    var s = fM.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + 2) {
        s += '0';
    }
    return s;
}

// 判断ios系统还是android系统
function getBlatFrom() {
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    // 返回1是android系统
    if (isAndroid) {
        return 1;
    }
    // 返回2是ios系统
    if (isiOS) {
        return 2;
    }
    return 0;
}

// 时间戳转换
function formatDate(now) {
    now = new Date(parseInt(now));
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var date = now.getDate();
    // var hour=now.getHours();
    // var minute=now.getMinutes();
    // var second=now.getSeconds();
    // return "20"+year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second;
    return year + "-" + month + "-" + date;
}

// 获取cookie里面的参数
function getCookie(cookie_name) {
    var allcookies = document.cookie;
    var cookie_pos = allcookies.indexOf(cookie_name);   //索引的长度

    // 如果找到了索引，就代表cookie存在，
    // 反之，就说明不存在。
    if (cookie_pos != -1) {  // 把cookie_pos放在值的开始，只要给值加1即可。
        cookie_pos += cookie_name.length + 1;  // 这里容易出问题，所以请大家参考的时候自己好好研究一下
        var cookie_end = allcookies.indexOf(";", cookie_pos);

        if (cookie_end == -1) {
            cookie_end = allcookies.length;
        }

        var value = unescape(allcookies.substring(cookie_pos, cookie_end));  //这里就可以得到你想要的cookie的值了。。。
    }
    return value;
};

// 获取网站的域名
function domainName() {
    var protocol = window.location.protocol;
    var host = window.location.host;
    var domainName = protocol + '//' + host;
    return domainName;
}
//限制文字数量
function txtFix(str,len){
    var char_length = 0;
    if(str.length<=len){
        return str
    }else{
        for (var i = 0; i < str.length; i++){
            var son_str = str.charAt(i);
            encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
            if (char_length >= len){
                var sub_len = (char_length == len) ? i+1 : i;
                return str.substr(0, sub_len);
                break;
            }
        }
    }
}
//图片懒加载
window.lazy = (function(window, document, undefined) {

    'use strict';

    var store = [],
        offset,
        poll;

    var _inView = function(el) {
        var coords = el.getBoundingClientRect();
        return ((coords.top >= 0 && coords.left >= 0 && coords.top) <= ((window.innerHeight || document.documentElement.clientHeight) + parseInt(offset)));
    };

    var _pollImages = function() {
        for (var i = store.length; i--;) {
            var self = store[i];
            if (_inView(self)) {
                self.src = self.getAttribute('lazy-src');
                store.splice(i, 1);
            }
        }
    };

    var _throttle = function() {
        clearTimeout(poll);
        poll = setTimeout(_pollImages,0);
    };

    var init = function(of) {
        var nodes = document.querySelectorAll('[lazy-src]');
        offset = of || 0;
        for (var i = 0; i < nodes.length; i++) {
            store.push(nodes[i]);
        }
        _throttle();

        if (document.addEventListener) {
            window.addEventListener('scroll', _throttle, false);
        } else {
            window.attachEvent('onscroll', _throttle);
        }
    };

    return {
        init: init,
        render: _throttle
    };

})(window, document);


// 隐藏电话号码的中间四位
function formateTelNum(tel) {
    if (tel) {
        var telLength = tel.length;
        if (telLength < 11) {
            tel = tel;
        } else if (telLength >= 11) {
            var telNum = tel.substring(4, 7);
            tel = tel.replace(telNum, "****");
        }
        return tel;
    } else {
        return "";
    }
}

