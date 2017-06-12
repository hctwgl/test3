let modelId=getUrl('modelId');
let windowW = $(window).outerWidth();
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
        this.element.addEventListener("touchstart", this);
        this.element.addEventListener("touchmove", this);
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
        ht:'#',
        moreHref:'getMore?modelId='+modelId+'&subjectId=',
        divTop:'',
        option:{
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
            self.$http.get(self.tableUrl,op).then(function (res) {
                self.content = eval('(' + res.data + ')');
                console.log(self.content);
                self.$nextTick(function () {                              //dom渲染完成后执行
                    self.divTop=document.getElementById('navWrap').offsetTop;
                    window.addEventListener('scroll', this.handleScroll);
                    new Swipe(document.getElementById('navWrap'));
                })
            },function (response) {
                console.log(response)
            })
        }
    }
});



