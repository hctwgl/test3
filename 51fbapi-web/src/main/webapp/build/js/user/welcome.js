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
            if(this.type=='down'){
                window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
            }else{
                var loadDateTime=new Date();
                if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
                    window.location.href = "com.91ala.www://home://";
                    window.setTimeout(function () {
                        let timeOutDateTime = new Date();
                        if (timeOutDateTime - loadDateTime < 5000 && location.href.indexOf('com.91ala.www://home//') == -1){
                            window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
                        } else {
                            window.close();
                        }
                    },2000);
                } else if (navigator.userAgent.match(/android/i)) {
                    window.location.href = "myapp://jp.app/openwith??isBrowser=1";
                    setTimeout(function(){
                        let timeOutDateTime = new Date();
                        if (timeOutDateTime - loadDateTime < 5000){
                            window.location = "http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
                        }
                    },2000);
                }
            }
        }
    }
})
