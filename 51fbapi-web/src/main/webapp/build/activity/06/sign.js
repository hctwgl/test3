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
        content:{},
        fixContent:{},
        current:4
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
            let list=this.fixContent.timeList;
            for(let i in list){
                if (list[i]== data){return true}
            }
            return false;
        },
        logData (){
            //获取页面初始化信息
            let self=this;
            self.$http.post('/fanbei-web/initActivitySign').then(function (res) {
                self.content =  eval('(' + res.data + ')');
                var contentData=self.content.data;

                let currentTime=contentData.currentDate;
                    currentTime=currentTime.replace(/\-/g, ""); 
                    currentTime=parseInt(currentTime);
                let beginTime=contentData.startDate;
                    beginTime=beginTime.replace(/\-/g, ""); 
                    beginTime=parseInt(beginTime);
                let timeList=contentData.resultList;
                for(let i=0;i<timeList.length;i++){
                    timeList[i]=timeList[i].replace(/\-/g, ""); 
                    timeList[i]=parseInt(timeList[i]);  
                }  
                  
                self.fixContent=contentData;
                self.current=new Date(contentData.currentDate).getDay();
                console.log(self.current)

            },function (response) {
                console.log(response)
            })
        },
        /*fixData(contentData){
            let currentTime=contentData.currentDate;
                currentTime=currentTime.replace(/\-/g, ""); 
                currentTime=parseInt(currentTime);
            let beginTime=contentData.startDate;
                beginTime=beginTime.replace(/\-/g, ""); 
                beginTime=parseInt(beginTime);
            let timeList=contentData.resultList;
            for(let i=0;i<timeList.length;i++){
                  timeList[i]=timeList[i].replace(/\-/g, ""); 
                  timeList[i]=parseInt(timeList[i]);  
            }    
                self.fixContent=contentData;
                console.log(contentData)
                
                return self.fixContent
        }*/
        
    }
});