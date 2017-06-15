/**
 * Created by nizhiwei-labtop on 2017/6/13.
 */
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}
let vm=new Vue({
    el:'#vueCon',
    dataUrl:'/fanbei-web/initActivitySign',
    msgUrl:'/fanbei-web/activitySignIn',
    data:{
        className:['Monday','Tuesday','Wednesday','Thursday','Friday'],
        content:{
            currentTime:'2017-06-12',
            beginTime:'2017-06-09',
            list:['2017-06-10','2017-06-11']
        }
    },
    created:function () {
        this.logData();
    },
    methods:{
        signIn(time){
            //点击签到弹出签到成功信息
            /*let self=this;
            self.$http.get(self.msgUrl).then(function (res) {
                self.data.message = eval('(' + res.data + ')');
                console.log(self.data.message);

            },function (response) {
                console.log(response)
            })*/

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
            //获取页面初始化信息
            let self=this;
            self.$http.get(self.dataUrl).then(function (res) {
                self.data.content = eval('(' + res.data + ')');
                var beginTime=self.data.content.beginTime;
                beginTime=beginTime.replace(/\-/g, "");
                beginTime=parseInt(beginTime);
                console.log(beginTime);

            },function (response) {
                console.log(response)
            })
        }
        
    }
});