/**
 * Created by nizhiwei-labtop on 2017/6/13.
 */
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}
let vm=new Vue({
    el:'#vueCon',
    data:{
        content:''
    },
    created:function () {
        this.logData();
    },
    methods:{
        signIn (time){
            console.log(time);
            this.logData();
        },
        selected(data){
            let list=this.content.list;
            for(let i in list){
                if (list[i].time == data){return true}
            }
            return false;
        },
        logData (){
            let self=this;
            self.$http.get(self.tableUrl).then(function (res) {
                self.content = eval('(' + res.data + ')');
                console.log(self.content);

            },function (response) {
                console.log(response)
            })
        }
        
    }
});