
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};

// 获取网站的域名
let domainName = domainName();

// app调用web的方法
function alaShareData(){
    // 分享内容
    let dataObj = {
        'appLogin': 'N', // 是否需要登录，Y需要，N不需要
        'type': 'share', // 此页面的类型
        'shareAppTitle': 'OPPO R11预约返利300福利',  // 分享的title
        'shareAppContent': 'OPPO R11全明星首发，疯陪到底！0元预约享12期分期免息，更有超级返利300福利！有，且只在51返呗 GO>>>',  // 分享的内容
        'shareAppImage': 'http://f.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
        'shareAppUrl': domainName+'/fanbei-web/activity/oppoR11?oppoR11Share=oppoR11Share',  // 分享后的链接
        'isSubmit': 'N', // 是否需要向后台提交数据，Y需要，N不需要
        'sharePage': 'oppoR11' // 分享的页面
    };
    let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
    return dataStr;
};

// 初始化页面
window.onload=function(){
    $.ajax({
        url: "/app/activity/reserveActivityInfo",
        type: "POST",
        dataType: "JSON",
        data: {userName: userName},
        success: function(returnData){
            if (returnData.data.isHaveReservationRecord == "Y") {
                $("#btn").attr('src', 'http://f.51fanbei.com/h5/app/activity/06/oppo2_2.png');
            }
        },
        error: function(){
            requestMsg("请求失败");
        }
    });
}

// 弹窗
$('.mask').click(function(){
    $('.mask').css('display','none');
    $('.orderSuccess').css('display','none');
    window.location.reload();
});
$('.close').click(function(){
    $('.mask').css('display','none');
    $('.orderSuccess').css('display','none');
    window.location.reload();
});

