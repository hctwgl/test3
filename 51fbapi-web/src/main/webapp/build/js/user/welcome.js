/**
 * Created by nizhiwei-labtop on 2017/9/27.
 */
new Vue({
    el:'#wrap',
    data:{
        type:getUrl('isNew')||'1',
    },
    methods:{
        title:function(){
            if(this.type=='1'){
                return '恭喜你！注册成功！'
            }else{
                return '您已注册，打开借钱吧！'
            }
        },
        btn:function () {
            if(this.type=='1'){
                return '下载App'
            }else{
                return '打开App'
            }
        },
        cli:function () {
            let ifrSrc = 'com.91ala.www://home://';
            if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
                ifrSrc = 'com.91ala.www://home://';
            }else if (navigator.userAgent.match(/android/i)){
                ifrSrc = 'myapp://jp.app/openwith??isBrowser=1';
            }
            let ifr = document.createElement('iframe');
            ifr.src = ifrSrc;
            ifr.style.display = 'none';
            // var isInstalled;
            // ifr.onload = function() {
            //     isInstalled = true;
            //     alert(isInstalled);
            //     document.getElementById('openApp1').click();};
            // ifr.onerror = function() {
            //     // alert('May be not installed.');
            //     isInstalled = false;
            //     alert(isInstalled);
            // };
            document.body.appendChild(ifr);
            setTimeout(function() {
                document.body.removeChild(ifr);
            },1000);
                window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
        }
    }
})
