/*
* @Author: yoe
* @Date:   2017-05-22 20:43:19
* @Last Modified by:   yoe
* @Last Modified time: 2017-05-22 20:44:22
*/

var userName = "";
if(getInfo().userName){
    userName=getInfo().userName
}

 new Vue({
    el:'.footer',
    methods:{
        bombBox: function(){ // 规则弹窗

            $.ajax({
	            url: '/fanbei-web/pickBoluomeCoupon',
	            data:{'sceneId':'8166','userName':userName},
	            type: 'POST',
	            success:function (data) {
	                data=eval('(' + data + ')');
	                if(data.success){
	                    requestMsg("领劵成功")
	                }else{
	                    if(data.url){
	                        location.href=data.url;
	                    }else{
	                        requestMsg(data.msg);
	                    }
	                }
	            }
	        });
            
        }
    }