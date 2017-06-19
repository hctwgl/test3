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
        okContent:[
            false,
            false,
            false,
            false,
            false
            ],
        current:'',
        msg:{}
    },
    created:function () {
        this.logData();
    },
    methods:{
        signIn(time){
            //点击签到弹出签到成功信息
            let self=this;
            self.$http.post('/fanbei-web/activitySignIn').then(function (res) {
                self.msg= eval('(' + res.data + ')');     
                requestMsg(self.msg.msg);
                self.logData();
            },function (response) {
                //console.log(response)
            })
            
        },
        logData (){
            //获取页面初始化信息
            let self=this;
            self.$http.post('/fanbei-web/initActivitySign').then(function (res) {
                    
                    self.content =  eval('(' + res.data + ')');
                    let contentData=self.content.data;
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
                        if(timeList[i]>=beginTime&&timeList[i]<=currentTime){
                            timeList.push[timeList[i]];
                        }                       
                    } 
                    self.fixContent.timeList=timeList
                    self.fixContent.currentTime=currentTime;
                    self.fixContent.beginTime=beginTime;
                    self.current=currentTime-beginTime+1;
                    //console.log(self.fixContent)
 for(let i in self.fixContent.timeList){
    self.okContent.splice(self.fixContent.timeList[i]-self.fixContent.beginTime,1,true)
        }            
            },function (response) {
                //console.log(response)
            })
        }
        
    }
})