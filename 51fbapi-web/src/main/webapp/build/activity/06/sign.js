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
                self.logData();
                requestMsg(self.msg.msg)
            },function (response) {
                //console.log(response)
            })

            
        },
        selected(data){
            //console.log(this.fixContent)
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
                        timeList.push[timeList[i]]
                    } 
                    self.fixContent.timeList=timeList
                    self.fixContent.currentTime=currentTime;
                    self.fixContent.beginTime=beginTime;
                    
                    self.current=currentTime-beginTime+1;
                    console.log(self.fixContent)

                
            },function (response) {
                //console.log(response)
            })
        }
        
    }
})