//滚轮事件
/*function AutoScroll(obj,x) {
    $(obj).find("ul:first").animate({
        marginTop: x
    },
    500,
    function() {
        $(this).css({
            marginTop: "0"
        }).find("li:first").appendTo(this);
    });
}*/


$(document).ready(function() {
    //setInterval('AutoScroll(".prizeName","-0.75rem")', 1300);
    $.ajax({
        url:'/fanbei-web/tearPacketAwardList',
        type:'post',
        dataType: "JSON",
        success:function(data){
           var prizeData=data.data.awordList
           //console.log(prizeData);
           for(var i=0;i<prizeData.length;i++){
              var str='<li><p><span>'+prizeData[i].mobile+'</span><span style="-webkit-box-orient: vertical;">获得'+prizeData[i].prizeName+'</span></p></li>';
              $('.roll').append(str);
           }          
        },
        error:function(){
            requestMsg("请求失败");
        }
    })
    //点击去借钱页面
    $('.toBorrowMoney').click(function(){
        window.location.href="/fanbei-web/opennative?name=BORROW_MONEY";
    })
});

//1.速度
var speed = 30;
//2.复制 demo1-->demo2
var cont = $(".roll").html();
$("#copyRoll").html(cont); 
//3:创建方法定时执行  
function hello(){
   var t = $(".prizeName").scrollTop();
   if(t >= $(".roll").height()){
    t = 0;
   }else{
    t++;
   }
   $(".prizeName").scrollTop(t);  
   setTimeout('hello()',speed); 
}
hello()