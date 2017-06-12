let modelId=getUrl('modelId');
new Vue({
    el:'#vueCon',
    data:{
        tableUrl:"/fanbei-web/partActivityInfo?modelId="+modelId,
        content:[],
        ht:'#',
        moreHref:'getMore?modelId='+modelId+'&subjectId=',
        divTop:'',
        option:{
        }
    },
    created:function () {
        this.logData();
        window.addEventListener('scroll', this.handleScroll);
    },
    methods:{
        handleScroll (){
            let win=jQuery(window).scrollTop();
            if(win>=this.divTop){
                jQuery('#listAlert').addClass('fixTop');
            }else{
                jQuery('#listAlert').removeClass('fixTop');
            }
        },
        logData (){
            Vue.http.options.emulateJSON = true;
            let self=this;
            let op={data:JSON.stringify(self.option)};
            self.$http.get(self.tableUrl,op).then(function (res) {
                self.content = eval('(' + res.data + ')');
                console.log(self.content);
                self.$nextTick(function () {                              //dom渲染完成后执行
                    self.divTop=document.getElementById('listAlert').offsetTop
                })
            },function (response) {
                console.log(response)
            })
        }
    }
});

