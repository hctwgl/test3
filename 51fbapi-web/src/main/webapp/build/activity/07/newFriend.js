/**
 * Created by nizhiwei-labtop on 2017/7/10.
 */
let activityId = getUrl("activityId");
let vue=new Vue({
    el:'#vueCon',
    data:{
        content:[],
        tableContent:[]
    },
    created:function () {
        this.logData();
    },
    methods: {
        getCoupon:function (data,type) {
            let self=this;
            let url='pickBoluomeCoupon';
            let postData={"sceneId":data,"userName":self.content.userName};
            if(type=='1'){
                url='pickCoupon';
                postData={couponId:data};
            }
            $.ajax({
                url:"/fanbei-web/"+url,
                type:'post',
                data:postData,
                success:function (res) {
                    res=eval('(' + res+ ')');
                    if(res.success==false){
                        if(res.url&&res.url!==""){
                            window.location.href=res.url
                        }else{
                            requestMsg(res.msg)
                        }
                    }else{
                        requestMsg(res.msg)
                    }
                }
            });
        },
        buyNow(item){
            let self=this;
            if (item.source=="SELFSUPPORT"){
                window.location.href=self.tableContent.notifyUrl+'&params={"privateGoodsId":"'+item.goodsId+'"}'
            }else{
                window.location.href=self.tableContent.notifyUrl+'&params={"goodsId":"'+item.goodsId+'"}'
            }
        },
        txtFix(i){
            function cut_str(str,len){
                var char_length = 0;
                if(str.length<=len){
                    return str
                }else{
                    for (var i = 0; i < str.length; i++){
                        var son_str = str.charAt(i);
                        encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
                        if (char_length >= len){
                            var sub_len = char_length == len ? i+1 : i;
                            return str.substr(0, sub_len);
                            break;
                        }
                    }
                }
            }
            return cut_str(i, 20)
        },
        logData:function(){
            let self=this;
            $.ajax({
                url:'/fanbei-web/encoreActivityInfo',
                type:'post',
                data:{'activityId':activityId},
                success:function (data) {
                    self.tableContent = eval('(' + data + ')');
                    self.tableContent = self.tableContent.data;
                }
            });
            $.ajax({
                url:'/fanbei-web/newUserGift',
                type:'post',
                success:function (data) {
                    self.content = eval('(' + data + ')');
                    self.content = self.content.data;
                }
            });
        }
    }
});