// 倒计时
$(function(){
    // 结束时间的时间戳
    let endDate = new Date("June 22,2017 10:00:00")
    let endStamp = endDate.valueOf();
    // 获取当前时间的时间戳
    let now = new Date();
    let nowTimeStamp = now.valueOf();
    // 相差的时间戳
    let differStamp = endStamp - nowTimeStamp;
    let intDiff = parseInt(differStamp/1000);//倒计时总秒数量

    function showTimerS( diff ){
        var day=0,
        hour=0,
        minute=0,
        second=0;//时间默认值

        if(diff > 0){
            day = Math.floor(diff / (60 * 60 * 24));
            hour = Math.floor(diff / (60 * 60)) - (day * 24);
            minute = Math.floor(diff / 60) - (day * 24 * 60) - (hour * 60);
            second = Math.floor(diff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
        }

        if (minute <= 9) minute = '0' + minute;
        if (second <= 9) second = '0' + second;
        $('#day_show').html(day+"天");
        $('#hour_show').html('<s id="h"></s>'+hour+'时');
        $('#minute_show').html('<s></s>'+minute+'分');
        $('#second_show').html('<s></s>'+second+'秒');
    };

    function timer(intDiff){
        showTimerS(intDiff);
        intDiff--;
        window.setInterval(function(){
            showTimerS(intDiff);
            intDiff--;
        }, 1000);
    };
    timer(intDiff);
});

// 点击手机弹窗
new Vue({
    el: '#oppoR11',
    data: {
        show: [true,false,false,false,],
        bannerlist: 'http://f.51fanbei.com/h5/app/activity/06/oppo12_31.png',
        url: 'https://app.51fanbei.com/fanbei-web/opennative?name=GOODS_DETAIL_INFO&params={"privateGoodsId":"120751"}',
        content: '¥2999 日供 ¥8.2起'
    },
    methods:{
        btnBox: function(){
            $.ajax({
                url: '/app/activity/reserveActivityGoods',
                dataType:'json',
                data:{'userName':userName},
                type: 'post',
                success:function (data) {
                    if(data.success){
                        $('.mask').css('display','block');
                        $('.orderSuccess').css('display','block');
                    }else{
                        if(data.url){
                            location.href=data.url;
                        }else{
                            requestMsg(data.msg);
                        }
                    }
                },
                error: function(){
                    requestMsg("请求失败");
                }
            });
        },
        mobilePopup: function(){ // 显示手机弹窗
            $('.popupBox').removeClass('hide');
            $('.popup').removeClass('hide');
        },
        close: function(){ // 关闭手机弹窗
            $('.popupBox').addClass('hide');
            $('.popup').addClass('hide');
        },
        oppoR11List(e){
            for(let i=0;i<this.show.length;i++){
                this.show.splice(i, 1, false);  // 其他的为false
            }
            this.show.splice(e-1, 1, true);  // 点击当前的为true

            // 手机的bannerlist
            this.bannerlist="http://f.51fanbei.com/h5/app/activity/06/oppo12_3"+e+".png";

            // 手机的privateGoodsId
            let privateGoodsId=[120751,120790,120791,120792];
            let notifyUrl = "https://app.51fanbei.com/fanbei-web/opennative?name=GOODS_DETAIL_INFO";
            this.url=notifyUrl+'&params={"privateGoodsId":"'+privateGoodsId[e-1]+'"}';  // a链接的url

            // 手机的分期的文案
            let content=['¥2999 日供 ¥8.2起','¥2999 日供 ¥8.2起','¥2999 日供 ¥8.2起','¥3199 日供 ¥8.7起'];
            this.content=content[e-1];
        }
    }
});

// mobileList手机列表
function addMobileListModel(goodsList,notifyUrl) {

    let html = '';
        for (let i = 0; i < goodsList.length; i++) {
        // let privateGoodsId = notifyUrl + '&params={"privateGoodsId":"'+goodsList[i].goodsId+'"}';
        let goodsIcon = goodsList[i].goodsIcon;
        let goodsName = goodsList[i].goodsName;
        let saleAmount = goodsList[i].saleAmount;
        let remark = goodsList[i].remark;
        let numId = goodsList[i].numId;

        // 判断android和ios的版本 给出相应的链接
        let privateGoodsId='';
        if (getBlatFrom()==2){
            var ver=getInfo().appVersion;
            if( ver&&ver < 365 ){
                privateGoodsId = notifyUrl + '&params={"goodsId":"'+goodsList[i].goodsId+'"}';
            }else {
                privateGoodsId = notifyUrl + '&params={"privateGoodsId":"'+goodsList[i].goodsId+'"}';
            }
        }else{
            privateGoodsId = notifyUrl + '&params={"privateGoodsId":"'+goodsList[i].goodsId+'"}';
        }

        // 锁定分享后的页面
        let oppoR11Share = getUrl("oppoR11Share");
        if (oppoR11Share == "oppoR11Share") {
            // 立即购买按钮
            $("#oppoR11Btn").attr('href', 'javascript:void(0);');
            $("#oppoR11Btn").click(function() {
                layer.open({
                    content: '享12期免息500元福利<br/>有且只在51返呗',
                    btn: ['确认', '取消'],
                    yes: function(){
                        window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
                    }
                });
            });

            // 列表的弹窗
            privateGoodsId = "javascript:void(0);";
            $('#mobileList').on('click','li',function() {
                layer.open({
                    content: '享12期免息500元福利<br/>有且只在51返呗',
                    btn: ['确认', '取消'],
                    yes: function(){
                        window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=com.alfl.www";
                    }
                });
            });
        };

        html+=`<li>
                <a href='${privateGoodsId}'>
                  <img src="${goodsIcon}">
                  <div class="mobileListContent">
                    <p><i></i>${goodsName}</p>
                    <span>¥${saleAmount}</span>
                    <span>${remark}</span>
                  </div>
                </a>
              </li>`;
    }
    return html;
};

// 初始化页面
let page = 0;
window.onload=function(){
    page++;
    $.ajax({
        url: "/app/activity/getSelfSupportGoodsInfo",
        type: "POST",
        dataType: "JSON",
        data: {
            pageNo: page
        },
        success: function(returnData){
            $("#mobileList").append(addMobileListModel(returnData.data.goodsList,returnData.data.notifyUrl));
        },
        error: function(){
            requestMsg("请求失败");
        }
    });
}
