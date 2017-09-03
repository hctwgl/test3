var vm=new Vue({
    el: '#billion',
    data: {
        returnData: []
    },
    created:function(){
        let _this=this;
        _this.initial();
        // loading();
    },
    methods:{
        initial(){
            let _this=this;
            $.ajax({
                url: '/app/activity/borrowCashActivities',
                dataType:'json',
                type: 'post',
                success:function (data) {
                    console.log(data);
                    var str=data.data;//获取返回的破十五亿金额
                    console.log(str);
                    var s = str;//将字符串转换成数组
                    var num = s.split("");// 在每个逗号(,)处进行分解。
                    console.log(num);
                    for(var i=0;i<num.length;i++){//对返回的狂送十亿金额进行遍历
                        var index=num.index;
                        num[i]=index;
                        if(num[i]=='.'){
                            num[i]=="";
                        }
                    }
                    
                    ScrollImgLeft();
                    //文字轮播
                    function ScrollImgLeft() {
                        var speed = 50;
                        var MyMar = null;
                        var scroll_begin = document.getElementById("scroll_begin");
                        var scroll_end = document.getElementById("scroll_end");
                        var scroll_div = document.getElementById("scroll_div");
                        scroll_end.innerHTML = scroll_begin.innerHTML;

                        function Marquee() {
                            if (scroll_end.offsetWidth - scroll_div.scrollLeft <= 0)
                                scroll_div.scrollLeft -= scroll_begin.offsetWidth;
                            else
                                scroll_div.scrollLeft++;
                        }
                        MyMar = setInterval(Marquee, speed);
                        scroll_div.onmouseover = function () {　　　　　　　
                            clearInterval(MyMar);　　　　　
                        }
                        scroll_div.onmouseout = function () {　　　　　　　
                            MyMar = setInterval(Marquee, speed);　　　　　　　　　
                        }
                    }

                }
            })

        }, 
    }
});