// let userItemsId = getUrl("1234");
let vue=new Vue({
    el:'#login',
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
                url:'/app/user/commitRegister',
                type:'post',
                success:function (data) {
                    console.log(data);
                    // self.tableContent = eval('(' + data + ')');
                    // self.tableContent = self.tableContent.data;
                }
            });
        }
    }
});
