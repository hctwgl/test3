let userItemsId = getUrl("1234");
let vue=new Vue({
    el:'#presents',
    data:{
        content:[],
        tableContent:[]
    },
    created:function () {
        this.logData();
    },
    methods: {
        logData:function(){
            let self=this;
            $.ajax({
                url:'/H5GGShare/initHomePage',
                type:'get',
                data:{'userItemsId':userItemsId},
                success:function (data) {
                    console.log(data);
                    self.tableContent = eval('(' + data + ')');
                    // self.tableContent = self.tableContent.data;
                }
            });
        }
    }
});
