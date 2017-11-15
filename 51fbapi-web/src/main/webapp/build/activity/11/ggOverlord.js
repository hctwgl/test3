let protocol = window.location.protocol;//域名
let host = window.location.host;
let domainName = protocol+'//'+host;

//获取数据
let vm = new Vue({
    el: '#ggOverlord',
    data: {
        content: {},
        couponLength:'',
        inviteSumMoney:''
    },
    created: function () {
        this.logData();
    },
    methods: {
        //获取页面初始化信息
        logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/h5GgActivity/returnCoupon",
                success: function (data) {
                    console.log(data);
                    /*self.content=eval('('+data.data+')');*/
                    self.content=data.data;
                    console.log(self.content);
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
