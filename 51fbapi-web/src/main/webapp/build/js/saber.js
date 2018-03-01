$(function(){
    let tokenVal=formatDate()+Math.random().toString(36).substr(2);
    let domain = window.location.hostname;
    let aa;
    let bb;
    let _saber;
    if(domain=='app.51fanbei.com'){
        (function () {
            _saber = {
                partnerId: 'fanbei',
                tokenKey: tokenVal
            };
            aa = document.createElement('script'); aa.async = true;
            aa.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'df.baiqishi.com/static/webdf/saber.js?t=' + (new Date().getTime()/3600000).toFixed(0);
            bb = document.getElementsByTagName('script')[0]; bb.parentNode.insertBefore(aa, bb);

        })();
    }else{
        (function () {
            _saber = {
                partnerId: 'fanbei',
                tokenKey: tokenVal
            };
            aa = document.createElement('script'); aa.async = true;
            aa.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'dfst.baiqishi.com/static/webdf/saber.js?t=' + (new Date().getTime()/3600000).toFixed(0);
            bb = document.getElementsByTagName('script')[0]; bb.parentNode.insertBefore(aa, bb);

        })();
    }

    // 防止风控被拒
    function formatDate() {
        let date = new Date();
        let y = date.getFullYear();
        let m = date.getMonth() + 1;
        m = m < 10 ? ('0' + m) : m;
        let d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        let h = date.getHours();
        let minute = date.getMinutes();
        let second = date.getSeconds();
        return y +  m +  d +h +minute+second;
    }
});