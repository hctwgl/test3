let modelId=getUrl('modelId');
let sort=getUrl('sort');
let windowW = $(window).outerWidth();
// app调用web的方法
let shareData=[
    {title:'分期购物无忧 3/6/9/12免息',content:'分期无忧 拯救剁手党,90后消费哲学潮这里 我的 我的 都是我的！'},
    {title:'高佣好货聚集618，最高返利50%',content:'全球好货你来淘，佣金我来返。大牌好货爆款，超高额返利尽在嗨购高佣超级券专场。你来淘，我来返！'},
    {title:'联手大牌  满百就减',content:'一份品味逆天的秘籍送给你！不止大牌集群，小众精美好物待你来挖掘~美好生活，触手可及'}
    ];


function alaShareData(){
    let title,con;
    switch (sort){
        case 1:
            title=shareData[0].title;
            con=shareData[0].content;
            break;
        case 2:
            title=shareData[1].title;
            con=shareData[1].content;
            break;
        case 3:
            title=shareData[2].title;
            con=shareData[2].content;
            break;
    }
    // 分享内容
    let dataObj = {
        'appLogin': 'Y', // 是否需要登录，Y需要，N不需要
        'type': 'share', // 此页面的类型
        'shareAppTitle': title,  // 分享的title
        'shareAppContent': con,  // 分享的内容
        'shareAppImage': 'https://fs.51fanbei.com/h5/common/icon/midyearCorner.png',  // 分享右边小图
        'shareAppUrl': 'https://app.51fanbei.com/fanbei-web/activity/feastRaidersShare',  // 分享后的链接
        'isSubmit': 'Y', // 是否需要向后台提交数据，Y需要，N不需要
        'sharePage': 'feastRaidersShare' // 分享的页面
    };
    let dataStr = JSON.stringify(dataObj);  // json数组转换成json对象
    return dataStr;
}
//导航滑动
class Swipe{
    constructor(ele){
        this.container = ele;
        this.element = this.container.children[0];
        this.distance=0;
        this.length = this.element.children.length;
        this.speed = 200;
        //执行对象中的handleEvent函数
        document.getElementById('listAlert').style.width=this.length*1.5+'rem';
        if(this.length>5){
            this.element.addEventListener("touchstart", this);
            this.element.addEventListener("touchmove", this);
        }

    }
    handleEvent(a) {
        switch (a.type) {
            case "touchstart":
                this.onTouchStart(a);
                break;
            case "touchmove":
                this.onTouchMove(a);
                break;
        }
    }
    onTouchStart(a) {
        this.isScrolling = false;
        this.deltaX = 0;
        this.start = {
            pageX: a.touches[0].pageX,
            pageY: a.touches[0].pageY
        };
        this.element.style.MozTransitionDuration = this.element.style.webkitTransitionDuration = this.speed + "ms";
        this.startDistance=this.distance;
        a.stopPropagation()
    }
    onTouchMove(a) {
        if (a.touches.length > 1 || a.scale && a.scale !== 1) {
            return
        }
        this.deltaX = a.touches[0].pageX - this.start.pageX;
        (Math.abs(this.deltaX) > Math.abs(a.touches[0].pageY - this.start.pageY))?this.isScrolling = true:this.isScrolling = false;   //判断横滚还是竖滚
        if (this.isScrolling) {
            this.distance=this.startDistance-this.deltaX;
            if(this.distance<0){
                this.distance=0
            }else if(this.distance>this.element.clientWidth-windowW){
                this.distance=this.element.clientWidth-windowW
            }
            this.element.style.left = -this.distance + "px";
            a.stopPropagation()
        }
    }
}

new Vue({
    el:'#vueCon',
    data:{
        tableUrl:"/fanbei-web/partActivityInfo?modelId="+modelId,
        content:[],
        moreHref:'getMore?modelId='+modelId+'&subjectId=',
        divTop:'',
        option:{
            sort:sort
        }
    },
    created:function () {
        this.logData();
    },
    methods:{
        handleScroll (){
            let win=jQuery(window).scrollTop();
            if(win>=this.divTop){
                jQuery('#navWrap').addClass('fixTop');
            }else{
                jQuery('#navWrap').removeClass('fixTop');
            }
        },
        logData (){
            Vue.http.options.emulateJSON = true;
            let self=this;
            let op={data:JSON.stringify(self.option)};
            self.$http.post(self.tableUrl,op).then(function (res) {
                self.content = eval('(' + res.data + ')');
                console.log(self.content);
                self.$nextTick(function () {                              //dom渲染完成后执行
                    document.title=self.content.data.modelName;
                    if(sort==2){
                        $('#vueCon').css('background-color','#4515aa');
                        $('#listAlert').css('background-color','#4515aa');
                    }
                    self.divTop=document.getElementById('navWrap').offsetTop;
                    if(getBlatFrom()==2){
                        window.addEventListener('touchstart', this.handleScroll);
                        window.addEventListener('touchmove', this.handleScroll);
                        window.addEventListener('touchend', this.handleScroll);
                    }else{
                        window.addEventListener('scroll', this.handleScroll);
                    }
                    new Swipe(document.getElementById('navWrap'));
                })
            },function (response) {
                console.log(response)
            })
        }
    }
});
