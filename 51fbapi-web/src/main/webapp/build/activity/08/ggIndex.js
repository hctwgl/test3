var liLength=$('.imgList li').length;
var liWidth=2.73+'rem';
var move;
var i=0;
var ulWidth=(2.73+0.4)*liLength-0.4+'rem';
$('.imgList li').width(liWidth);
$('.imgList').width(ulWidth);
//获取数据
var vm = new Vue({
    el: '#ggIndex',
    data: {
        content: {},
        unbind:false
    },
    created: function () {
        //this.logData();
    },
    methods: {
        imgClick:function(){
            var self=this;
            i++;
            move=3.13*i+'rem';
            $('.imgList').css('left','-'+move);
            console.log(i)
            $('.imgList').find('li').eq(i).removeClass('change');
            $('.imgList').find('li').eq(i+1).addClass('change');
            if(i>=liLength-2){
                self.unbind=true;
            }
        }
    }
})


