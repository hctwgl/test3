/* $(function(){
    $('.credit-two').click(function(){
        window.location.href='https://e.czbank.com/weixinHTML/outInterface/creditCardApplyTemplateOutside.html?apOrigin=1348&pageFlag=a1111';
    })
    $('.credit-three').click(function(){
        window.location.href='https://creditcard.cmbc.com.cn/online/mobile/index.aspx?tradeFrom=YX-BJHE6&EnStr=3A771CB25A6E0B7F273689E71E02B32D&jg=601000001&jgEnStr=E2F54C04A99D8227F0A8DD973DA6A05C';
    })
     $('.credit-five').click(function(){
        window.location.href='http://xyk.cmbchina.com/Latte/card/cardList?WT.mc_id=N3700MMA061V164500BF';
    })
}) */

let vm=new Vue({
    el: '#creditCard',
    data: {},
    created: function () {
        this.theFirst();
    },
    methods: {
        theFirst(){
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {maidianInfo: location.pathname + '?type=creditCard'},
                success: function (data) {
                    console.log(data)
                }
            });
        },
        clickOne(){
            window.location.href = 'https://e.czbank.com/weixinHTML/outInterface/creditCardApplyTemplateOutside.html?apOrigin=1348&pageFlag=a1111';
            //点击加埋点
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {maidianInfo: '/fanbei-web/activity/creditCard?type=zheshang'},
                success: function (data) {
                    console.log(data)
                }
            });
        },
        clickTwo(){
            window.location.href = 'https://ecentre.spdbccc.com.cn/creditcard/indexActivity.htm?data=P2224588';
            //点击加埋点
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {maidianInfo: '/fanbei-web/activity/creditCard?type=pufa'},
                success: function (data) {
                    console.log(data)
                }
            });
        },
        clickThree(){
            window.location.href = 'http://xyk.cmbchina.com/Latte/card/cardList?WT.mc_id=N3700MMA061V164500BF';
            //点击加埋点
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {maidianInfo: '/fanbei-web/activity/creditCard?type=zhaoshang'},
                success: function (data) {
                    console.log(data)
                }
            });
        },
        zhongXinBankClick(){
            window.location.href = 'http://creditcard.ecitic.com/h5/shenqing/index.html?sid=SJUSHALD';
            //点击加埋点
            $.ajax({
                url: '/fanbei-web/postMaidianInfo',
                type: 'post',
                data: {maidianInfo: '/fanbei-web/activity/creditCard?type=zhongxin'},
                success: function (data) {
                    console.log(data)
                }
            });
        }
    }
});
    