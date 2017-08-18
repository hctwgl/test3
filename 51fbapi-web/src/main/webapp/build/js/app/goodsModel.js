/**
 * Created by nizhiwei-labtop on 2017/8/16.
 */
let activityId=getUrl('activityId');
let windowW = $(window).outerWidth();
// // app调用web的方法
// let shareData=[
//     {title:'分期购物无忧 3/6/9/12免息',content:'分期无忧 拯救剁手党,90后消费哲学潮这里 我的 我的 都是我的！'},
//     {title:'高佣好货聚集618，最高返利50%',content:'全球好货你来淘，佣金我来返。大牌好货爆款，超高额返利尽在嗨购高佣超级券专场。你来淘，我来返！'},
//     {title:'联手大牌  满百就减',content:'一份品味逆天的秘籍送给你！不止大牌集群，小众精美好物待你来挖掘~美好生活，触手可及'}
// ];
// function alaShareData(){
//     let title,con;
//     switch (sort){
//         case '1':
//             title=shareData[0].title;
//             con=shareData[0].content;
//             break;
//         case '2':
//             title=shareData[1].title;
//             con=shareData[1].content;
//             break;
//         case '3':
//             title=shareData[2].title;
//             con=shareData[2].content;
//             break;
//     }
//     // 分享内容
//     let dataObj = {
//         'appLogin': 'Y', // 是否需要登录，Y需要，N不需要
//         'type': 'share', // 此页面的类型
//         'shareAppTitle': title,  // 分享的title
//         'shareAppContent': con,  // 分享的内容
//         'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
//         'shareAppUrl': 'https://app.51fanbei.com/fanbei-web/activity/feastRaidersShare',  // 分享后的链接
//         'isSubmit': 'Y', // 是否需要向后台提交数据，Y需要，N不需要
//         'sharePage': 'feastRaidersShare' // 分享的页面
//     };
//     let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
//     return dataStr;
// }

new Vue({
    el:'#vueCon',
    data:{
        tableUrl:"/fanbei-web/newEncoreActivityInfo",
        content:[]
    },
    created:function () {
        this.logData();
    },
    methods:{
        buttonTxt(data){
            if(data.doubleRebate=='Y'){
                return "双倍返:￥"+(data.rebateAmount/2)+'×2'
            }else if(data.nperMap){
                if(data.nperMap.isFree==1){
                    return '月供:￥'+data.nperMap.freeAmount+'起'
                }else{
                    return '月供:￥'+data.nperMap.amount+'起'
                }
            }else{
                return '立即购买'
            }
        },
        priceTxt(data){
          if(data.remark){
              return data.remark
          }else{
              return '原价'
          }
        },
        logData (){
            let self=this;
            $.ajax({
                url:self.tableUrl,
                type:'post',
                data:{activityId:activityId},
                success:function (res) {
                    self.content = JSON.parse(res);
                    console.log(self.content);
                    self.$nextTick(function () {                              //dom渲染完成后执行
                       document.title=self.content.data.title;
                        $('body').css('background',self.content.data.bgColor);
                       $('.monthPrice').css('background',self.content.data.btnColor);
                        lazy.init()
                    })
                }
            })
        }
    }
});
