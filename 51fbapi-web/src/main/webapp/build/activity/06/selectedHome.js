new Vue({
    el:'#vueCon',
    data:{
        tableUrl:"/app/activity/partActivityInfo.htm",
        content:[],
        option:{
            pageNum:1,
            pageSize:15
        }
    },
    created:function () {
        this.logData()
    },
    methods:{
        logData: function(){
            Vue.http.options.emulateJSON = true;
            let self=this;
            let op={data:JSON.stringify(self.option)};
            self.$http.post(self.tableUrl,op).then(function (res) {
                self.content = res.data;
                console.log(res);
            },function (response) {
                console.log(response)
            })
        }
    }
});

