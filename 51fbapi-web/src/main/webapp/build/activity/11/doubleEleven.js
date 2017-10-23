let vm=new Vue({
    el:'#doubleEleven',
    data:{
        content: '',
        isShow:true
    },
    created:function(){
         this.logData();
    },
    methods:{
        //页面初始化信息
         logData() {
            let self = this;
            $.ajax({
                type: 'post',
                url: "/fanbei-web/partActivityInfo",
                data:{'modelId':modelId},
                success: function (data) {
                    data = eval('(' + data + ')')
                    self.content =  data.data;
                    var m = self.content.qualityGoodsList;
                    var c = JSON.stringify(m)
                    m = JSON.parse(c)
                    self.renderdata = m
                    self.list=self.content.qualityGoodsList;

                },
                error:function(){
                    requestMsg('哎呀，出错了！')
                }
            })
        },
        noShow(){
                this.isShow=!this.isShow;
                if(this.isShow){
                    $('.processBar').show();
                }else{
                    $('.processBar').hide();
                } 
        }
    }
})