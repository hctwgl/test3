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
        tableUrl:"/fanbei-web/partActivityInfo?modelId=",
        className:['Monday','Tuesday','Wednesday','Thursday','Friday'],
        content:{
            currentTime:20170615,
            beginTime:20170612,
            list:[
                {time:20170613},
                {time:20170614}
            ]
        },
        beginTime:20170609,
        current:3,
        option:{
        }
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
            console.log(data);
            console.log(list.indexOf(data));
            for(let i in list){
                if (list[i] == data){return true}
            }
            return false;
        },
        logData (){
            let self=this;
            self.$http.get(self.tableUrl,self.option).then(function (res) {
                self.content = eval('(' + res.data + ')');
                console.log(self.content);
                self.$nextTick(function () {                              //dom渲染完成后执行

                })
            },function (response) {
                console.log(response)
            })
        }
    }
});