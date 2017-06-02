var data=[
          {'phoneNum':'15666666666','value':'100抵10元优惠券'},
          {'phoneNum':'15777777777','value':'100抵10元优惠券'},
          {'phoneNum':'15888888888','value':'100抵10元优惠券'},
          {'phoneNum':'15999999999','value':'100抵10元优惠券'},
          {'phoneNum':'15000000000','value':'100抵10元优惠券'},
          {'phoneNum':'15111111111','value':'100抵10元优惠券'}
         ]

//数据加载
function addData(x){
    var liCont="";
    for(var i=0;i<x.length;i++){
        liCont+='<li><div class="personImg"></div><h2><span>'+x[i].phoneNum+'</span>获得<span>'+x[i].value+'</span></h2></li>';
    }
    $('#roll ul').append(liCont);
}

//滚轮事件
function AutoScroll(obj) {
    $(obj).find("ul:first").animate({
        marginTop: "-1rem"
    },
    500,
    function() {
        $(this).css({
            marginTop: "0"
        }).find("li:first").appendTo(this);
    });
}

//倒计时事件
function countTime(){
	var currentStamp=Date.parse(new Date());
    var activityStamp=Date.parse("2017/05/30 19:00:00");
    var countDownStamp=(activityStamp-currentStamp)/1000;
    var hours=parseInt(countDownStamp/3600);
    var minutes=parseInt((countDownStamp%3600)/60);
    var seconds=(countDownStamp%3600)%60;
    if(hours<10){
       hours='0'+hours
    }
    if(minutes<10){
       minutes='0'+minutes
    }
    if(seconds<10){
       seconds='0'+seconds
    }
	$("#countDownTime h3").html(hours+":"+minutes+":"+seconds)
}

$(document).ready(function() {
    addData(data);
	setInterval('countTime()',1000)
    setInterval('AutoScroll("#roll")', 1000);
    
});