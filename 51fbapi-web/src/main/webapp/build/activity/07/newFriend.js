/**
 * Created by nizhiwei-labtop on 2017/7/10.
 */
let activityId = getUrl("activityId");
let vue=new Vue({
    el:'#vueCon',
    data:{
        coupons:[
            '-44deg, #fc858a 0%, #feb39e 100%'
            ,'135deg, #fdcae5 0%, #fd9ea3 100%'
            ,'-45deg, #fa9054 0%, #fdc98b 100%'
            ,'-43deg, #3298f3 0%, #77f2f8 100%'
            ,'-44deg, #ff517c 0%, #ffb199 100%'
            ,'-44deg, #c357f8 0%, #afd9fe 100%'
        ],
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
            // $.ajax({
            //     url:'/fanbei-web/'+url,
            //     type:'post',
            //     data:postData,
            //     success:function (data) {
            //         requestMsg(data.msg)
            //     }
            // });
            Vue.http.options.emulateJSON = true;
            self.$http.post("/fanbei-web/"+url,postData).then(function(res){
                res=eval('(' + res.data + ')');
                if(res.success=='false'){
                    window.location.href=res.url
                }else{
                    requestMsg(res.msg)
                }
            },function (response) {
                console.log(response)
            })
        },
        buyNow(id){
            let self=this;
            window.location.href=self.tableContent.notifyUrl+'&params={"goodsId":"'+id+'"}'
        },
        txtFix(i){
            function get_length(s){
                var char_length = 0;
                for (var i = 0; i < s.length; i++){
                    var son_char = s.charAt(i);
                    encodeURI(son_char).length > 2 ? char_length += 1 : char_length += 0.5;
                }
                return char_length;
            }
            function cut_str(str, len){
                var char_length = 0;
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
                    console.log(self.tableContent)
                }
            });

            self.$http.post("/fanbei-web/newUserGift").then(function(res){
                self.content = eval('(' + res.data + ')');
                self.content = self.content.data;
                console.log(self.content);
            },function (response) {
                requestMsg("请求失败");
            })
        }
    }
});
