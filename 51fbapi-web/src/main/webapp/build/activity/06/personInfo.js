 $("#city-picker").cityPicker(function(){
    toolbarTemplate: '<header class="bar bar-nav">\
    <button class="button button-link pull-right close-picker">OK</button>\
    <h1 class="title">choose address</h1>\
    </header>'
  }
  );

function button(){
        var personName=$('#cont1').val();
        var personNumber=$('#cont2').val();
        var personAddress=$('#cont3').val();
    	   $.ajax({
            url: "/fanbei-web/submitContract",
            type: "POST",
            dataType: "JSON",
            data: {
                name: personName,
                mobilePhone: personNumber,
                address:personAddress              
            },
            success: function(data){
            	if(data.success){
            		requestMsg("提交成功");
            		location.href="game";
            	} else{
            		requestMsg("参数异常")
            	}
            },
            error: function(){
                requestMsg("请求失败");
            }
        });

    }

//获取图片地址
function img(){
    var url=location.href;
    var s=url.indexOf("?");
    var imgSrc=url.substring(s+5);
    var str='<img src="'+imgSrc+'">';
    $('.prizeImg').append(str);   
}
img()
