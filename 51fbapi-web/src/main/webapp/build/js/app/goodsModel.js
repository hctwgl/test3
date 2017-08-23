/**
 * Created by nizhiwei-labtop on 2017/8/16.
 */
let activityId=getUrl('activityId');

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
                        $('body').css('background',self.content.data.bgColor);
                        $('.monthPrice').css('background',self.content.data.btnColor);
                        $("img.lazy").lazyload({
                            placeholder : "http://f.51fanbei.com/h5/common/images/bitmap1.png",  //用图片提前占位
                            effect : "fadeIn",  // 载入使用的效果
                            threshold: 200 // 提前开始加载
                        });
                    })
                }
            })
        }
    }
});
