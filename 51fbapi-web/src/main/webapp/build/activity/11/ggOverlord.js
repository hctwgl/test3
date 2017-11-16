let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;

//获取数据
let vm = new Vue({
    el: '#ggOverlord',
    data: {
        content: {},
        couponLength:'',
        inviteSumMoney:'',
        baseData:''
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            //页面基本数据初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/inviteFriend",
                success: function (data) {
                    console.log(data);
                    self.baseData=data.data;
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
            //外卖券奖励列表初始化
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/returnCoupon",
                success: function (data) {
                    console.log(data);
                    /*self.content=eval('('+data.data+')');*/
                    self.content=data.data;
                    //console.log(self.content);
                    self.couponLength=self.content.returnCouponList.length;
                    self.content.inviteAmount=self.content.inviteAmount.toString();
                    self.inviteSumMoney=self.content.inviteAmount.split('');
                    self.$nextTick(function () {
                        /*图片预加载*/
                        $(".first").each(function() {
                            var img = $(this);
                            img.load(function () {
                                $(".loadingMask").fadeOut();
                            });
                            setTimeout(function () {
                                $(".loadingMask").fadeOut();
                            },1000)
                        });
                        $(".loadingMask").fadeOut();
                    })
                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            });
        },
        //日期格式转换
        fixDate(date){
            return date.replace(/-/g,'.');
        }
    }
});
//复制邀请码
$(function(){
    let clipboard = new Clipboard('.invitecode');
    clipboard.on('success', function(e) {
        console.log(e);
    });
    $('.copycode').on('click', ()=>{
        alert('已复制到剪贴板，可粘贴');
    })
});