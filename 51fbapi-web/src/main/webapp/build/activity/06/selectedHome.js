new Vue({
    el:'#vueCon',
    data:{
        tableUrl:"/fanbei-web/partActivityInfo?modelId=68",
        content:[],
        ht:'#',
        option:{
            modelId:getUrl('modelId')
        }
    },
    created:function () {
        this.logData();

    },
    ready:function () {
        window.addEventListener('scroll', this.handleScroll);
    },
    methods:{
        handleScroll:function () {
            // jQuery(window).scroll(function () {
                let win=jQuery(window).scrollTop();
                let new_top=jQuery('.listAlert').offset().top;
                if(win>=new_top){
                    jQuery('.listAlert').addClass('fixTop');
                }else{
                    jQuery('.listAlert').removeClass('fixTop');
                }
            // });
        },
        logData: function(){
            Vue.http.options.emulateJSON = true;
            let self=this;
            let op={data:JSON.stringify(self.option)};
            self.$http.get(self.tableUrl,op).then(function (res) {
                self.content = eval('(' + res.data + ')');
                console.log(self.content);
                console.log(self.content.data.bannerImage);
            },function (response) {
                console.log(response)
            })
        }
    }
});

