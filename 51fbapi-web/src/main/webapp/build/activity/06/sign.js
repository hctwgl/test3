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
            currentTime:20170612,
            beginTime:20170609,
            list:[
                {time:20170610},
                {time:20170611}
            ]
        },
        beginTime:'',
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
        },
        logData (){
            Vue.http.options.emulateJSON = true;
            let self=this;
            let op={data:JSON.stringify(self.option)};
            self.$http.get(self.tableUrl,op).then(function (res) {
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